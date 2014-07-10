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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.IStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;

public class ObsoleteXmlModelSerializer {
	private OutputStream fOutputStream;

	public ObsoleteXmlModelSerializer(OutputStream ostream){
		fOutputStream = ostream;
	}

	public void writeXmlDocument(RootNode root) throws IOException {
		Element rootElement = createElement(root);
		Document document = new Document(rootElement);
		Serializer serializer = new Serializer(fOutputStream);
		// Uncomment for pretty formatting. This however will affect 
		// whitespaces in the document's ... infoset
		//serializer.setIndent(4);
		serializer.write(document);
	}

	protected Element createElement(IGenericNode node) {
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
			String value = ((CategoryNode)node).getDefaultValueString();
			element = createCategoryElement(name, type, ((CategoryNode)node).isExpected(), value);
		}
		else if (node instanceof PartitionNode){
			String value = ((PartitionNode)node).getExactValueString();
			String type = ((PartitionNode)node).getCategory().getType();
			element = createPartitionElement(type, name, value, ((PartitionNode)node).getLabels());
			
		}
		else if (node instanceof TestCaseNode){
			List<PartitionNode> testData = ((TestCaseNode)node).getTestData();
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
	protected Element createTestDataElement(String name, List<PartitionNode> testData) {
		Element testCaseElement = new Element(Constants.TEST_CASE_NODE_NAME);

		Attribute testSuiteNameAttribute = new Attribute(Constants.TEST_SUITE_NAME_ATTRIBUTE, name);
		testCaseElement.addAttribute(testSuiteNameAttribute);
		
		for(PartitionNode parameter : testData){
			if(parameter.getCategory().isExpected()){
				createExpectedValueElement(testCaseElement, parameter);
			}
			else{
				createTestParameterElement(testCaseElement, parameter);
			}
		}
		
		return testCaseElement;
	}

	protected void createExpectedValueElement(Element testCaseElement,
			PartitionNode parameter) {
		Element testParameterElement = new Element(Constants.EXPECTED_PARAMETER_NODE_NAME);
		Attribute partitionNameAttribute = new Attribute(Constants.VALUE_ATTRIBUTE_NAME, parameter.getExactValueString());
		testParameterElement.addAttribute(partitionNameAttribute);
		testCaseElement.appendChild(testParameterElement);
	}

	protected void createTestParameterElement(Element testCaseElement,
			PartitionNode parameter) {
		Element testParameterElement = new Element(Constants.TEST_PARAMETER_NODE_NAME);
		Attribute partitionNameAttribute = new Attribute(Constants.PARTITION_ATTRIBUTE_NAME, parameter.getQualifiedName());
		testParameterElement.addAttribute(partitionNameAttribute);
		testCaseElement.appendChild(testParameterElement);
	}

	protected Element createPartitionElement(String type, String name, String value, Set<String> labels) {
		Element partitionElement = new Element(Constants.PARTITION_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, name);
		Attribute valueAttribute = new Attribute(Constants.VALUE_ATTRIBUTE, value);
		partitionElement.addAttribute(nameAttribute);
		partitionElement.addAttribute(valueAttribute);
		for(String label : labels){
			Element labelElement = new Element(Constants.LABEL_NODE_NAME);
			labelElement.addAttribute(new Attribute(Constants.LABEL_ATTRIBUTE_NAME, label));
			partitionElement.appendChild(labelElement);
		}
		return partitionElement;
	}

	protected Element createCategoryElement(String name, String type, boolean expected, String value) {
		Element categoryElement = new Element(Constants.CATEGORY_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, name);
		Attribute typeNameAttribute = new Attribute(Constants.TYPE_NAME_ATTRIBUTE, type);
		Attribute expectedAttribute;
		if(expected){
			expectedAttribute = new Attribute(Constants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, value);
		} else {
			expectedAttribute = new Attribute(Constants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, "");
		}
		categoryElement.addAttribute(expectedAttribute);
		Attribute isExpectedAttribute = new Attribute(Constants.CATEGORY_IS_EXPECTED_ATTRIBUTE_NAME, Boolean.toString(expected));
		categoryElement.addAttribute(nameAttribute);
		categoryElement.addAttribute(typeNameAttribute);
		categoryElement.addAttribute(isExpectedAttribute);
		return categoryElement;
	}

	protected Element createMethodElement(String name) {
		Element methodElement = new Element(Constants.METHOD_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, name);
		methodElement.addAttribute(nameAttribute);
		return methodElement;
	}

	protected Element createClassElement(String qualifiedName) {
		Element classElement = new Element(Constants.CLASS_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, qualifiedName);
		classElement.addAttribute(nameAttribute);
		return classElement;
	}

	protected Element createRootElement(String name) {
		Element rootElement = new Element(Constants.ROOT_NODE_NAME);
		Attribute nameAttribute = new Attribute(Constants.NODE_NAME_ATTRIBUTE, name);
		rootElement.addAttribute(nameAttribute);
		return rootElement;
	}

	protected Element createConstraintElement(String name, Constraint constraint){
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
	
	protected void appendStatement(Element element, IStatement istatement){
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
		else if(istatement instanceof PartitionedCategoryStatement){
			PartitionedCategoryStatement statement = (PartitionedCategoryStatement)istatement;
			String categoryName = statement.getCategory().getName();
			Attribute categoryAttribute = 
					new Attribute(Constants.STATEMENT_CATEGORY_ATTRIBUTE_NAME, categoryName);
			Attribute relationAttribute = 
					new Attribute(Constants.STATEMENT_RELATION_ATTRIBUTE_NAME, statement.getRelation().toString());
			Object condition = statement.getConditionValue();
			Element statementElement = null;
			if(condition instanceof String){
				String label = (String)condition;
				statementElement = new Element(Constants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME);
				statementElement.addAttribute(new Attribute(Constants.STATEMENT_LABEL_ATTRIBUTE_NAME, label));
			}
			else if(condition instanceof PartitionNode){
				PartitionNode partition = (PartitionNode)condition;
				statementElement = new Element(Constants.CONSTRAINT_PARTITION_STATEMENT_NODE_NAME);
				statementElement.addAttribute(new Attribute(Constants.STATEMENT_PARTITION_ATTRIBUTE_NAME, partition.getQualifiedName()));
			}
//TODO implement exceptions for unsupported types			
//			else{
//				throw new ParserException("Unknown statement condition type");
//			}
			statementElement.addAttribute(categoryAttribute);
			statementElement.addAttribute(relationAttribute);
			
			element.appendChild(statementElement);
		}
		else if(istatement instanceof ExpectedValueStatement){
			ExpectedValueStatement statement = (ExpectedValueStatement)istatement;
			String categoryName = statement.getLeftHandName();
			PartitionNode condition = statement.getCondition();
			Attribute categoryAttribute = 
					new Attribute(Constants.STATEMENT_CATEGORY_ATTRIBUTE_NAME, categoryName);
			Attribute valueAttribute = 
					new Attribute(Constants.STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME, condition.getExactValueString());
			
			Element statementElement = new Element(Constants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME);
			statementElement.addAttribute(categoryAttribute);
			statementElement.addAttribute(valueAttribute);

			element.appendChild(statementElement);
		}
	}

	protected void appentStatementArray(Element element, IStatement statement) {
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
