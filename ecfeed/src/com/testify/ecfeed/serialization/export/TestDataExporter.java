package com.testify.ecfeed.serialization.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.TestCaseNode;

public class TestDataExporter {
	
	private static final String CLASS_NAME_SEQUENCE = "%class";
	private static final String PACKAGE_NAME_SEQUENCE = "%package";
	private static final String METHOD_NAME_SEQUENCE = "%method";
	private static final String TEST_SUITE_NAME_SEQUENCE = "%suite";
	private static final String TEST_CASE_INDEX_NAME_SEQUENCE = "%index";
	private static final String PARAMETER_COMMAND_NAME = "name";
	private static final String CHOICE_COMMAND_SHORT_NAME = "choice";
	private static final String CHOICE_COMMAND_FULL_NAME = "full_choice";
	private static final String CHOICE_COMMAND_VALUE = "value";
	private static final String TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN = "#\\d+\\.(" + CHOICE_COMMAND_SHORT_NAME + "|" + CHOICE_COMMAND_FULL_NAME + "|" + CHOICE_COMMAND_VALUE + ")";
	private static final String METHOD_PARAMETER_SEQUENCE_GENERIC_PATTERN = "#\\d+\\." + PARAMETER_COMMAND_NAME;

	public void export(MethodNode method, Collection<TestCaseNode> testCases, String headerTemplate, 
			String testCaseTemplate, String tailTemplate, String file) throws IOException{
		FileOutputStream os = new FileOutputStream(file);
		os.write(generateSection(method, headerTemplate).getBytes());
		Iterator<TestCaseNode> iterator = testCases.iterator();
		for(int i = 0; i < testCases.size(); ++i){
			TestCaseNode testCase = iterator.next();
			os.write(generateTestCaseString(i, testCase, testCaseTemplate).getBytes());
		}
		os.write(generateSection(method, headerTemplate).getBytes());
		os.close();
	}

	private String generateSection(MethodNode method, String template) {
		String result = template.replace(CLASS_NAME_SEQUENCE, JavaUtils.getLocalName(method.getClassNode()));
		result = result.replace(PACKAGE_NAME_SEQUENCE, JavaUtils.getPackageName(method.getClassNode()));
		result = result.replace(METHOD_NAME_SEQUENCE, method.getName());
		result = replaceParameterSequences(method, result);

		return result;
	}

	private String replaceParameterSequences(MethodNode method, String template) {
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

	private String resolveParameterCommand(String command, MethodParameterNode parameter) {
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

	private String getParameterCommand(String parameterCommandSequence) {
		return parameterCommandSequence.substring(parameterCommandSequence.indexOf(".") + 1, parameterCommandSequence.length());
	}

	private int getParameterNumber(String parameterSequence) {
		String parameterNumberString = parameterSequence.substring(1, parameterSequence.indexOf("."));
		return Integer.parseInt(parameterNumberString);
	}

	private String generateTestCaseString(int index, TestCaseNode testCase, String template) {
		MethodNode method = testCase.getMethod();
		String result = generateSection(method, template);
		result = replaceTestParameterSequences(testCase, template);
		result = result.replace(TEST_CASE_INDEX_NAME_SEQUENCE, String.valueOf(index));
		result = result.replace(TEST_SUITE_NAME_SEQUENCE, testCase.getName());
		return result;
	}

	private String replaceTestParameterSequences(TestCaseNode testCase, String template) {
		String result = replaceParameterSequences(testCase.getMethod(), template);
		Matcher m = Pattern.compile(TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN).matcher(template);
		while(m.find()){
			String parameterCommandSequence = m.group();
			String command = getParameterCommand(parameterCommandSequence);
			int parameterNumber = getParameterNumber(parameterCommandSequence) - 1;
			ChoiceNode choice = testCase.getTestData().get(parameterNumber);
			String substitute = resolveChoiceCommand(command, choice);
			result = result.replace(parameterCommandSequence, substitute);
		}		
		return result;
	}

	private String resolveChoiceCommand(String command, ChoiceNode choice) {
		String result = command;
		switch(command){
		case CHOICE_COMMAND_SHORT_NAME:
			result = choice.getName();
			break;
		case CHOICE_COMMAND_FULL_NAME:
			result = choice.getQualifiedName();
			break;
		case CHOICE_COMMAND_VALUE:
			result = choice.getValueString();
			break;
		default:
			break;
		}
		return result;
	}
}
