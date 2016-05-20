/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.core.serialization.export;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.testify.ecfeed.core.adapter.java.ChoiceValueParser;
import com.testify.ecfeed.core.adapter.java.JavaUtils;
import com.testify.ecfeed.core.model.AbstractParameterNode;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.serialization.export.Expression;

public class TestCasesExportHelper {

	private static final String CLASS_NAME_SEQUENCE = "%class";
	private static final String PACKAGE_NAME_SEQUENCE = "%package";
	private static final String METHOD_NAME_SEQUENCE = "%method";
	private static final String TEST_SUITE_NAME_SEQUENCE = "%suite";
	private static final String TEST_CASE_INDEX_NAME_SEQUENCE = "%index";
	private static final String PARAMETER_COMMAND_NAME = "name";
	private static final String CHOICE_COMMAND_SHORT_NAME = "choice";
	private static final String CHOICE_COMMAND_FULL_NAME = "full_choice";
	private static final String CHOICE_COMMAND_VALUE = "value";
	private static final String TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN = "\\$\\d+\\.(" + CHOICE_COMMAND_SHORT_NAME + "|" + CHOICE_COMMAND_FULL_NAME + "|" + CHOICE_COMMAND_VALUE + ")";
	private static final String METHOD_PARAMETER_SEQUENCE_GENERIC_PATTERN = "\\$\\d+\\." + PARAMETER_COMMAND_NAME;
	private static final String ARITHMETIC_EXPRESSION_SEQUENCE_GENERIC_PATTERN = "\\$\\(.*\\)";

	public static String generateSection(MethodNode method, String template) {
		String result = template.replace(CLASS_NAME_SEQUENCE, JavaUtils.getLocalName(method.getClassNode()));
		result = result.replace(PACKAGE_NAME_SEQUENCE, JavaUtils.getPackageName(method.getClassNode()));
		result = result.replace(METHOD_NAME_SEQUENCE, method.getName());
		result = replaceParameterSequences(method, result);
		result = evaluateExpressions(result);

		return result;
	}

	private static String replaceParameterSequences(MethodNode method, String template) {
		String result = template;
		Matcher m = Pattern.compile(METHOD_PARAMETER_SEQUENCE_GENERIC_PATTERN).matcher(template);
		while(m.find()){
			String parameterCommandSequence = m.group();
			String command = getParameterCommand(parameterCommandSequence);
			int parameterNumber = getParameterNumber(parameterCommandSequence) - 1;
			MethodParameterNode parameter = method.getMethodParameters().get(parameterNumber);
			String substitute = resolveParameterCommand(command, parameter);
			result = result.replace(parameterCommandSequence, substitute);
		}		

		return result;
	}

	private static String resolveParameterCommand(String command, MethodParameterNode parameter) {
		String result = command;
		switch(command){
		case PARAMETER_COMMAND_NAME:
			result = parameter.getName();
			break;
		default:
			break;
		}
		return result;
	}

	private static String getParameterCommand(String parameterCommandSequence) {
		return parameterCommandSequence.substring(parameterCommandSequence.indexOf(".") + 1, parameterCommandSequence.length());
	}

	private static int getParameterNumber(String parameterSequence) {
		String parameterNumberString = parameterSequence.substring(1, parameterSequence.indexOf("."));
		return Integer.parseInt(parameterNumberString);
	}

	public static String generateTestCaseString(int sequenceIndex, TestCaseNode testCase, String template) {

		MethodNode method = testCase.getMethod();

		String result = generateSection(method, template);
		result = replaceTestParameterSequences(testCase, result);
		result = result.replace(TEST_CASE_INDEX_NAME_SEQUENCE, String.valueOf(sequenceIndex));
		result = result.replace(TEST_SUITE_NAME_SEQUENCE, testCase.getName());
		result = evaluateExpressions(result);

		return result;
	}

	private static String evaluateExpressions(String template) {
		String result = template;
		Matcher m = Pattern.compile(ARITHMETIC_EXPRESSION_SEQUENCE_GENERIC_PATTERN).matcher(template);
		while(m.find()){
			String expressionSequence = m.group();
			String expressionString = expressionSequence.substring(2, expressionSequence.length() - 1); //remove initial "$(" and ending ")"
			try{
				Expression expression = new Expression(expressionString);
				String substitute = expression.eval().toPlainString();
				result = result.replace(expressionSequence, substitute);
			}catch(RuntimeException e){} //if evaluation failed, do not stop, keep the result as it is
		}		
		return result;
	}

	private static String replaceTestParameterSequences(TestCaseNode testCase, String template) {

		String result = replaceParameterSequences(testCase.getMethod(), template);

		Matcher m = Pattern.compile(TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN).matcher(template);

		while(m.find()){
			String parameterCommandSequence = m.group();

			String command = getParameterCommand(parameterCommandSequence);

			int parameterNumber = getParameterNumber(parameterCommandSequence) - 1;

			if (parameterNumber < testCase.getTestData().size()) {
				ChoiceNode choice = testCase.getTestData().get(parameterNumber);
				String substitute = resolveChoiceCommand(command, choice);
				result = result.replace(parameterCommandSequence, substitute);
			}
		}

		return result;
	}

	private static String resolveChoiceCommand(String command, ChoiceNode choice) {
		String result = command;
		switch(command){
		case CHOICE_COMMAND_SHORT_NAME:
			result = choice.getName();
			break;
		case CHOICE_COMMAND_FULL_NAME:
			result = choice.getQualifiedName();
			break;
		case CHOICE_COMMAND_VALUE:
			result = getValue(choice);
			break;
		default:
			break;
		}
		return result;
	}

	private static String getValue(ChoiceNode choice) {
		String convertedValue = convertValue(choice);
		if (convertedValue != null) {
			return convertedValue;
		}
		return choice.getValueString();
	}
	private static String convertValue(ChoiceNode choice) {
		AbstractParameterNode parameter = choice.getParameter();
		if (parameter == null) {
			return null;
		}

		String argType = choice.getParameter().getType();
		if (argType == null) {
			return null;
		}

		Object parsedObject = ChoiceValueParser.parseValueOfJavaType(choice.getValueString(), argType);
		if (parsedObject == null) {
			return null;
		}

		return parsedObject.toString();
	}

}