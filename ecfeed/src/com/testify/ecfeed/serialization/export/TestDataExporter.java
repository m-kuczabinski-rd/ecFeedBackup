package com.testify.ecfeed.serialization.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

public class TestDataExporter {
	
	private static final CharSequence CLASS_NAME_SEQUENCE = "%class";
	private static final CharSequence PACKAGE_NAME_SEQUENCE = "%package";
	private static final CharSequence METHOD_NAME_SEQUENCE = "%method";
	private static final CharSequence TEST_SUITE_NAME_SEQUENCE = "%suite";
	private static final String TEST_CASE_SEQUENCE_GENERIC_PATTERN = "#\\d+\\.[name|choice|value]";

	public void export(MethodNode method, Collection<TestCaseNode> testCases, String headerTemplate, 
			String testCaseTemplate, String tailTemplate, String file) throws IOException{
		FileOutputStream os = new FileOutputStream(file);
		os.write(generateSection(method, headerTemplate).getBytes());
		for(TestCaseNode testCase : testCases){
			os.write(generateTestCaseString(testCase, testCaseTemplate).getBytes());
		}
		os.write(generateSection(method, headerTemplate).getBytes());
		os.close();
	}

	private String generateSection(MethodNode method, String template) {
		String result = template.replace(CLASS_NAME_SEQUENCE, JavaUtils.getLocalName(method.getClassNode()));
		result = result.replace(PACKAGE_NAME_SEQUENCE, JavaUtils.getPackageName(method.getClassNode()));
		result = result.replace(METHOD_NAME_SEQUENCE, method.getName());

		return result;
	}

	private String generateTestCaseString(TestCaseNode testCase, String template) {
		String result = generateSection(testCase.getMethod(), template);
		List<String> matches = new ArrayList<String>();
		Matcher m = Pattern.compile(TEST_CASE_SEQUENCE_GENERIC_PATTERN).matcher(template);
		while(m.find()){
			matches.add(m.group());
		}
		for(String regex : matches){
			
		}
		result = result.replace(TEST_SUITE_NAME_SEQUENCE, testCase.getName());
		return result;
	}

}
