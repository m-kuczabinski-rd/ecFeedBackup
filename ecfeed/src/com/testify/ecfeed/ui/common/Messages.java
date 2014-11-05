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
	public static final String DIALOG_choice_VALUE_PROBLEM_TITLE = "Wrong choice value";
	public static final String DIALOG_choice_VALUE_PROBLEM_MESSAGE(String value){
		return "Value " + value + " is not valid for given category.\n" +
				"choice value must fit to type and range of the variable " +
				"represented by the choice, contain between 1 and 64 characters.\n" +
				"choices of user defined type must follow Java enum defining rules.";
	}
	public static final String DIALOG_TEST_CLASS_SELECTION_TITLE = "Test class selection";
	public static final String DIALOG_TEST_CLASS_SELECTION_MESSAGE = "Select class";
	public static final String DIALOG_IMPORT_TEST_CLASS_SELECTION_MESSAGE = "Select class to import";
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
	public static final String DIALOG_EXECUTE_ONLINE_MESSAGE = "Setup the test data generator and select which constraints and choices shall be considered for generating test cases";

	//PROBLEM COMMUNICATES
	public static final String DIALOG_RENAME_MODEL_PROBLEM_TITLE = "Couldn't rename model";
	public static final String DIALOG_ADD_NEW_CLASS_PROBLEM_TITLE = "Couldn't add new class";
	public static final String DIALOG_REMOVE_CLASSES_PROBLEM_TITLE = "Couldn't remove classes";
	public static final String DIALOG_RENAME_CLASS_PROBLEM_TITLE = "Couldn't rename class";
	public static final String DIALOG_MOVE_NODE_PROBLEM_TITLE = "Couldn't move node";
	public static final String DIALOG_ADD_METHOD_PROBLEM_TITLE = "Couldn't add new method";
	public static final String DIALOG_ADD_METHODS_PROBLEM_TITLE = "Couldn't add all methods";
	public static final String DIALOG_REMOVE_METHOD_PROBLEM_TITLE = "Couldn't remove requested method";
	public static final String DIALOG_REMOVE_METHODS_PROBLEM_TITLE = "Couldn't remove some of requested methods";
	public static final String DIALOG_RENAME_METHOD_PROBLEM_TITLE = "Couldn't rename method";
	public static final String DIALOG_CONVERT_METHOD_PROBLEM_TITLE = "Couldn't convert method";
	public static final String DIALOG_UNEXPECTED_PROBLEM_WITH_TEST_EXECUTION = "Unexpected problem with test execution";
	public static final String DIALOG_SET_CATEGORY_EXPECTED_PROBLEM_TITLE = "Couldn't change category expected status";
	public static final String DIALOG_SET_DEFAULT_VALUE_PROBLEM_TITLE = "Couldn't change default value for category";
	public static final String DIALOG_REMOVE_PARAMETERS_PROBLEM_TITLE = "Couldn't remove parameters";
	public static final String DIALOG_ADD_CONSTRAINT_PROBLEM_TITLE = "Couldn't add constraint";
	public static final String DIALOG_REMOVE_CONSTRAINTS_PROBLEM_TITLE = "Couldn't remove constraints";
	public static final String DIALOG_ADD_TEST_CASE_PROBLEM_TITLE = "Cannot add test case";
	public static final String DIALOG_TEST_CASE_WITH_EMPTY_CATEGORY_MESSAGE = "Some non-expected parameters have empty choice set.";
	public static final String DIALOG_RENAME_TEST_SUITE_PROBLEM = "Couldn't rename test cases";
	public static final String DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE = "Couldn't remove some of requested test cases";
	public static final String DIALOG_ADD_TEST_SUITE_PROBLEM_TITLE = "Couldn't add test cases";
	public static final String DIALOG_EMPTY_TEST_SUITE_GENERATED_MESSAGE = "The generated test suite was empty";
	public static final String DIALOG_TEST_EXECUTION_PROBLEM_TITLE = "Test execution problem";
	public static final String DIALOG_TEST_EXECUTION_REPORT_TITLE = "Test execution report";
	public static final String DIALOG_UPDATE_TEST_DATA_PROBLEM_TITLE = "Couldn't update test data";
	public static final String DIALOG_ADD_STATEMENT_PROBLEM_TITLE = "Couldn't add statement";
	public static final String DIALOG_REMOVE_STATEMENT_PROBLEM_TITLE = "Couldn't remove statement";
	public static final String DIALOG_EDIT_STATEMENT_PROBLEM_TITLE = "Couldn't edit statement";
	public static final String DIALOG_ADD_choice_PROBLEM_TITLE = "Couldn't add choice";
	public static final String DIALOG_REMOVE_choice_TITLE = "Couldn't remove choice";
	public static final String DIALOG_REMOVE_choiceS_PROBLEM_TITLE = "Couldn't remove some of requested patitions";
	public static final String DIALOG_RENAME_PAREMETER_PROBLEM_TITLE = "Couldn't rename parameter";
	public static final String DIALOG_RENAME_choice_PROBLEM_TITLE = "Couldn't rename choice";
	public static final String DIALOG_SET_choice_VALUE_PROBLEM_TITLE = "Couldn't change choice value";
	public static final String DIALOG_REMOVE_LABEL_PROBLEM_TITLE = "Couldn't remove label";
	public static final String DIALOG_ADD_LABEL_PROBLEM_TITLE = "Cannot add label";
	public static final String DIALOG_CHANGE_LABEL_PROBLEM_TITLE = "Cannot change label";
	public static final String DIALOG_REMOVE_NODE_PROBLEM_TITLE = "Connot remove element";
	public static final String DIALOG_REMOVE_NODES_PROBLEM_TITLE = "Connot remove elements";
	public static final String DIALOG_ADD_CHILDREN_PROBLEM_TITLE = "Cannot add children elements";
	public static final String DIALOG_RENAME_CONSTRAINT_PROBLEM_TITLE = "Cannot rename constraint";

	//CONFIRMATIONS AND WARNINGS
	public static final String DIALOG_REMOVE_CLASSES_TITLE = "Remove classes";
	public static final String DIALOG_REMOVE_CLASSES_MESSAGE = "This operation will remove selected test classes from the model.";
	public static final String DIALOG_REMOVE_CLASS_TITLE = "Remove class";
	public static final String DIALOG_REMOVE_CLASS_MESSAGE = "This operation will remove selected test class from the model.";
	public static final String DIALOG_RENAME_IMPLEMENTED_CLASS_TITLE = "Renaming implemented class";
	public static final String DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE = "Renaming implemented class may cause the test cases to be not executable.";
	public static final String DIALOG_REMOVE_METHOD_TITLE = "Remove method";
	public static final String DIALOG_REMOVE_METHOD_MESSAGE = "This operation will remove selected test method from the model.";
	public static final String DIALOG_REMOVE_METHODS_TITLE = "Remove method";
	public static final String DIALOG_REMOVE_METHODS_MESSAGE = "This operation will remove selected test methods from the model.";
	public static final String DIALOG_SET_CATEGORY_EXPECTED_WARNING_TITLE = "Change parameter's expected property";
	public static final String DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_REMOVED = "All test cases will be removed.";
	public static final String DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_ALTERED = "Corresponding test parameter in all test cases will be replaced by default value.";
	public static final String DIALOG_SET_CATEGORY_EXPECTED_CONSTRAINTS_REMOVED = "All constraints that refer to that parameter will be removed.";
	public static final String DIALOG_REMOVE_PARAMETERS_WARNING_TITLE = "Remove parameters";
	public static final String DIALOG_REMOVE_PARAMETERS_WARNING_MESSAGE = "Removing selected parameters will result in removing all test cases and constraints that refer to removed parameters.";
	public static final String DIALOG_LARGE_TEST_SUITE_GENERATED_TITLE = "Large size of generated data";
	public static final String DIALOG_LARGE_TEST_SUITE_GENERATED_MESSAGE(int length) {
		return "The algortithm generated " + length
				+ " test cases. Adding this amount of data to the model may heavily affect tool's performance"
				+ " and cause loss of data. Do you want to continue?";
	}
	public static final String DIALOG_REMOVE_choice_WARNING_TITLE = "Removing choice";
	public static final String DIALOG_REMOVE_choice_WARNING_MESSAGE = "Removing choice will cause removing of all test cases and constraints referring to that choice";
	public static final String DIALOG_REMOVE_LABELS_WARNING_TITLE = "Removing labels";
	public static final String DIALOG_REMOVE_LABELS_WARNING_MESSAGE = "Removing labels will result in removing constraints that refer to the labels.";
	public static final String DIALOG_RENAME_LABELS_ERROR_TITLE = "Could't rename label";
	public static final String DIALOG_LABEL_IS_ALREADY_INHERITED = "The label is already inherited from a parent choice";
	public static final String DIALOG_RENAME_LABELS_WARNING_TITLE = "Renaming label";

	//OTHER MESSAGES
	public static final String EXECUTING_TEST_WITH_PARAMETERS = "Executing test function with generated parameters";
	public static final String EXECUTING_TEST_WITH_NO_PARAMETERS = "Executing test function";

	public static final String DIALOG_GENERATOR_INPUT_PROBLEM_MESSAGE = "At least one choice per category must be check";
	public static final String DIALOG_GENERATOR_EXECUTABLE_INPUT_PROBLEM_MESSAGE = "At least one choice per category must be check. All checked categories must be implemented";
	public static final String DIALOG_GENERATE_TEST_SUITES_SELECT_CONSTRAINTS_LABEL = "Select constraints considered when generating test suite";
	public static final String DIALOG_GENERATE_TEST_SUITES_SELECT_CHOICES_LABEL = "Select which choices will be considered for generating test suite. "
			+ "Each category must be represented by at least one choice.";
	public static final String DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE = "Entered test suite name not allowed";
	public static final String DIALOG_DESCENDING_LABELS_WILL_BE_REMOVED_WARNING_TITLE = "choices below in the hierarchy are already labeled with this label. Changing the label will result in removing the labels from all that choices";
	public static String DIALOG_UNSUCCESFUL_TEST_EXECUTION(int totalTestCases, int unsuccesfull) {
		return String.valueOf(unsuccesfull) + " of " + totalTestCases + " test cases have met problems during execution.";
	}
	public static String DIALOG_SUCCESFUL_TEST_EXECUTION(int totalTestCases) {
		return "All of " + totalTestCases + " test cases were succesfully executed.";
	}
}
