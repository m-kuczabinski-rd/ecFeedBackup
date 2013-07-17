package com.testify.ecfeed.constants;

public class DialogStrings {
	public static final String DIALOG_REMOVE_CLASS_TITLE = "Remove class"; 
	public static final String DIALOG_REMOVE_CLASS_MESSAGE = "This operation will remove selected test classes " +
			"from the model. All generated test cases will be permanently deleted";
	public static final String DIALOG_REMOVE_TEST_CASES_TITLE = "Remove test cases";
	public static final String DIALOG_REMOVE_TEST_CASES_MESSAGE = "Delete selected test cases?";
	public static final String DIALOG_REMOVE_METHODS_TITLE = "Remove methods";
	public static final String DIALOG_REMOVE_METHODS_MESSAGE = "Remove selected methods from the model?\n" +
			"All generated test cases will be lost.";
	public static final String DIALOG_REMOVE_TEST_SUITES_TITLE = "Remove test suites";
	public static final String DIALOG_REMOVE_TEST_SUITES_MESSAGE = "Select test suites to remove";
	public static final String DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE = "Name of a test suite must be between 1 and 64 characters long";
	public static final String DIALOG_PARTITION_NAME_PROBLEM_TITLE = "Wrong partition name";
	public static final String DIALOG_PARTITION_NAME_PROBLEM_MESSAGE = "Partition name must have between 1 and 64 characters, " +
			"must not contain only white characters and must be unique within category";
	public static final String DIALOG_PARTITION_VALUE_PROBLEM_TITLE = "Wrong partition value";
	public static final String DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE = "Partition value must fit to type and range of the variable " +
			"represented by the partition";
	public static final String DIALOG_REMOVE_PARTITIONS_TITLE = "Remove partitions";
	public static final String DIALOG_REMOVE_PARTITIONS_MESSAGE = "Removing partitions will remove also all test cases " +
			"referencing those partitions. Are you sure that you want to proceed?";
	public static final String DIALOG_PARTITION_SETTINGS_DIALOG_TITLE = "Partition settings";
	public static final String DIALOG_PARTITION_SETTINGS_DIALOG_MESSAGE = "Enter partition name and value";

}
