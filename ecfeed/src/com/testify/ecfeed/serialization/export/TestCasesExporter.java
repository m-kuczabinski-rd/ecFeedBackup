package com.testify.ecfeed.serialization.export;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.TestCaseNode;

public class TestCasesExporter {
	
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

	OutputStream fOutputStream;
	int fExportedTestCases;
	String fHeaderTemplate;
	String fTestCaseTemplate;
	String fTailTemplate;
	
	
	public TestCasesExporter(String file, String headerTemplate, String testCaseTemplate, String tailTemplate) 
			throws FileNotFoundException {
		 fOutputStream = new FileOutputStream(file);
		 fExportedTestCases = 0;
		 
		 fHeaderTemplate = headerTemplate;
		 fTestCaseTemplate = testCaseTemplate;
		 fTailTemplate = tailTemplate;
	}

	public void exportHeader(MethodNode method) throws IOException{
		if (fHeaderTemplate != null) {
			fOutputStream.write(generateSection(method, fHeaderTemplate).getBytes());
		}
		
		fExportedTestCases = 0;
	}
	
	public void exportTail(MethodNode method) throws IOException{
		if(fTailTemplate != null){
			fOutputStream.write(generateSection(method, fTailTemplate).getBytes());
		}
	}	

	public void exportTestCase(TestCaseNode testCase) throws IOException{
		if(fTestCaseTemplate != null){
			fOutputStream.write(generateTestCaseString(fExportedTestCases, testCase, fTestCaseTemplate).getBytes());
			++fExportedTestCases; 
		}
	}

	public void exportTestCases(MethodNode method, Collection<TestCaseNode> testCases) throws IOException{
		exportHeader(method);
		
		if (fTestCaseTemplate != null) {
			for (TestCaseNode testCase : testCases)	{
				exportTestCase(testCase);
			}
		}

		exportTail(method);
		fOutputStream.close();
	}

	private String generateSection(MethodNode method, String template) {
		String result = template.replace(CLASS_NAME_SEQUENCE, JavaUtils.getLocalName(method.getClassNode()));
		result = result.replace(PACKAGE_NAME_SEQUENCE, JavaUtils.getPackageName(method.getClassNode()));
		result = result.replace(METHOD_NAME_SEQUENCE, method.getName());
		result = replaceParameterSequences(method, result);
		result = evaluateExpressions(result);

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
		result = replaceTestParameterSequences(testCase, result);
		result = result.replace(TEST_CASE_INDEX_NAME_SEQUENCE, String.valueOf(index));
		result = result.replace(TEST_SUITE_NAME_SEQUENCE, testCase.getName());
		result = evaluateExpressions(result);
		return result;
	}

	private String evaluateExpressions(String template) {
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

	public void close() throws IOException {
		fOutputStream.close();
	}
}
