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

import static com.testify.ecfeed.serialization.ect.Constants.CLASS_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.CONSTRAINT_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.METHOD_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.PARAMETER_IS_LINKED_ATTRIBUTE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.PARAMETER_LINK_ATTRIBUTE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.ROOT_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.TEST_CASE_NODE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.TEST_SUITE_NAME_ATTRIBUTE;
import static com.testify.ecfeed.serialization.ect.Constants.TYPE_NAME_ATTRIBUTE;
import static com.testify.ecfeed.serialization.ect.Constants.VALUE_ATTRIBUTE;
import static com.testify.ecfeed.serialization.ect.Constants.PARAMETER_IS_RUN_ON_ANDROID_ATTRIBUTE_NAME;
import static com.testify.ecfeed.serialization.ect.Constants.ANDROID_RUNNER_ATTRIBUTE_NAME;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import com.testify.ecfeed.adapter.java.JavaPrimitiveTypePredicate;
import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ChoicesParentStatement;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.EStatementOperator;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.serialization.ParserException;
import com.testify.ecfeed.serialization.WhiteCharConverter;

public abstract class XomAnalyser {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	protected abstract String getChoiceNodeName();
	protected abstract String getChoiceAttributeName();
	protected abstract String getStatementChoiceAttributeName();
	protected abstract String getParameterNodeName();
	protected abstract String getStatementParameterAttributeName();

