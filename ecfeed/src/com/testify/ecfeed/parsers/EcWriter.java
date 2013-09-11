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
import java.io.OutputStream;
import java.util.ArrayList;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.IStatement;
import com.testify.ecfeed.model.constraint.Statement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

import nu.xom.*;

public class EcWriter {
	private OutputStream fOutputStream;

	public EcWriter(OutputStream ostream){
		fOutputStream = ostream;
	}

	public void writeXmlDocument(RootNode root) {
		Element rootElement = createElement(root);
		Document document = new Document(rootElement);
		try{
			Serializer serializer = new Serializer(fOutputStream);
			serializer.setIndent(4);
			serializer.write(document);
		}catch(IOException e){
			System.out.println("IOException: " + e.getMessage());
		}
	}

	private Element createElement(IGenericNode node) {
		String name = node.getName();
		Element element = null;
		if(node instanceof RootNode){
			element = createRootElement(name);
		}
		else if(node instanceof ClassNode){
			element = createClassElement(name);
		}
		else if(node instanceof MethodNode){
			element = createMethodElement(name);
		}
		else if (node instanceof CategoryNode){
			String type = ((CategoryNode)node).getType();
			element = createCategoryElement(name, type);
		}
		else if (node instanceof PartitionNode){
			Object value = ((PartitionNode)node).getValue();
			element = createPartitionElement(name, value);
		}
		else if (node instanceof TestCaseNode){
			ArrayList<PartitionNode> testData = ((TestCaseNode)node).getTestData();
			element = createTestDataElement(name, testData);
		}
		else if (node instanceof ConstraintNode){
			element = createConstraintElement(name, (Constraint)((ConstraintNode)node).getConstraint());
		}
		
		for(IGenericNode child : node.getChildren()){
			element.appendChild(createElement(child));
		}
		return element;
	}

	//TODO Unit tests
	private Element createTestDataElement(String name, ArrayList<PartitionNode> testData) {
		Element testCaseElement = new Element(Constants.TEST_CASE_NODE_NAME);

		Attribute testSuiteNameAttribute = new Attribute(Constants.TEST_SUITE_NAME_ATTRIBUTE, name);
		testCaseElement.addAttribute(testSuiteNameAttribute);
		
		for(PartitionNode parameter : testData){
			Element testParameterElement = new Element(Constants.TEST_PARAMETER_NODE_NAME);
			Attribute partitionNameAttribute = new Attribute(Constants.PARTITION_ATTRIBUTE_NAME, parameter.getName());
			testParameterElement.addAttribute(partitionNameAttribute);
			testCaseElement.appendChild(testParameterElement);
		}
		
		return testCaseElement;
	}

	private Element createPartitionElement(String name, Object value) {
		String valueString = 
				(value == null)?Constants.NULL_VALUE_STRING_REPRESENTATION:String.valueOf(value);
		if(value == null){
			valueString = Constants.NULL_VALUE_STRING_REPRESENTATION;
		}
		else if(value instanceof Character){
			valueString = String.valueOf((int)((char)value));
		}
		else{
			valueString = String.valueOf(value);
		}
		
		Element partitionElement = new Element(Constants.PARTITION_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, name);
		Attribute valueAttribute = new Attribute(Constants.VALUE_ATTRIBUTE, valueString);
		partitionElement.addAttribute(nameAttribute);
		partitionElement.addAttribute(valueAttribute);
		return partitionElement;
	}

	private Element createCategoryElement(String name, String type) {
		Element categoryElement = new Element(Constants.CATEGORY_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, name);
		Attribute typeNameAttribute = new Attribute(Constants.TYPE_NAME_ATTRIBUTE, type);
		categoryElement.addAttribute(nameAttribute);
		categoryElement.addAttribute(typeNameAttribute);
		return categoryElement;
	}

	private Element createMethodElement(String name) {
		Element methodElement = new Element(Constants.METHOD_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, name);
		methodElement.addAttribute(nameAttribute);
		return methodElement;
	}

	private Element createClassElement(String qualifiedName) {
		Element classElement = new Element(Constants.CLASS_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, qualifiedName);
		classElement.addAttribute(nameAttribute);
		return classElement;
	}

	private Element createRootElement(String name) {
		Element rootElement = new Element(Constants.ROOT_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, name);
		rootElement.addAttribute(nameAttribute);
		return rootElement;
	}

	private Element createConstraintElement(String name, Constraint constraint){
		Element constraintElement = new Element(Constants.CONSTRAINT_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, name);
		constraintElement.addAttribute(nameAttribute);
		Element premiseElement = new Element(Constants.CONSTRAINT_PREMISE_NODE_NAME);
		appendStatement(premiseElement, constraint.getPremise());
		constraintElement.appendChild(premiseElement);
		Element consequenceElement = new Element(Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME);
		appendStatement(consequenceElement, constraint.getConsequence());
		constraintElement.appendChild(consequenceElement);
		return constraintElement;
	}
	
	private void appendStatement(Element element, IStatement istatement){
		if(istatement instanceof StatementArray){
			appentStatementArray(element, istatement);
		}
		else if(istatement instanceof StaticStatement){
			StaticStatement statement = (StaticStatement)istatement;
			Element statementElement = new Element(Constants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME);
			String attrName = Constants.STATEMENT_STATIC_VALUE_ATTRIBUTE_NAME;
			String attrValue = statement.getValue()?Constants.STATIC_STATEMENT_TRUE_VALUE:
								Constants.STATIC_STATEMENT_FALSE_VALUE;
			statementElement.addAttribute(new Attribute(attrName, attrValue));
			
			element.appendChild(statementElement);
		}
		else if(istatement instanceof Statement){
			Statement statement = (Statement)istatement;
			Element statementElement = new Element(Constants.CONSTRAINT_STATEMENT_NODE_NAME);
			PartitionNode condition = statement.getCondition();
			String categoryName = condition.getParent().getName();
			Attribute categoryAttribute = 
					new Attribute(Constants.STATEMENT_CATEGORY_ATTRIBUTE_NAME, categoryName);
			Attribute partitionAttribute = 
					new Attribute(Constants.STATEMENT_PARTITION_ATTRIBUTE_NAME, condition.getName());
			Attribute relationAttribute = 
					new Attribute(Constants.STATEMENT_RELATION_ATTRIBUTE_NAME, statement.getRelation().toString());
			statementElement.addAttribute(categoryAttribute);
			statementElement.addAttribute(partitionAttribute);
			statementElement.addAttribute(relationAttribute);

			element.appendChild(statementElement);
		}
	}

	private void appentStatementArray(Element element, IStatement statement) {
		StatementArray statementArray = (StatementArray)statement;
		Element statementArrayElement = new Element(Constants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME);
		Attribute operatorAttribute = null;
		switch(statementArray.getOperator()){
		case AND:
			operatorAttribute = new Attribute(Constants.STATEMENT_OPERATOR_ATTRIBUTE_NAME, 
					Constants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE);
			break;
		case OR:
			operatorAttribute = new Attribute(Constants.STATEMENT_OPERATOR_ATTRIBUTE_NAME, 
					Constants.STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE);
			break;
		}
		statementArrayElement.addAttribute(operatorAttribute);
		for(IStatement child : statementArray.getChildren()){
			appendStatement(statementArrayElement, child);
		}
		element.appendChild(statementArrayElement);
	}
	
}
