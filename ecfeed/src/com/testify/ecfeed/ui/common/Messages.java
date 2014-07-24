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

import com.testify.ecfeed.utils.Constants;

public class Messages {
	public static final String DIALOG_REMOVE_CLASSES_TITLE = "Remove classes"; 
	public static final String DIALOG_REMOVE_CLASSES_MESSAGE = "This operation will remove selected test classes " +
			"from the model. All generated test cases will be permanently deleted.";
	public static final String DIALOG_REMOVE_CLASS_TITLE = "Remove classes"; 
	public static final String DIALOG_REMOVE_CLASS_MESSAGE = "This operation will remove this test class " +
			"from the model. All generated test cases will be permanently deleted.";
	public static final String DIALOG_REMOVE_TEST_CASES_TITLE = "Remove test cases";
	public static final String DIALOG_REMOVE_TEST_CASES_MESSAGE = "Delete selected test cases?";

	public static final String DIALOG_REMOVE_CONSTRAINTS_TITLE = "Remove constraints";
	public static final String DIALOG_REMOVE_CONSTRAINTS_MESSAGE = "Remove selected constraints from model?";

	public static final String DIALOG_REMOVE_METHODS_TITLE = "Remove methods";
	public static final String DIALOG_REMOVE_METHODS_MESSAGE = "Remove selected methods from the model?\n" +
			"All generated test cases will be lost.";
	public static final String DIALOG_REMOVE_TEST_SUITES_TITLE = "Remove test suites";
	public static final String DIALOG_REMOVE_TEST_SUITES_MESSAGE = "Select test suites to remove.";
	public static final String DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE = "Name of a test suite must be between 1 and 64 characters long.";
	public static final String DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE_NO_WHITESPACE = "Name of a test suite cannot consist of whitespace characters only.";
	public static final String DIALOG_PARTITION_NAME_PROBLEM_TITLE = "Wrong partition name";
	public static final String DIALOG_PARTITION_NAME_PROBLEM_MESSAGE = "Partition name must have between 1 and 64 characters, " +
			"must not contain only white characters and must be unique within category.";
	public static final String DIALOG_PARTITION_VALUE_PROBLEM_TITLE = "Wrong partition value";
	public static final String DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE = "Partition value must fit to type and range of the variable " +
			"represented by the partition, contain between 1 and 64 characters.\n" +
			"Partitions of user defined type must follow Java enum defining rules.";
	public static final String DIALOG_REMOVE_PARTITIONS_TITLE = "Remove partitions";
	public static final String DIALOG_REMOVE_PARTITIONS_MESSAGE = "Removing partitions will remove also all test cases " +
			"referencing those partitions. Are you sure that you want to proceed?";
	public static final String DIALOG_PARTITION_SETTINGS_DIALOG_TITLE = "Partition settings";
	public static final String DIALOG_PARTITION_SETTINGS_DIALOG_MESSAGE = "Enter partition name and value.";
	public static final String DIALOG_TEST_SUITE_NAME_PROBLEM_TITLE = "Wrong test suite name";
	public static final String DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE = "Test suite name must contain between 1 and 64 characters " +
			"and may not contain only white characters.";
	public static final String DIALOG_CLASS_EXISTS_TITLE = "Class exists";
	public static final String DIALOG_CLASS_EXISTS_MESSAGE = "Selected class is already contained in the model.";
	public static final String DIALOG_RENAME_MODEL_TITLE = "Rename model";
	public static final String DIALOG_RENAME_MODEL_MESSAGE = "Enter new name for the model. This name has only aesthetic purposes, " +
			"so feel free to provide any name you want. "
			+ "\nTo have some minimal tidiness however, keep the name between 1 and 64 characters long and let it not "
			+ "contain white characters only.";
	public static final String DIALOG_TEST_CLASS_SELECTION_TITLE = "Test class selection";
	public static final String DIALOG_TEST_CLASS_SELECTION_MESSAGE = "Select class to import";
	public static final String DIALOG_TEST_METHOD_SELECTION_TITLE = "Test method selection";
	public static final String DIALOG_TEST_METHOD_SELECTION_MESSAGE = "Select a method annotated with @Test with the same arguments set" +
			"as the original method.";
	public static final String DIALOG_RENAME_METHOD_TITLE = "Rename method";
	public static final String DIALOG_RENAME_METHOD_MESSAGE = "Select compatible method from the list.";
	public static final String DIALOG_ADD_TEST_CASE_TITLE = "New test case";
	public static final String DIALOG_ADD_TEST_CASE_MESSAGE = "Set test suite name and edit test data.";
	public static final String DIALOG_REMOVE_LAST_PARTITION_TITLE = "Cannot remove partition";
	public static final String DIALOG_REMOVE_LAST_PARTITION_MESSAGE = "A category must have at least one partition.";
	public static final String DIALOG_SELECT_CONTAINER_FOR_NEW_ECT_FILE_TITLE = "Select container for the new file";
	
