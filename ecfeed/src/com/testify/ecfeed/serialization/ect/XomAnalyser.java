/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.serialization.ect;

import static com.testify.ecfeed.serialization.ect.Constants.*;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.serialization.ParserException;
import com.testify.ecfeed.utils.ModelUtils;

public class XomAnalyser {
	public RootNode parseRoot(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), ROOT_NODE_NAME);
		String name = getElementName(element);
		
		RootNode root = new RootNode(name);
		
		for(Element child : getIterableChildren(element)){
			root.addClass(parseClass(child));
		}
		
		return root;
	}
	
	public ClassNode parseClass(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), CLASS_NODE_NAME);
		String name = getElementName(element);
		
		ClassNode _class = new ClassNode(name);
		
		for(Element child : getIterableChildren(element)){
			_class.addMethod(parseMethod(child));
		}
		
		return _class;
	}

	public MethodNode parseMethod(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), METHOD_NODE_NAME);
		String name = getElementName(element);
		
		MethodNode method = new MethodNode(name);
		
		for(Element child : getIterableChildren(element)){
			if(child.getLocalName() == Constants.CATEGORY_NODE_NAME){
				method.addCategory(parseCategory(child));
			}
			
			else if(child.getLocalName() == Constants.TEST_CASE_NODE_NAME){
				method.addTestCase(parseTestCase(child, method));
			}

			else if(child.getLocalName() == Constants.CONSTRAINT_NODE_NAME){
				method.addConstraint(parseConstraint(child, method));
			}
			else{
				throw new ParserException(Messages.WRONG_CHILD_ELEMENT_TYPE(element, child.getLocalName()));
			}
		}
		
		return method;
	}

	public CategoryNode parseCategory(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), CATEGORY_NODE_NAME);
		String name = getElementName(element);
		String type = getAttributeValue(element, TYPE_NAME_ATTRIBUTE);
		String defaultValue = ModelUtils.getDefaultExpectedValueString(type);
		String expected = String.valueOf(false);
		if(element.getAttribute(CATEGORY_IS_EXPECTED_ATTRIBUTE_NAME) != null){
			expected = getAttributeValue(element, CATEGORY_IS_EXPECTED_ATTRIBUTE_NAME);
			defaultValue = getAttributeValue(element, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME);
		}
		CategoryNode category = new CategoryNode(name, type, defaultValue, Boolean.parseBoolean(expected));
		
		for(Element child : getIterableChildren(element)){
			category.addPartition(parsePartition(child));
		}
		
		return category;
	}
	
	public TestCaseNode parseTestCase(Element element, MethodNode method) throws ParserException{
		assertNodeTag(element.getQualifiedName(), TEST_CASE_NODE_NAME);
		String name = getAttributeValue(element, TEST_SUITE_NAME_ATTRIBUTE);

		List<Element> parameterElements = getIterableChildren(element);
		List<CategoryNode> categories = method.getCategories();
		
		List<PartitionNode> testData = new ArrayList<PartitionNode>();
		
		if(categories.size() != parameterElements.size()){
			throw new ParserException(Messages.WRONG_NUMBER_OF_TEST_PAREMETERS(name));
		}

		for(int i = 0; i < parameterElements.size(); i++){
			Element testParameterElement = parameterElements.get(i);
			CategoryNode category = categories.get(i);
			PartitionNode testValue = null;

			if(testParameterElement.getLocalName().equals(Constants.TEST_PARAMETER_NODE_NAME)){
				String partitionName = getAttributeValue(testParameterElement, Constants.PARTITION_ATTRIBUTE_NAME);
				testValue = category.getPartition(partitionName);
				if(testValue == null){
					throw new ParserException(Messages.PARTITION_DOES_NOT_EXIST(category.getName(), partitionName));
				}
			}
			else if(testParameterElement.getLocalName().equals(Constants.EXPECTED_PARAMETER_NODE_NAME)){
				String valueString = getAttributeValue(testParameterElement, Constants.VALUE_ATTRIBUTE_NAME);
				if(valueString == null){
					throw new ParserException(Messages.MISSING_VALUE_ATTRIBUTE_IN_TEST_CASE_ELEMENT);
				}
				testValue = new PartitionNode(Constants.EXPECTED_VALUE_PARTITION_NAME, valueString);
				testValue.setParent(category);
			}
			testData.add(testValue);
		}
		
		return new TestCaseNode(name, testData);
	}
	
	public ConstraintNode parseConstraint(Element element, MethodNode method) throws ParserException{
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_NODE_NAME);
		String name = getElementName(element);
		
		
		BasicStatement premise = null;
		BasicStatement consequence = null;
		for(Element child : getIterableChildren(element)){
			if(child.getLocalName().equals(Constants.CONSTRAINT_PREMISE_NODE_NAME)){
				if(getIterableChildren(child).size() == 1){
					//there is only one statement per premise or consequence that is either
					//a single statement or statement array
					premise = parseStatement(child.getChildElements().get(0), method);
				}
				else{
					throw new ParserException(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
				}
			}
			else if(child.getLocalName().equals(Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME)){
				if(getIterableChildren(child).size() == 1){
					consequence = parseStatement(child.getChildElements().get(0), method);
				}
				else{
					throw new ParserException(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
				}
			}
			else{
				throw new ParserException(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
			}
		}
		if(premise == null || consequence == null){
			throw new ParserException(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
		}
		return new ConstraintNode(name, new Constraint(premise, consequence));
	}
	
	public BasicStatement parseStatement(Element element, MethodNode method) throws ParserException {
		switch(element.getLocalName()){
		case Constants.CONSTRAINT_PARTITION_STATEMENT_NODE_NAME:
			return parsePartitionStatement(element, method);
		case Constants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME:
			return parseLabelStatement(element, method);
		case Constants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME:
			return parseStatementArray(element, method);
		case Constants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME:
			return parseStaticStatement(element);
		case Constants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME:
			return parseExpectedValueStatement(element, method);
		default: return null;
		}
	}

	public StatementArray parseStatementArray(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_STATEMENT_ARRAY_NODE_NAME);

		StatementArray statementArray = null;
		String operatorValue = getAttributeValue(element, Constants.STATEMENT_OPERATOR_ATTRIBUTE_NAME);
		switch(operatorValue){
		case Constants.STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(Operator.OR);
			break;
		case Constants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(Operator.AND);
			break;
		default: 
			throw new ParserException(Messages.WRONG_STATEMENT_ARRAY_OPERATOR(method.getName(), operatorValue));
		}
		for(Element child : getIterableChildren(element)){
			BasicStatement childStatement = parseStatement(child, method);
			if(childStatement != null){
				statementArray.addStatement(childStatement);
			}
		}
		return statementArray;
	}

	public StaticStatement parseStaticStatement(Element element) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_STATIC_STATEMENT_NODE_NAME);

		String valueString = getAttributeValue(element, Constants.STATIC_VALUE_ATTRIBUTE_NAME);
		switch(valueString){
		case Constants.STATIC_STATEMENT_TRUE_VALUE:
			return new StaticStatement(true);
		case Constants.STATIC_STATEMENT_FALSE_VALUE:
			return new StaticStatement(false);
		default:
			throw new ParserException(Messages.WRONG_STATIC_STATEMENT_VALUE(valueString));
		}
	}
	
	public PartitionedCategoryStatement parsePartitionStatement(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_PARTITION_STATEMENT_NODE_NAME);

		String categoryName = getAttributeValue(element, Constants.STATEMENT_CATEGORY_ATTRIBUTE_NAME);
		CategoryNode category = method.getCategory(categoryName);
		if(category == null || category.isExpected()){
			throw new ParserException(Messages.WRONG_CATEGORY_NAME(categoryName, method.getName()));
		}
		String partitionName = getAttributeValue(element, Constants.STATEMENT_PARTITION_ATTRIBUTE_NAME);
		PartitionNode partition = category.getPartition(partitionName);
		if(partition == null){
			throw new ParserException(Messages.WRONG_PARTITION_NAME(categoryName, method.getName()));
		}
	
		String relationName = getAttributeValue(element, Constants.STATEMENT_RELATION_ATTRIBUTE_NAME);
		Relation relation = getRelation(relationName);
		
		return new PartitionedCategoryStatement(category, relation, partition); 
	}

	public PartitionedCategoryStatement parseLabelStatement(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_LABEL_STATEMENT_NODE_NAME);

		String categoryName = getAttributeValue(element, Constants.STATEMENT_CATEGORY_ATTRIBUTE_NAME);
		String label = getAttributeValue(element, Constants.STATEMENT_LABEL_ATTRIBUTE_NAME);
		String relationName = getAttributeValue(element, Constants.STATEMENT_RELATION_ATTRIBUTE_NAME);
		
		CategoryNode category = method.getCategory(categoryName);
		if(category == null || category.isExpected()){
			throw new ParserException(Messages.WRONG_CATEGORY_NAME(categoryName, method.getName()));
		}
		Relation relation = getRelation(relationName);
		
		return new PartitionedCategoryStatement(category, relation, label);
	}

	public ExpectedValueStatement parseExpectedValueStatement(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME);

		String categoryName = getAttributeValue(element, Constants.STATEMENT_CATEGORY_ATTRIBUTE_NAME);
		String valueString = getAttributeValue(element, Constants.STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME);
		CategoryNode category = method.getCategory(categoryName);
		if(category == null || !category.isExpected()){
			throw new ParserException(Messages.WRONG_CATEGORY_NAME(categoryName, method.getName()));
		}
		PartitionNode condition = new PartitionNode("expected", valueString);
		condition.setParent(category);
		
		return new ExpectedValueStatement(category, condition);
	}

	public PartitionNode parsePartition(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), PARTITION_NODE_NAME);
		String name = getElementName(element);
		String value = getAttributeValue(element, VALUE_ATTRIBUTE);
		
		PartitionNode partition = new PartitionNode(name, value);
		
		for(Element child : getIterableChildren(element)){
			if(child.getLocalName() == Constants.PARTITION_NODE_NAME){
				partition.addPartition(parsePartition(child));
			}
			if(child.getLocalName() == Constants.LABEL_NODE_NAME){
				partition.addLabel(child.getAttributeValue(Constants.LABEL_ATTRIBUTE_NAME));
			}
		}

		return partition;
	}

	private void assertNodeTag(String qualifiedName, String expectedName) throws ParserException {
		if(qualifiedName.equals(expectedName) == false){
			throw new ParserException("Unexpected node name: " + qualifiedName + " instead of " + expectedName);
		}
	}
	
	protected List<Element> getIterableChildren(Element element){
		ArrayList<Element> list = new ArrayList<Element>();
		Elements children = element.getChildElements();
		for(int i = 0; i < children.size(); i++){
			Node node = children.get(i);
			if(node instanceof Element){
				list.add((Element)node);
			}
		}
		return list;
	}

	protected String getElementName(Element element) throws ParserException {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if(name == null){
			throw new ParserException(Messages.MISSING_ATTRIBUTE(element, Constants.NODE_NAME_ATTRIBUTE));
		}
		return name;
	}

	protected String getAttributeValue(Element element, String attributeName) throws ParserException{
		String value = element.getAttributeValue(attributeName);
		if(value == null){
			throw new ParserException(Messages.MISSING_ATTRIBUTE(element, attributeName));
		}
		return value;
	}

	protected Relation getRelation(String relationName) throws ParserException{
		Relation relation = null;
		switch(relationName){
		case Constants.RELATION_EQUAL:
			relation = Relation.EQUAL;
			break;
		case Constants.RELATION_NOT:
		case Constants.RELATION_NOT_ASCII:
			relation = Relation.NOT;
			break;
		default:
			throw new ParserException(Messages.WRONG_OR_MISSING_RELATION_FORMAT(relationName));
		}
		return relation;
	}

}