	public RootNode parseRoot(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), ROOT_NODE_NAME);
		String name = getElementName(element);

		RootNode root = new RootNode(name);
		root.setDescription(parseComments(element));

		//parameters must be parsed before classes
		for(Element child : getIterableChildren(element, getParameterNodeName())){
			try{
				root.addParameter(parseGlobalParameter(child));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}
		for(Element child : getIterableChildren(element, Constants.CLASS_NODE_NAME)){
			try{
				root.addClass(parseClass(child, root));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		return root;
	}

	public ClassNode parseClass(Element element, RootNode parent) throws ParserException{
		assertNodeTag(element.getQualifiedName(), CLASS_NODE_NAME);

		String name = getElementName(element);

		boolean runOnAndroid = Boolean.parseBoolean(
				element.getAttributeValue(PARAMETER_IS_RUN_ON_ANDROID_ATTRIBUTE_NAME));

		String androidBaseRunner = element.getAttributeValue(ANDROID_RUNNER_ATTRIBUTE_NAME);

		ClassNode _class = new ClassNode(name, runOnAndroid, androidBaseRunner);

		_class.setDescription(parseComments(element));
		//we need to do it here, so the backward search for global parameters will work
		_class.setParent(parent);

		//parameters must be parsed before classes
		for(Element child : getIterableChildren(element, getParameterNodeName())){
			try{
				_class.addParameter(parseGlobalParameter(child));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}
		for(Element child : getIterableChildren(element, Constants.METHOD_NODE_NAME)){
			try{
				_class.addMethod(parseMethod(child, _class));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		return _class;
	}

	public MethodNode parseMethod(Element element, ClassNode parent) throws ParserException{
		assertNodeTag(element.getQualifiedName(), METHOD_NODE_NAME);
		String name = getElementName(element);

		MethodNode method = new MethodNode(name);
		method.setParent(parent);

		for(Element child : getIterableChildren(element, getParameterNodeName())){
			try{
				method.addParameter(parseMethodParameter(child, method));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		for(Element child : getIterableChildren(element, Constants.TEST_CASE_NODE_NAME)){
			try{
				method.addTestCase(parseTestCase(child, method));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		for(Element child : getIterableChildren(element, Constants.CONSTRAINT_NODE_NAME)){
			try{
				method.addConstraint(parseConstraint(child, method));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		method.setDescription(parseComments(element));

		return method;
	}

	public MethodParameterNode parseMethodParameter(Element element, MethodNode method) throws ParserException{
		assertNodeTag(element.getQualifiedName(), getParameterNodeName());
		String name = getElementName(element);
		String type = getAttributeValue(element, TYPE_NAME_ATTRIBUTE);
		String defaultValue = null;
		String expected = String.valueOf(false);

		if(element.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null){
			expected = getAttributeValue(element, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME);
			defaultValue = getAttributeValue(element, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME);
		}
		MethodParameterNode parameter = new MethodParameterNode(name, type, defaultValue, Boolean.parseBoolean(expected));

		if(element.getAttribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME) != null){
			boolean linked = Boolean.parseBoolean(getAttributeValue(element, PARAMETER_IS_LINKED_ATTRIBUTE_NAME));
			parameter.setLinked(linked);
		}

		if(element.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null && method != null && method.getClassNode() != null){
			String linkPath = getAttributeValue(element, PARAMETER_LINK_ATTRIBUTE_NAME);
			GlobalParameterNode link = method.getClassNode().findGlobalParameter(linkPath);
			if(link != null){
				parameter.setLink(link);
			}
			else{
				parameter.setLinked(false);
			}
		}else{
			parameter.setLinked(false);
		}

		for(Element child : getIterableChildren(element, getChoiceNodeName())){
			try{
				parameter.addChoice(parseChoice(child));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		parameter.setDescription(parseComments(element));
		if(parameter.isLinked() == false){
			parameter.setTypeComments(parseTypeComments(element));
		}

		return parameter;
	}

	public GlobalParameterNode parseGlobalParameter(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), getParameterNodeName());
		String name = getElementName(element);
		String type = getAttributeValue(element, TYPE_NAME_ATTRIBUTE);
		GlobalParameterNode parameter = new GlobalParameterNode(name, type);

		for(Element child : getIterableChildren(element, getChoiceNodeName())){
			try{
				parameter.addChoice(parseChoice(child));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		parameter.setDescription(parseComments(element));
		parameter.setTypeComments(parseTypeComments(element));

		return parameter;
	}

	public TestCaseNode parseTestCase(Element element, MethodNode method) throws ParserException{
		assertNodeTag(element.getQualifiedName(), TEST_CASE_NODE_NAME);
		String name = getAttributeValue(element, TEST_SUITE_NAME_ATTRIBUTE);

		List<Element> parameterElements = getIterableChildren(element);
		List<MethodParameterNode> parameters = method.getMethodParameters();

		List<ChoiceNode> testData = new ArrayList<ChoiceNode>();

		if(parameters.size() != parameterElements.size()){
			ParserException.report(Messages.WRONG_NUMBER_OF_TEST_PAREMETERS(name));
		}

		for(int i = 0; i < parameterElements.size(); i++){
			Element testParameterElement = parameterElements.get(i);
			MethodParameterNode parameter = parameters.get(i);
			ChoiceNode testValue = null;

			if(testParameterElement.getLocalName().equals(Constants.TEST_PARAMETER_NODE_NAME)){
				String choiceName = getAttributeValue(testParameterElement, getChoiceAttributeName());
				testValue = parameter.getChoice(choiceName);
				if(testValue == null){
					ParserException.report(Messages.PARTITION_DOES_NOT_EXIST(parameter.getName(), choiceName));
				}
			}
			else if(testParameterElement.getLocalName().equals(Constants.EXPECTED_PARAMETER_NODE_NAME)){
				String valueString = getAttributeValue(testParameterElement, Constants.VALUE_ATTRIBUTE_NAME);
				if(valueString == null){
					ParserException.report(Messages.MISSING_VALUE_ATTRIBUTE_IN_TEST_CASE_ELEMENT);
				}
				testValue = new ChoiceNode(Constants.EXPECTED_VALUE_CHOICE_NAME, valueString);
				testValue.setParent(parameter);
			}
			testData.add(testValue);
		}

		TestCaseNode testCase = new TestCaseNode(name, testData);
		testCase.setDescription(parseComments(element));
		return testCase;
	}

	public ConstraintNode parseConstraint(Element element, MethodNode method) throws ParserException{
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_NODE_NAME);
		String name = getElementName(element);

		AbstractStatement premise = null;
		AbstractStatement consequence = null;

		if((getIterableChildren(element, Constants.CONSTRAINT_PREMISE_NODE_NAME).size() != 1) ||
				(getIterableChildren(element, Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME).size() != 1)){
			ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
		}
		for(Element child : getIterableChildren(element, Constants.CONSTRAINT_PREMISE_NODE_NAME)){
			if(child.getLocalName().equals(Constants.CONSTRAINT_PREMISE_NODE_NAME)){
				if(getIterableChildren(child).size() == 1){
					//there is only one statement per premise or consequence that is either
					//a single statement or statement array
					premise = parseStatement(child.getChildElements().get(0), method);
				}
				else{
					ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
				}
			}
		}
		for(Element child : getIterableChildren(element, Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME)){
			if(child.getLocalName().equals(Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME)){
				if(getIterableChildren(child).size() == 1){
					consequence = parseStatement(child.getChildElements().get(0), method);
				}
				else{
					ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
				}
			}
			else{
				ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
			}
		}
		if(premise == null || consequence == null){
			ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
		}

		ConstraintNode constraint = new ConstraintNode(name, new Constraint(premise, consequence));

		constraint.setDescription(parseComments(element));

		return constraint;
	}

	public AbstractStatement parseStatement(Element element, MethodNode method) throws ParserException {
		switch(element.getLocalName()){
		case Constants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME:
			return parseChoiceStatement(element, method);
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
			statementArray = new StatementArray(EStatementOperator.OR);
			break;
		case Constants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(EStatementOperator.AND);
			break;
		default:
			ParserException.report(Messages.WRONG_STATEMENT_ARRAY_OPERATOR(method.getName(), operatorValue));
		}
		for(Element child : getIterableChildren(element)){
			AbstractStatement childStatement = parseStatement(child, method);
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
			ParserException.report(Messages.WRONG_STATIC_STATEMENT_VALUE(valueString));
			return new StaticStatement(false);
		}
	}

	public ChoicesParentStatement parseChoiceStatement(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_CHOICE_STATEMENT_NODE_NAME);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName());
		MethodParameterNode parameter = (MethodParameterNode)method.getParameter(parameterName);
		if(parameter == null || parameter.isExpected()){
			ParserException.report(Messages.WRONG_CATEGORY_NAME(parameterName, method.getName()));
		}
		String choiceName = getAttributeValue(element, getStatementChoiceAttributeName());
		ChoiceNode choice = parameter.getChoice(choiceName);
		if(choice == null){
			ParserException.report(Messages.WRONG_PARTITION_NAME(choiceName, parameterName, method.getName()));
		}

		String relationName = getAttributeValue(element, Constants.STATEMENT_RELATION_ATTRIBUTE_NAME);
		EStatementRelation relation = getRelation(relationName);

		return new ChoicesParentStatement(parameter, relation, choice);
	}

	public ChoicesParentStatement parseLabelStatement(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_LABEL_STATEMENT_NODE_NAME);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName());
		String label = getAttributeValue(element, Constants.STATEMENT_LABEL_ATTRIBUTE_NAME);
		String relationName = getAttributeValue(element, Constants.STATEMENT_RELATION_ATTRIBUTE_NAME);

		MethodParameterNode parameter = method.getMethodParameter(parameterName);
		if(parameter == null || parameter.isExpected()){
			ParserException.report(Messages.WRONG_CATEGORY_NAME(parameterName, method.getName()));
		}
		EStatementRelation relation = getRelation(relationName);

		return new ChoicesParentStatement(parameter, relation, label);
	}

	public ExpectedValueStatement parseExpectedValueStatement(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName());
		String valueString = getAttributeValue(element, Constants.STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME);
		MethodParameterNode parameter = method.getMethodParameter(parameterName);
		if(parameter == null || !parameter.isExpected()){
			ParserException.report(Messages.WRONG_CATEGORY_NAME(parameterName, method.getName()));
		}
		ChoiceNode condition = new ChoiceNode("expected", valueString);
		condition.setParent(parameter);

		return new ExpectedValueStatement(parameter, condition, new JavaPrimitiveTypePredicate());
	}

	public ChoiceNode parseChoice(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), getChoiceNodeName());
		String name = getElementName(element);
		String value = getAttributeValue(element, VALUE_ATTRIBUTE);

		ChoiceNode choice = new ChoiceNode(name, value);
		choice.setDescription(parseComments(element));

		for(Element child : getIterableChildren(element)){
			if(child.getLocalName() == getChoiceNodeName()){
				try{
					choice.addChoice(parseChoice(child));
				}catch(ParserException e){
					System.err.println("Exception: " + e.getMessage());
				}

			}
			if(child.getLocalName() == Constants.LABEL_NODE_NAME){
				choice.addLabel(fWhiteCharConverter.decode(child.getAttributeValue(Constants.LABEL_ATTRIBUTE_NAME)));
			}
		}

		return choice;
	}

	private void assertNodeTag(String qualifiedName, String expectedName) throws ParserException {
		if(qualifiedName.equals(expectedName) == false){
			ParserException.report("Unexpected node name: " + qualifiedName + " instead of " + expectedName);
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

	protected List<Element> getIterableChildren(Element element, String name){
		List<Element> elements = getIterableChildren(element);
		Iterator<Element> it = elements.iterator();
		while(it.hasNext()){
			if(it.next().getLocalName().equals(name) == false){
				it.remove();
			}
		}
		return elements;
	}

	protected String getElementName(Element element) throws ParserException {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if(name == null){
			ParserException.report(Messages.MISSING_ATTRIBUTE(element, Constants.NODE_NAME_ATTRIBUTE));
		}
		return fWhiteCharConverter.decode(name);
	}

	protected String getAttributeValue(Element element, String attributeName) throws ParserException{

		String value = element.getAttributeValue(attributeName);
		if(value == null){
			ParserException.report(Messages.MISSING_ATTRIBUTE(element, attributeName));
		}

		return fWhiteCharConverter.decode(value);
	}

	protected EStatementRelation getRelation(String relationName) throws ParserException{
		EStatementRelation relation = null;
		switch(relationName){
		case Constants.RELATION_EQUAL:
			relation = EStatementRelation.EQUAL;
			break;
		case Constants.RELATION_NOT:
		case Constants.RELATION_NOT_ASCII:
			relation = EStatementRelation.NOT;
			break;
		default:
			ParserException.report(Messages.WRONG_OR_MISSING_RELATION_FORMAT(relationName));
		}
		return relation;
	}

	protected String parseComments(Element element) {
		if(element.getChildElements(Constants.COMMENTS_BLOCK_TAG_NAME).size() > 0){
			Element comments = element.getChildElements(Constants.COMMENTS_BLOCK_TAG_NAME).get(0);
			if(comments.getChildElements(Constants.BASIC_COMMENTS_BLOCK_TAG_NAME).size() > 0){
				Element basicComments = comments.getChildElements(Constants.BASIC_COMMENTS_BLOCK_TAG_NAME).get(0);
				return fWhiteCharConverter.decode(basicComments.getValue());
			}
		}
		return null;
	}

	protected String parseTypeComments(Element element){
		if(element.getChildElements(Constants.COMMENTS_BLOCK_TAG_NAME).size() > 0){
			Element comments = element.getChildElements(Constants.COMMENTS_BLOCK_TAG_NAME).get(0);
			if(comments.getChildElements(Constants.TYPE_COMMENTS_BLOCK_TAG_NAME).size() > 0){
				Element typeComments = comments.getChildElements(Constants.TYPE_COMMENTS_BLOCK_TAG_NAME).get(0);
				return typeComments.getValue();
			}
		}
		return null;
	}
}
