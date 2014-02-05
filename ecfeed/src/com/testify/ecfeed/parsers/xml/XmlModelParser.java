/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.parsers.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.*;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.PartitionStatement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.parsers.IModelParser;
import com.testify.ecfeed.parsers.ParserException;
import com.testify.ecfeed.parsers.Constants;

public class XmlModelParser implements IModelParser{
	
	public RootNode parseModel(InputStream istream) throws ParserException{
		try {
			Builder parser = new Builder();
			Document document = parser.build(istream);
			if(document.getRootElement().getLocalName() == Constants.ROOT_NODE_NAME){
				return parseRootElement(document.getRootElement());
			}
			else{
				throw new ParserException(Messages.WRONG_ROOT_ELEMENT_TAG);
			}
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	protected RootNode parseRootElement(Element element) throws ParserException {
		String name = getElementName(element);
		RootNode rootNode = new RootNode(name);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.CLASS_NODE_NAME){
				rootNode.addClass(parseClassElement(child));
			}
			else{
				throw new ParserException(Messages.WRONG_CHILD_ELEMENT_TYPE(element, child.getLocalName()));
			}
		}
		return rootNode;
	}

	protected ClassNode parseClassElement(Element element) throws ParserException {
		String qualifiedName = getElementName(element);

		ClassNode classNode = new ClassNode(qualifiedName);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.METHOD_NODE_NAME){
				classNode.addMethod(parseMethodElement(child));
			}
			else{
				throw new ParserException(Messages.WRONG_CHILD_ELEMENT_TYPE(element, child.getLocalName()));
			}
		}
		return classNode;
	}

	protected MethodNode parseMethodElement(Element element) throws ParserException {
		String name = getElementName(element);
		MethodNode methodNode = new MethodNode(name);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.EXPECTED_VALUE_CATEGORY_NODE_NAME){
				methodNode.addCategory(parseExpectedValueCategoryElement(child));
			}
			else if(child.getLocalName() == Constants.CATEGORY_NODE_NAME){
				methodNode.addCategory(parseCategoryElement(child));
			}
			else if(child.getLocalName() == Constants.TEST_CASE_NODE_NAME){
				methodNode.addTestCase(parseTestCaseElement(child, methodNode.getCategories()));
			}
			else if(child.getLocalName() == Constants.CONSTRAINT_NODE_NAME){
				methodNode.addConstraint(parseConstraintElement(child, methodNode));
			}
			else{
				throw new ParserException(Messages.WRONG_CHILD_ELEMENT_TYPE(element, child.getLocalName()));
			}
		}
		return methodNode;
	}
	
	protected ConstraintNode parseConstraintElement(Element element, MethodNode method) throws ParserException {
		String name = getElementName(element);
		BasicStatement premise = null;
		BasicStatement consequence = null;
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName().equals(Constants.CONSTRAINT_PREMISE_NODE_NAME)){
				if(getIterableElements(child.getChildElements()).size() == 1){
					//there is only one statement per premise or consequence that is either
					//a single statement or statement array
					premise = parseStatement(child.getChildElements().get(0), method);
				}
				else{
					throw new ParserException(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
				}
			}
			else if(child.getLocalName().equals(Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME)){
				if(getIterableElements(child.getChildElements()).size() == 1){
//					if(child.getChildCount() == 0){
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

	protected BasicStatement parseStatement(Element element, MethodNode method) throws ParserException {
		switch(element.getLocalName()){
		case Constants.CONSTRAINT_STATEMENT_NODE_NAME:
			return parseSigleStatement(element, method);
		case Constants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME:
			return parseStatementArray(element, method);
		case Constants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME:
			return parseStaticStatement(element);
		default: return null;
		}
	}

	protected BasicStatement parseStatementArray(Element element, MethodNode method) throws ParserException {
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
		for(Element child : getIterableElements(element.getChildElements())){
			BasicStatement childStatement = parseStatement(child, method);
			if(childStatement != null){
				statementArray.addStatement(childStatement);
			}
		}
		return statementArray;
	}

	protected BasicStatement parseStaticStatement(Element element) throws ParserException {
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

	protected BasicStatement parseSigleStatement(Element element, MethodNode method) throws ParserException {
		String categoryName = getAttributeValue(element, Constants.STATEMENT_CATEGORY_ATTRIBUTE_NAME);
		CategoryNode category = method.getCategory(categoryName);
		if(category == null){
			throw new ParserException(Messages.WRONG_CATEGORY_NAME(categoryName, method.getName()));
		}
		String partitionName = getAttributeValue(element, Constants.STATEMENT_PARTITION_ATTRIBUTE_NAME);
		PartitionNode partition = category.getPartition(partitionName);
		if(partition == null){
			throw new ParserException(Messages.WRONG_PARTITION_NAME(categoryName, method.getName()));
		}

		String relationName = getAttributeValue(element, Constants.STATEMENT_RELATION_ATTRIBUTE_NAME);
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
			throw new ParserException(Messages.WRONG_OR_MISSING_RELATION_FORMAT(method.getName(), relationName));
		}
		
		return new PartitionStatement(partition, relation);
	}

	protected TestCaseNode parseTestCaseElement(Element element, List<CategoryNode> categories) throws ParserException {
		String testSuiteName = getAttributeValue(element, Constants.TEST_SUITE_NAME_ATTRIBUTE);
		ArrayList<PartitionNode> testData = new ArrayList<PartitionNode>();
		List<Element> parameterElements = getIterableElements(element.getChildElements());
		
		if(categories.size() != parameterElements.size()){
			throw new ParserException(Messages.WRONG_TEST_PAREMETERS_NUMBER(testSuiteName));
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
				Object value = parseValue(valueString, category.getType());
				testValue = new PartitionNode(Constants.EXPECTED_VALUE_PARTITION_NAME, value);
				testValue.setParent(category);
			}
			testData.add(testValue);
		}
		return new TestCaseNode(testSuiteName, testData);
	}

	protected ExpectedValueCategoryNode parseExpectedValueCategoryElement(Element element) throws ParserException {
		String name = getElementName(element);
		String type = getAttributeValue(element, Constants.TYPE_NAME_ATTRIBUTE);
		String defaultValueString = getAttributeValue(element, Constants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE);
		Object defaultValue = parseValue(defaultValueString, type);
		return new ExpectedValueCategoryNode(name, type, defaultValue);
	}
	
	protected CategoryNode parseCategoryElement(Element element) throws ParserException {
		String name = getElementName(element);
		String type = getAttributeValue(element, Constants.TYPE_NAME_ATTRIBUTE);

		CategoryNode categoryNode = new CategoryNode(name, type);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.PARTITION_NODE_NAME){
				categoryNode.addPartition(parsePartitionElement(child, type));
			}
		}
		return categoryNode;
	}

	protected PartitionNode parsePartitionElement(Element element, String typeSignature) throws ParserException {
		String name = getElementName(element);
		String valueString = getAttributeValue(element, Constants.VALUE_ATTRIBUTE);
		Object value = parseValue(valueString, typeSignature);
		PartitionNode partition = new PartitionNode(name, value);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.PARTITION_NODE_NAME){
				partition.addPartition(parsePartitionElement(child, typeSignature));
			}
			if(child.getLocalName() == Constants.LABEL_NODE_NAME){
				partition.addLabel(child.getAttributeValue(Constants.LABEL_ATTRIBUTE_NAME));
			}
		}
		return partition;
	}

	protected Object parseValue(String valueString, String type) {
		switch(type){
		case Constants.TYPE_NAME_BOOLEAN:
			return Boolean.parseBoolean(valueString);
		case Constants.TYPE_NAME_BYTE:
			return Byte.parseByte(valueString);
		case Constants.TYPE_NAME_CHAR:
			if (valueString.length() <= 0){
				return null;
			}
			int intValue = Integer.parseInt(valueString);
			return (char)intValue;
		case Constants.TYPE_NAME_DOUBLE:
			return Double.parseDouble(valueString);
		case Constants.TYPE_NAME_FLOAT:
			return Float.parseFloat(valueString);
		case Constants.TYPE_NAME_INT:
			return Integer.parseInt(valueString);
		case Constants.TYPE_NAME_LONG:
			return Long.parseLong(valueString);
		case Constants.TYPE_NAME_SHORT:
			return Short.parseShort(valueString);
		case Constants.TYPE_NAME_STRING:
			return valueString.equals(Constants.NULL_VALUE_STRING_REPRESENTATION)?null:valueString;
		default:
			return null;
		}		
	}

	protected List<Element> getIterableElements(Elements elements){
		ArrayList<Element> list = new ArrayList<Element>();
		for(int i = 0; i < elements.size(); i++){
			Node node = elements.get(i);
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
}