	public static final String DIALOG_CALCULATE_COVERAGE_TITLE = "Calculate n-wise coverage";
	public static final String DIALOG_CALCULATE_COVERAGE_MESSAGE = "Select test cases to include in evaluation.";

	public static final String WIZARD_NEW_ECT_FILE_TITLE = "New Equivalence Class Model";
	public static final String WIZARD_NEW_ECT_FILE_MESSAGE = "Create new file with equivalence class model";
	public static final String WIZARD_UNSPECIFIED_CONTAINER_MESAGE = "File container must be specified.";
	public static final String WIZARD_CONTAINER_DOES_NOT_EXIST_MESAGE = "Specified container does not exist.";
	public static final String WIZARD_CONTAINER_NOT_ACCESSIBLE = "The specified container is not writeable.";
	public static final String WIZARD_FILE_NAME_NOT_SPECIFIED = "Please enter name of the new ect file.";
	public static final String WIZARD_WRONG_ECT_FILE_NAME = "File name is not valid.";
	public static final String WIZARD_WRONG_ECT_FILE_EXTENSION = "File extension must be \"" + Constants.EQUIVALENCE_CLASS_FILE_EXTENSION + "\"";
	public static final String WIZARD_FILE_EXISTS_TITLE = "File exists";
	public static final String WIZARD_FILE_EXISTS_MESSAGE = "File with specified name already exists in this container. "
			+ "Do you want to overwrite it?";
	public static final String DIALOG_GENERATE_TEST_SUITE_TITLE = "Generate test suite";
	public static final String DIALOG_GENERATE_TEST_SUITE_MESSAGE = "Select test suite name and algorithm for test suite generation";
	public static final String DIALOG_EMPTY_TEST_SUITE_GENERATED_TITLE = "No test data generated";
	public static final String DIALOG_EMPTY_TEST_SUITE_GENERATED_MESSAGE = "The algorithm generated empty test data set for this method";
	public static final String DIALOG_ADD_CONSTRAINT_TITLE = "Add constraint";
	public static final String DIALOG_ADD_CONSTRAINT_MESSAGE = "Enter constraint's name and define its premise and consequence";
	public static final String DIALOG_CONSTRAINT_NAME_PROBLEM_TITLE = "Wrong constraint name.";
	public static final String DIALOG_CONSTRAINT_NAME_PROBLEM_MESSAGE = "Name of a constrant must be between 1 and 64 characters long.";
	public static final String DIALOG_GENERATE_TEST_SUITES_SELECT_CONSTRAINTS_LABEL = "Select constraints considered when generating test suite";
	public static final String DIALOG_GENERATE_TEST_SUITES_SELECT_PARTITIONS_LABEL = "Select which partitions will be considered for generating test suite. "
			+ "Each category must be represented by at least one partition.";
	public static final String DIALOG_LARGE_TEST_SUITE_GENERATED_TITLE = "Large size of generated data";
	public static final String DIALOG_TEST_GENERATOR_EXCEPTION_TITLE = "Test generator issue";
	public static final String DIALOG_CANNOT_ADD_LABEL_TITLE = "Cannot add label for partition";
	public static final String DIALOG_CANNOT_ADD_LABEL_MESSAGE = "New label could not be added to the partition";
	public static final String DIALOG_CANNOT_REMOVE_LABEL_TITLE = "Could not remove label";
	public static final String DIALOG_COULDNT_LOAD_TEST_CLASS_TITLE = "Could not load test class";
	public static String DIALOG_COULDNT_LOAD_TEST_CLASS_MESSAGE(String className) {
		return "The test class " + className + " could not be loaded. Make sure that the class is compiled and the "
				+ ".class file is the output folder. Try to enable 'Build automatically' option in Projet menu.";
	}
	public static final String DIALOG_COULDNT_LOAD_TEST_METHOD_TITLE = "Could not find test method";
	public static String DIALOG_COULDNT_LOAD_TEST_METHOD_MESSAGE(String method) {
		return "The test method " + method + " could not be found in the loaded test class. Make sure that such a method " + 
				"is defined in the class and that there is only one such class defined in the workspace.";
	}
	public static final String DIALOG_CANNOT_REMOVE_LABEL_MESSAGE(String label){return "The label " + label + " coud not be removed";}
	public static final String DIALOG_LARGE_TEST_SUITE_GENERATED_MESSAGE(int length) {
		return "The algortithm generated " + length + " test cases. Adding this amount of data to the model may heavily affect tool's performance"
				+ " and cause loss of data. Do you want to continue?";
	}
	public static final String DIALOG_TEST_METHOD_EXECUTION_STOPPED_TITLE = "Unexpected test function execution exit";
	public static String DIALOG_TEST_METHOD_EXECUTION_STOPPED_MESSAGE(String function, String exceptionMessage) {
		return "Execution of test function " + function + " stopped:\n\n" + exceptionMessage;
	}
	public static final String DIALOG_EXECUTE_ONLINE_TITLE = "Execute online test";
	public static final String DIALOG_EXECUTE_ONLINE_MESSAGE = "Setup the test data generator and select which constraints and partitions shall be considered for generating test cases";
	public static final String DIALOG_TEST_CLASS_NAME_ERROR_MESSAGE = "Name of a test class must be between 1 and 64 characters long.";
	public static final String DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE = "You are about to rename implemented class. Continue?";
	public static final String DIALOG_METHOD_INVALID_NAME_TITLE = "Invalid method name";
	public static final String DIALOG_METHOD_INVALID_NAME_MESSAGE = "Entered method name is not valid.";
	public static final String DIALOG_REMOVE_PARAMETERS_TITLE = "Remove parameters";
	public static final String DIALOG_REMOVE_PARAMETERS_MESSAGE = "This operation will remove selected parameters and all correlated data from the method.";
	public static final String DIALOG_DATA_MIGHT_BE_LOST_TITLE = "Correlated data might be lost.";
	public static final String DIALOG_DATA_MIGHT_BE_LOST_MESSAGE = "This operation might cause loss of data, i.e. generated test cases.\nProceed?";
	public static final String DIALOG_CATEGORY_EXISTS_TITLE = "Category exists";
	public static final String DIALOG_CATEGORY_EXISTS_MESSAGE = "Entered category is already contained in the method.";
	public static final String DIALOG_CLASS_NAME_PROBLEM_TITLE = "Wrong class name";
	public static final String DIALOG_CLASS_NAME_PROBLEM_MESSAGE = "Class name must contain between 1 and 64 characters " +
			"and fulfill java class naming rules.";
	public static final String DIALOG_PACKAGE_NAME_PROBLEM_TITLE = "Wrong package";
	public static final String DIALOG_PACKAGE_NAME_PROBLEM_MESSAGE = "Package must fulfill java package naming rules.";
	public static final String DIALOG_METHOD_NAME_PROBLEM_TITLE = "Wrong method name";
	public static final String DIALOG_METHOD_NAME_PROBLEM_MESSAGE = "Method name must contain between 1 and 64 characters " +
			"and may not contain only white characters.";
	public static final String DIALOG_PARAMETER_NAME_PROBLEM_TITLE = "Wrong parameter name";
	public static final String DIALOG_PARAMETER_NAME_PROBLEM_MESSAGE = "Parameter name must contain between 1 and 64 characters " +
			"and may not contain only white characters.";
	public static final String DIALOG_PARAMETER_TYPE_PROBLEM_TITLE = "Wrong parameter type name";
	public static final String DIALOG_PARAMETER_TYPE_PROBLEM_MESSAGE = "Parameter type name must contain between 1 and 64 characters " +
			"and must not contain white characters.";
	public static final String DIALOG_METHOD_EXISTS_TITLE = "Method exists";
	public static final String DIALOG_METHOD_WITH_PARAMETERS_EXISTS_MESSAGE = "Method with the same name and parameter types already exists in the class.";
	public static final String DIALOG_PASTE_OPERATION_FAILED_TITLE = "Paste failed";
	public static final String DIALOG_PASTE_OPERATION_FAILED_MESSAGE = "Clipboard content doesn't match here.";
	public static final String DIALOG_PROJECT_SELECTION_TITLE = "Project selection";
	public static final String DIALOG_PROJECT_SELECTION_MESSAGE = "Select project for implementation";
}
