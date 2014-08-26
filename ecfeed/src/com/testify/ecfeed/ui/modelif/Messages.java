package com.testify.ecfeed.ui.modelif;

public class Messages {
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
	public static final String DIALOG_TEST_CASE_WITH_EMPTY_CATEGORY_MESSAGE = "Some non-expected parameters have empty partition set.";
	public static final String DIALOG_RENAME_TEST_SUITE_PROBLEM = "Couldn't rename test cases";
	public static final String DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE = "Couldn't remove some of requested test cases";
	public static final String DIALOG_ADD_TEST_SUITE_PROBLEM_TITLE = "Couldn't add test cases";
	public static final String DIALOG_EMPTY_TEST_SUITE_GENERATED_MESSAGE = "The generated test suite was empty";
	public static final String DIALOG_TEST_EXECUTION_PROBLEM_TITLE = "Couldn't execute test";
	public static final String DIALOG_UPDATE_TEST_DATA_PROBLEM_TITLE = "Couldn't update test data";
	public static final String DIALOG_ADD_STATEMENT_PROBLEM_TITLE = "Couldn't add statement";
	public static final String DIALOG_REMOVE_STATEMENT_PROBLEM_TITLE = "Couldn't remove statement";
	public static final String DIALOG_EDIT_STATEMENT_PROBLEM_TITLE = "Couldn't edit statement";
	public static final String DIALOG_ADD_PARTITION_PROBLEM_TITLE = "Couldn't add partition";
	public static final String DIALOG_REMOVE_PARTITION_TITLE = "Couldn't remove partition";
	public static final String DIALOG_REMOVE_PARTITIONS_PROBLEM_TITLE = "Couldn't remove some of requested patitions";
	public static final String DIALOG_RENAME_PAREMETER_PROBLEM_TITLE = "Couldn't rename parameter";
	public static final String DIALOG_RENAME_PARTITION_PROBLEM_TITLE = "Couldn't rename partition";
	public static final String DIALOG_SET_PARTITION_VALUE_PROBLEM_TITLE = "Couldn't change partition value";


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
	public static final String DIALOG_REMOVE_PARTITION_WARNING_TITLE = "Removing partition";
	public static final String DIALOG_REMOVE_PARTITION_WARNING_MESSAGE = "Removing partition will cause removing of all test cases and constraints referring to that partition";


	//EXCEPTIONS
	public static final String EXCEPTION_METHOD_IMPORT(String name){
		return "Unexpected problems with importing method " + name;
	}
	public static final String EXCEPTION_CLASS_IMPORT(String name){
		return "Unexpected problems with importing class " + name;
	}
	public static final String EXCEPTION_TYPE_DOES_NOT_EXIST_IN_THE_PROJECT = "The imported type does not exist in the project";
	
	//OTHER MESSAGES
	public static final String EXECUTING_TEST_WITH_PARAMETERS = "Executing test function with generated parameters";
	public static final String EXECUTING_TEST_WITH_NO_PARAMETERS = "Executing test function";
	
	public static final String DIALOG_GENERATOR_INPUT_PROBLEM_MESSAGE = "At least one partition per category must be check";
	public static final String DIALOG_GENERATOR_EXECUTABLE_INPUT_PROBLEM_MESSAGE = "At least one partition per category must be check. All checked categories must be implemented";
	public static final String DIALOG_GENERATE_TEST_SUITES_SELECT_CONSTRAINTS_LABEL = "Select constraints considered when generating test suite";
	public static final String DIALOG_GENERATE_TEST_SUITES_SELECT_PARTITIONS_LABEL = "Select which partitions will be considered for generating test suite. "
			+ "Each category must be represented by at least one partition.";
	public static final String DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE = "Entered test suite name not allowed";
}
