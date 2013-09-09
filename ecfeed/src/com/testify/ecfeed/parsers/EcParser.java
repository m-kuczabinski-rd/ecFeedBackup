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

package com.testify.ecfeed.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import nu.xom.*;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.IStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.Statement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

public class EcParser {
	
	public RootNode parseEctFile(InputStream istream){
		RootNode root = null;
		try {
			Builder parser = new Builder();
			Document document = parser.build(istream);
			if(document.getRootElement().getLocalName() == Constants.ROOT_NODE_NAME){
				root = (RootNode)parseRootElement(document.getRootElement());
			}
		} catch (IOException|ParsingException e) {
			System.out.println("Exception: " + e.getMessage());
			return null;
		} 
		return root;
	}

	private RootNode parseRootElement(Element element) {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if(name == null || element.getLocalName() != Constants.ROOT_NODE_NAME){
			return null;
		}

		RootNode rootNode = new RootNode(name);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.CLASS_NODE_NAME){
				rootNode.addClass(parseClassElement(child));
			}
		}
		return rootNode;
	}

	private ClassNode parseClassElement(Element element) {
		String qualifiedName = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if (qualifiedName == null){
			return null;
		}
		
		ClassNode classNode = new ClassNode(qualifiedName);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.METHOD_NODE_NAME){
				classNode.addMethod(parseMethodElement(child));
			}
		}

		return classNode;
	}

	//TODO unit tests
	private MethodNode parseMethodElement(Element element) {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if (name == null){
			return null;
		}
		
		MethodNode methodNode = new MethodNode(name);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.CATEGORY_NODE_NAME){
				methodNode.addCategory(parseCategoryElement(child));
			}
			if(child.getLocalName() == Constants.TEST_CASE_NODE_NAME){
				methodNode.addTestCase(parseTestCaseElement(child, methodNode.getCategories()));
			}
			if(child.getLocalName() == Constants.CONSTRAINT_NODE_NAME){
				methodNode.addConstraint(parseConstraintElement(child, methodNode));
			}
		}
		return methodNode;
	}
	
	private ConstraintNode parseConstraintElement(Element constraintElement, MethodNode method) {
		String name = constraintElement.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		BasicStatement premise = null;
		BasicStatement consequence = null;
		for(Element child : getIterableElements(constraintElement.getChildElements())){
			if(child.getLocalName().equals(Constants.CONSTRAINT_PREMISE_NODE_NAME)){
				if(child.getChildCount() > 0){
					//there is only one statement per premise or consequence that is either
					//a single statement or statement array
					premise = parseStatement(child.getChildElements().get(0), method);
				}
			}
			else if(child.getLocalName().equals(Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME)){
				if(child.getChildCount() > 0){
					consequence = parseStatement(child.getChildElements().get(0), method);
				}
			}
		}
		return new ConstraintNode(name, new Constraint(premise, consequence));
	}

	private BasicStatement parseStatement(Element element, MethodNode method) {
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

	private BasicStatement parseStatementArray(Element element, MethodNode method) {
		StatementArray statementArray = null;
		String operatorValue = element.getAttributeValue(Constants.STATEMENT_OPERATOR_ATTRIBUTE_NAME);
		switch(operatorValue){
		case Constants.STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(Operator.OR);
			break;
		case Constants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(Operator.AND);
			break;
		default: return null;
		}
		for(Element child : getIterableElements(element.getChildElements())){
			BasicStatement childStatement = parseStatement(child, method);
			if(childStatement != null){
				statementArray.addStatement(childStatement);
			}
		}
		return statementArray;
	}

	private BasicStatement parseStaticStatement(Element element) {
		String valueString = element.getAttributeValue(Constants.STATIC_VALUE_ATTRIBUTE_NAME);
		switch(valueString){
		case Constants.STATIC_STATEMENT_TRUE_VALUE:
			return new StaticStatement(true);
		case Constants.STATIC_STATEMENT_FALSE_VALUE:
			return new StaticStatement(false);
		default: return null;
		}
	}

	private BasicStatement parseSigleStatement(Element element, MethodNode method) {
		
		String categoryName = element.getAttributeValue(Constants.STATEMENT_CATEGORY_ATTRIBUTE_NAME);
		CategoryNode category = method.getCategory(categoryName);
		String partitionName = element.getAttributeValue(Constants.STATEMENT_PARTITION_ATTRIBUTE_NAME);
		PartitionNode partition = category.getPartition(partitionName);

		String relationName = element.getAttributeValue(Constants.STATEMENT_RELATION_ATTRIBUTE_NAME);
		Relation relation = null;
		switch(relationName){
		case Constants.RELATION_LESS:
			relation = Relation.LESS;
			break;
		case Constants.RELATION_LESS_EQUAL:
			relation = Relation.LESS_EQUAL;
			break;
		case Constants.RELATION_EQUAL:
			relation = Relation.EQUAL;
			break;
		case Constants.RELATION_GREATER_EQUAL:
			relation = Relation.GREATER_EQUAL;
			break;
		case Constants.RELATION_GREATER:
			relation = Relation.GREATER;
			break;
		case Constants.RELATION_NOT:
			relation = Relation.NOT;
			break;
		default:
			relation = Relation.EQUAL;
			break;
		}
		
		return new Statement(partition, relation);
	}

	//TODO unit tests
	private TestCaseNode parseTestCaseElement(Element element, Vector<CategoryNode> categories) {
		String testSuiteName = element.getAttributeValue(Constants.TEST_SUITE_NAME_ATTRIBUTE);
		Vector<PartitionNode> testData = new Vector<PartitionNode>();
		Vector<Element> parameterElements = getIterableElements(element.getChildElements());
		
		if(categories.size() != parameterElements.size()){
			return null;
		}

		for(int i = 0; i < parameterElements.size(); i++){
			Element testParameterElement = parameterElements.elementAt(i);
			
			if(testParameterElement.getLocalName().equals(Constants.TEST_PARAMETER_NODE_NAME)){
				String partitionName = testParameterElement.getAttributeValue(Constants.PARTITION_ATTRIBUTE_NAME);
				PartitionNode partition = categories.elementAt(i).getPartition(partitionName);
				testData.add(partition);
			}
		}
		return new TestCaseNode(testSuiteName, testData);
	}

	private CategoryNode parseCategoryElement(Element element) {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		String type = element.getAttributeValue(Constants.TYPE_NAME_ATTRIBUTE);
		if (name == null | type == null){
			return null;
		}
		
		CategoryNode categoryNode = new CategoryNode(name, type);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.PARTITION_NODE_NAME){
				categoryNode.addPartition(parsePartitionElement(child, type));
			}
		}
		
		return categoryNode;
	}

	private PartitionNode parsePartitionElement(Element element, String typeSignature) {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		String valueString = element.getAttributeValue(Constants.VALUE_ATTRIBUTE);
		if (name == null | valueString == null){
			return null;
		}
		Object value = parseValue(valueString, typeSignature);
		return new PartitionNode(name, value);
	}

	private Object parseValue(String valueString, String type) {
		switch(type){
		case Constants.BOOLEAN_TYPE_NAME:
			return Boolean.parseBoolean(valueString);
		case Constants.BYTE_TYPE_NAME:
			return Byte.parseByte(valueString);
		case Constants.CHAR_TYPE_NAME:
			if (valueString.length() <= 0){
				return null;
			}
			int intValue = Integer.parseInt(valueString);
			return (char)intValue;
		case Constants.DOUBLE_TYPE_NAME:
			return Double.parseDouble(valueString);
		case Constants.FLOAT_TYPE_NAME:
			return Float.parseFloat(valueString);
		case Constants.INT_TYPE_NAME:
			return Integer.parseInt(valueString);
		case Constants.LONG_TYPE_NAME:
			return Long.parseLong(valueString);
		case Constants.SHORT_TYPE_NAME:
			return Short.parseShort(valueString);
		case Constants.STRING_TYPE_NAME:
			return valueString.equals(Constants.NULL_VALUE_STRING_REPRESENTATION)?null:valueString;
		default:
			return null;
		}		
	}

	private Vector<Element> getIterableElements(Elements elements){
		Vector<Element> v = new Vector<Element>();
		for(int i = 0; i < elements.size(); i++){
			Node node = elements.get(i);
			if(node instanceof Element){
				v.add((Element)node);
			}
		}
		return v;
	}

}
