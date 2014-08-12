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


	//CONFIRMATIONS AND WARNINGS
	public static final String DIALOG_REMOVE_CLASSES_TITLE = "Remove classes"; 
	public static final String DIALOG_REMOVE_CLASSES_MESSAGE = "This operation will remove selected test classes from the model.";
	public static final String DIALOG_REMOVE_CLASS_TITLE = "Remove class"; 
	public static final String DIALOG_REMOVE_CLASS_MESSAGE = "This operation will remove selected test class " +
			"from the model.";
	public static final String DIALOG_RENAME_IMPLEMENTED_CLASS_TITLE = "Renaming implemented class";
	public static final String DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE = "You are about to rename implemented class. Continue?";
	public static final String DIALOG_REMOVE_METHOD_TITLE = "Remove method";
	public static final String DIALOG_REMOVE_METHOD_MESSAGE = "This operation will remove selected test method from the model.";
	public static final String DIALOG_REMOVE_METHODS_TITLE = "Remove method";;
	public static final String DIALOG_REMOVE_METHODS_MESSAGE = "This operation will remove selected test methods from the model.";
	public static final String DIALOG_SET_CATEGORY_EXPECTED_WARNING_TITLE = "Change parameter's expected property";
	public static final String DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_REMOVED = "All test cases will be removed"; 
	public static final String DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_ALTERED = "Corresponding test parameter in all test cases will be replaced by default value";
	public static final String DIALOG_SET_CATEGORY_EXPECTED_CONSTRAINTS_REMOVED = "All constraints that refer to that parameter will be removed";
	public static final String DIALOG_REMOVE_PARAMETERS_WARNING_TITLE = "Remove parameters";
	public static final String DIALOG_REMOVE_PARAMETERS_WARNING_MESSAGE = "Removing selected parameters will result in removing constraints and test cases that refer to them";
	

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

}
