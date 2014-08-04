package com.testify.ecfeed.ui.modelif;

public class Messages {
	//PROBLEM COMMUNICATES
	public static final String DIALOG_RENAME_MODEL_PROBLEM_TITLE = "Couldn't rename model";
	public static final String DIALOG_ADD_NEW_CLASS_PROBLEM_TITLE = "Couldn't add new class";
	public static final String DIALOG_REMOVE_CLASSES_PROBLEM_TITLE = "Couldn't remove classes";
	public static final String DIALOG_RENAME_CLASS_PROBLEM_TITLE = "Couldn't rename class";
	public static final String DIALOG_MOVE_NODE_PROBLEM_TITLE = "Couldn't move node";

	//CONFIRMATIONS AND WARNINGS
	public static final String DIALOG_REMOVE_CLASSES_TITLE = "Remove classes"; 
	public static final String DIALOG_REMOVE_CLASSES_MESSAGE = "This operation will remove selected test classes " +
			"from the model. All generated test cases will be permanently deleted.";
	public static final String DIALOG_REMOVE_CLASS_TITLE = "Remove class"; 
	public static final String DIALOG_REMOVE_CLASS_MESSAGE = "This operation will remove selected test class " +
			"from the model. All generated test cases will be permanently deleted.";
	public static final String DIALOG_RENAME_IMPLEMENTED_CLASS_TITLE = "Renaming implemented class";
	public static final String DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE = "You are about to rename implemented class. Continue?";

	//EXCEPTIONS
	public static final String METHOD_IMPORT_EXCEPTION(String name){
		return "Unexpected problems with importing method " + name;
	}
	public static final String CLASS_IMPORT_EXCEPTION(String name){
		return "Unexpected problems with importing class " + name;
	}

}
