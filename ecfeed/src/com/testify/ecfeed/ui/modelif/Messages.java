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

	//EXCEPTIONS
	public static final String EXCEPTION_METHOD_IMPORT(String name){
		return "Unexpected problems with importing method " + name;
	}
	public static final String EXCEPTION_CLASS_IMPORT(String name){
		return "Unexpected problems with importing class " + name;
	}
	public static final String EXCEPTION_TYPE_DOES_NOT_EXIST_IN_THE_PROJECT = "The imported type does not exist in the project";

}
