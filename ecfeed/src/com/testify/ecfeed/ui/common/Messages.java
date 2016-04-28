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
	public static final String MODEL_SOURCE_SIZE_EXCEEDED(int max_size){
		return "Model exceeds " + max_size + " items. For performance reasons it can not be displayed.";
	}

	public static final String EXCEPTION_METHOD_IMPORT(String name){
		return "Unexpected problems with importing method " + name;
	}
	public static final String EXCEPTION_CLASS_IMPORT(String name){
		return "Unexpected problems with importing class " + name;
	}
	public static final String EXCEPTION_TYPE_DOES_NOT_EXIST_IN_THE_PROJECT = "The imported type does not exist in the project";

	public static final String EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL = "File info provider must not be null.";

	public static final String EXCEPTION_FILE_NOT_FOUND_IN_INSTALLATION_DIR(String relativePath) {
		return "Can not find: " + relativePath + " in ecFeed installation directory."; 
	}

	public static final String EXCEPTION_FILE_NOT_FOUND_IN_INSTALLATION_DIR2(String relativePath, String installationDir) {
		return "Can not find: " + relativePath + " in ecFeed installation directory: " + installationDir; 
	}	

	public static final String EXCEPTION_ANDROID_METHOD_INVOKER_NOT_FOUND = 
			"External android method invoker not found (not installed).";

	public static final String EXCEPTION_EXTERNAL_IMPLEMENTER_NOT_FOUND = 
			"External android implementer not found (not installed).";

	public static final String EXCEPTION_EXTERNAL_DEVICE_CHECKER_NOT_FOUND = 
			"External device checker not found (not installed).";

	public static final String EXCEPTION_EXTERNAL_APK_INSTALLER_NOT_FOUND = 
			"External Android application installer not found (not installed).";	

	public static final String EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS = 
			"Can not remove selected items.";

	public static final String EXCEPTION_CAN_NOT_EXPORT = "Can not export.";

	public static final String EXCEPTION_CAN_NOT_IMPORT = "Can not export.";

	public static final String EXCEPTION_NO_REFERENCED_PROJECTS = "Testing project has no referenced (tested) projects.";

	public static final String EXCEPTION_TOO_MANY_REFERENCED_PROJECTS = "Testing project has too many referenced projects (only 1 - tested project is allowed).";

	public static final String EXCEPTION_NO_APK_NAME_PROJECT_NULL = "Can not create apk name. Project is null.";

	public static final String EXCEPTION_CAN_NOT_CREATE_INSTALL_PROCESS = "Can not create install process.";

	public static final String EXCEPTION_CAN_NOT_INSTALL_APK_FILE = "Can not install apk file.";

	public static final String CAN_NOT_LOG_OUTPUT = "Can not log output.";

	public static final String DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE = "Name of a test suite must be between 1 and 64 characters long.";
	public static final String DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE_NO_WHITESPACE = "Name of a test suite cannot consist of whitespace characters only.";
	public static final String DIALOG_CHOICE_VALUE_PROBLEM_TITLE = "Wrong choice value";
	public static final String DIALOG_CHOICE_VALUE_PROBLEM_MESSAGE(String value){
		return "Value " + value + " is not valid for given parameter.\n" +
				"choice value must fit to type and range of the variable " +
				"represented by the choice, contain between 1 and 64 characters.\n" +
				"choices of user defined type must follow Java enum defining rules.";
	}
	public static final String DIALOG_TEST_CLASS_SELECTION_TITLE = "Test class selection";
	public static final String DIALOG_TEST_CLASS_SELECTION_MESSAGE = "Select class";
	public static final String DIALOG_USER_TYPE_SELECTION_TITLE = "User type selection";
	public static final String DIALOG_USER_TYPE_SELECTION_MESSAGE = "Select enum with no constructor or parameterless constructor";
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
	public static final String DIALOG_EDIT_COMMENTS_TITLE = "Edit element's comments";
	public static final String DIALOG_EDIT_COMMENTS_MESSAGE = "Enter element's comments in plain text";

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
	public static final String DIALOG_SET_CATEGORY_EXPECTED_PROBLEM_TITLE = "Couldn't change parameter expected status";
	public static final String DIALOG_SET_DEFAULT_VALUE_PROBLEM_TITLE = "Couldn't change default value for parameter";
	public static final String DIALOG_REMOVE_PARAMETERS_PROBLEM_TITLE = "Couldn't remove parameters";
	public static final String DIALOG_ADD_CONSTRAINT_PROBLEM_TITLE = "Couldn't add constraint";
	public static final String DIALOG_REMOVE_CONSTRAINTS_PROBLEM_TITLE = "Couldn't remove constraints";
	public static final String DIALOG_ADD_TEST_CASE_PROBLEM_TITLE = "Cannot add test case";
	public static final String DIALOG_ADD_MODEL_CONVERSION_PROBLEM_TITLE = "Model conversion failed";
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
	public static final String DIALOG_ADD_CHOICE_PROBLEM_TITLE = "Couldn't add choice";
	public static final String DIALOG_REMOVE_CHOICE_TITLE = "Couldn't remove choice";
	public static final String DIALOG_REMOVE_CHOICES_PROBLEM_TITLE = "Couldn't remove some of requested patitions";
	public static final String DIALOG_RENAME_PAREMETER_PROBLEM_TITLE = "Couldn't rename parameter";
	public static final String DIALOG_RENAME_CHOICE_PROBLEM_TITLE = "Couldn't rename choice";
	public static final String DIALOG_SET_CHOICE_VALUE_PROBLEM_TITLE = "Couldn't change choice value";
	public static final String DIALOG_REMOVE_LABEL_PROBLEM_TITLE = "Couldn't remove label";
	public static final String DIALOG_ADD_LABEL_PROBLEM_TITLE = "Couldn't add label";
	public static final String DIALOG_CHANGE_LABEL_PROBLEM_TITLE = "Couldn't change label";
	public static final String DIALOG_REMOVE_NODE_PROBLEM_TITLE = "Couldn't remove element";
	public static final String DIALOG_REMOVE_NODES_PROBLEM_TITLE = "Couldn't remove elements";
	public static final String DIALOG_ADD_CHILDREN_PROBLEM_TITLE = "Couldn't add children elements";
	public static final String DIALOG_RENAME_CONSTRAINT_PROBLEM_TITLE = "Cannot rename constraint";
	public static final String DIALOG_SET_PARAMETER_LINKED_PROBLEM_TITLE = "Couldn't set the parameter linked";
	public static final String DIALOG_SET_PARAMETER_LINK_PROBLEM_TITLE = "Couldn't set global parameter link";
	public static final String DIALOG_SET_PARAMETER_TYPE_PROBLEM_TITLE = "Couldn't set parameter's type";
	public static final String DIALOG_RESET_CHOICES_PROBLEM_TITLE = "Couldn't reset choices to default";
	public static final String DIALOG_SET_COMMENTS_PROBLEM_TITLE = "Cannot set comments";
	public static final String DIALOG_REPLACE_PARAMETERS_WITH_LINKS_TITLE = "Cannot replace parameters with links";
	public static final String DIALOG_ANDROID_RUNNER_SET_PROBLEM_TITLE = "Couldn't set Android base runner";
	public static final String DIALOG_RUN_ON_ANDROID_SET_PROBLEM_TITLE = "Couldn't set run on android flag";

	//CONFIRMATIONS AND WARNINGS
	public static final String DIALOG_REMOVE_CLASSES_TITLE = "Remove classes";
	public static final String DIALOG_REMOVE_CLASSES_MESSAGE = "This operation will remove selected test classes from the model.";
	public static final String DIALOG_REMOVE_CLASS_TITLE = "Remove class";
	public static final String DIALOG_REMOVE_CLASS_MESSAGE = "This operation will remove selected test class from the model.";
	public static final String DIALOG_RENAME_IMPLEMENTED_CLASS_TITLE = "Renaming implemented class";
	public static final String DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE = "Renaming implemented class may cause the test cases to be not executable.";
	public static final String DIALOG_RENAME_RUN_ON_ANDROID_TITLE = "Changing flag Run on Android for implemented class";
	public static final String DIALOG_RENAME_RUN_ON_ANDROID_MESSAGE = "Changing the flag: 'Run on Android' for implemented class will cause the class to be not executable.";	

	public static final String DIALOG_RENAME_CLASS_TITLE = "Renaming class";
	public static final String DIALOG_RENAME_CLASS_MESSAGE_PACKAGE_NOT_EMPTY = "Package name must not be empty.";

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
	public static final String DIALOG_MISSING_ANDROID_RUNNER_TITLE = "Missing Android base runner";
	public static final String DIALOG_MISSING_ANDROID_RUNNER_INFO(String className) {
		return "The class: " + className +  " is configured to run on Android device but Android base runner is not supplied.";
	}
	public static final String DIALOG_REMOVE_CHOICE_WARNING_TITLE = "Removing choice";
	public static final String DIALOG_REMOVE_CHOICE_WARNING_MESSAGE = "Removing choice will cause removing of all test cases and constraints referring to that choice";
	public static final String DIALOG_REMOVE_LABELS_WARNING_TITLE = "Removing labels";
	public static final String DIALOG_REMOVE_LABELS_WARNING_MESSAGE = "Removing labels will result in removing constraints that refer to the labels.";
	public static final String DIALOG_RENAME_LABELS_ERROR_TITLE = "Could't rename label";
	public static final String DIALOG_LABEL_IS_ALREADY_INHERITED = "The label is already inherited from a parent choice";
	public static final String DIALOG_RENAME_LABELS_WARNING_TITLE = "Renaming label";

	//OTHER MESSAGES
	public static final String EXECUTING_TEST_WITH_PARAMETERS = "Executing test function with generated parameters";
	public static final String EXECUTING_TEST_WITH_NO_PARAMETERS = "Executing test function";
	public static final String CAN_NOT_IMPLEMENT_SOURCE_FOR_CLASS = "Can not implement source for the class.";
	public static final String DEFAULT_PACKAGE_NOT_SET_IN_ANDROID_MANIFEST = "Default package for tested aplication is not set in AndroidManifest.xml.";

	public static final String DIALOG_GENERATOR_INPUT_PROBLEM_MESSAGE = "At least one choice per parameter must be check";
	public static final String DIALOG_GENERATOR_EXECUTABLE_INPUT_PROBLEM_MESSAGE = "At least one choice per parameter must be check. All checked parameters must be implemented";
	public static final String DIALOG_GENERATE_TEST_SUITES_SELECT_CONSTRAINTS_LABEL = "Select constraints considered when generating test suite";
	public static final String DIALOG_GENERATE_TEST_SUITES_SELECT_CHOICES_LABEL = "Select which choices will be considered for generating test suite. "
			+ "Each parameter must be represented by at least one choice.";
	public static final String DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE = "Entered test suite name not allowed";
	public static final String DIALOG_DESCENDING_LABELS_WILL_BE_REMOVED_WARNING_TITLE = "choices below in the hierarchy are already labeled with this label. Changing the label will result in removing the labels from all that choices";
	public static final String DIALOG_NO_VALID_LINK_AVAILABLE_PROBLEM_MESSAGE = "There is no link available that can be assigned to parameter which does not cause method signature duplicate problem.";


	public static final String TOOLTIP_EDIT_COMMENTS = "Edit comment";
	public static final String TOOLTIP_EXPORT_SUBTREE_COMMENTS_TO_JAVADOC = "Export all comments in the subtree to sources as javadoc";
	public static final String TOOLTIP_IMPORT_SUBTREE_COMMENTS_FROM_JAVADOC = "Import comments from javadoc of all subtree nodes' sources";
	public static final String TOOLTIP_EXPORT_CLASS_COMMENTS = "Export comments to class sources as javadoc";
	public static final String TOOLTIP_IMPORT_CLASS_COMMENTS_FROM_JAVADOC = "Import comments from class sources javadoc";
	public static final String TOOLTIP_EXPORT_CHOICE_SUBTREE_COMMENTS_TO_JAVADOC = "Export comments for all leaf choices of this choice as javadoc";
	public static final String TOOLTIP_IMPORT_CHOICE_SUBTREE_COMMENTS_FROM_JAVADOC = "Import comments of all leaf choices of this choice from javadoc";
	public static final String TOOLTIP_EXPORT_CHOICE_COMMENTS_TO_JAVADOC = "Export comments of this choice to file as javadoc";
	public static final String TOOLTIP_IMPORT_CHOICE_COMMENTS_FROM_JAVADOC = "Import comments from source file";

	public static String DIALOG_UNSUCCESFUL_TEST_EXECUTION(int totalTestCases, int unsuccesfull) {
		return String.valueOf(unsuccesfull) + " of " + totalTestCases + " test cases have met problems during execution.";
	}
	public static String DIALOG_SUCCESFUL_TEST_EXECUTION(int totalTestCases) {
		return "All of " + totalTestCases + " test cases were succesfully executed.";
	}

	public static final String CHOICE_ALREADY_EXISTS(String choiceName) {
		return "Choice with the name: " + choiceName + "  already exists.";
	}
	public static final String CAN_NOT_PASTE_CHOICES = "Can not paste choices.";
}
