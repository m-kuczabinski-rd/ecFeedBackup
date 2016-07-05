package com.ecfeed.spec.gui;

import com.ecfeed.testutils.ETypeName;


public class TestTestCaseDetailsPage{

	public void RenameSuiteTest(String suiteName, boolean evaluation){
		// TODO Auto-generated method stub
		System.out.println("RenameSuiteTest(" + suiteName + ", " + evaluation + ")");
	}

	/* 
	 * Fixed scenario with several predefined setups:
	 * - check if test case of implemented methods and referring to implemented choices are executable
	 * - check if test case of not implemented method, but with implemented choices is NOT executable
	 * - test "implement" button with user type - add one value that is not implemented yet and
	 * 		create test case with that choice. Ensure that button is enable only in that case and that enum const
	 * - is properly implemented when the button is pressed.
	 */
	public void testCaseImplementationTest(){
		// TODO Auto-generated method stub
		System.out.println("testCaseImplementationTest()");
	}

	/*
	 * Similar to other "edit value" tests. Just boolean and user type need different approach here.
	 */
	public void changeExpectedParameterValueTest(ETypeName type, String newValue, boolean isConvertable){
		// TODO Auto-generated method stub
		System.out.println("changeTestCaseParameterValueTest(" + type + ", " + newValue + ", " + isConvertable + ")");
	}
	
	/*
	 * Create parameter with at least two choices, then ensure all of them are available for selection while editing value.
	 */
	public void changePartitionedParameterValueTest(ETypeName type){
		
	}
	
	/*
	 * Value represents predefined value for certain type, ex. MIN_VALUE for int. If value combo doesn't contain it - fail test.
	 */
	public void predefinedExpectedValuesTest(ETypeName type, String value){
		
	}

}

