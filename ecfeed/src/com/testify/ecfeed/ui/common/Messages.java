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

package com.testify.ecfeed.ui.common;


public class Messages {
	public static final String EXCEPTION_METHOD_IMPORT(String name){
		return "Unexpected problems with importing method " + name;
	}
	public static final String EXCEPTION_CLASS_IMPORT(String name){
		return "Unexpected problems with importing class " + name;
	}
	public static final String EXCEPTION_TYPE_DOES_NOT_EXIST_IN_THE_PROJECT = "The imported type does not exist in the project";

	
	public static final String DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE = "Name of a test suite must be between 1 and 64 characters long.";
	public static final String DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE_NO_WHITESPACE = "Name of a test suite cannot consist of whitespace characters only.";
	public static final String DIALOG_PARTITION_VALUE_PROBLEM_TITLE = "Wrong partition value";
	public static final String DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE(String value){
		return "Value " + value + " is not valid for given category.\n" + 
				"Partition value must fit to type and range of the variable " +
				"represented by the partition, contain between 1 and 64 characters.\n" +
				"Partitions of user defined type must follow Java enum defining rules.";
	}
	public static final String DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE = "Test suite name must contain between 1 and 64 characters " +
			"and may not contain only white characters.";
	public static final String DIALOG_TEST_CLASS_SELECTION_TITLE = "Test class selection";
	public static final String DIALOG_TEST_CLASS_SELECTION_MESSAGE = "Select class to import";
	public static final String DIALOG_RENAME_METHOD_TITLE = "Select compatible method";
	public static final String DIALOG_RENAME_METHOD_MESSAGE = "Select compatible method from the list.";
	public static final String DIALOG_ADD_TEST_CASE_TITLE = "New test case";
	public static final String DIALOG_ADD_TEST_CASE_MESSAGE = "Set test suite name and edit test data.";
	public static final String DIALOG_CALCULATE_COVERAGE_TITLE = "Calculate n-wise coverage";
	public static final String DIALOG_CALCULATE_COVERAGE_MESSAGE = "Select test cases to include in evaluation.";

	public static final String WIZARD_NEW_ECT_FILE_TITLE = "New Equivalence Class Model";
	public static final String WIZARD_NEW_ECT_FILE_MESSAGE = "Create new file with equivalence class model";
	public static final String WIZARD_FILE_EXISTS_TITLE = "File exists";
	public static final String WIZARD_FILE_EXISTS_MESSAGE = "File with specified name already exists in this container. "
			+ "Do you want to overwrite it?";
	public static final String DIALOG_GENERATE_TEST_SUITE_TITLE = "Generate test suite";
	public static final String DIALOG_GENERATE_TEST_SUITE_MESSAGE = "Select test suite name and algorithm for test suite generation";
	public static final String DIALOG_EXECUTE_ONLINE_TITLE = "Execute online test";
	public static final String DIALOG_EXECUTE_ONLINE_MESSAGE = "Setup the test data generator and select which constraints and partitions shall be considered for generating test cases";
}
