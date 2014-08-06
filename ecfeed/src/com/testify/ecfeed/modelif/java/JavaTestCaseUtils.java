package com.testify.ecfeed.modelif.java;

public class JavaTestCaseUtils {
	public static boolean validateTestCaseName(String name){
		return name.matches(Constants.REGEX_TEST_CASE_NODE_NAME);
	}
}
