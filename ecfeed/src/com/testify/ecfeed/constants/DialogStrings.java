/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.constants;

public class DialogStrings {
	public static final String DIALOG_REMOVE_CLASSES_TITLE = "Remove classes"; 
	public static final String DIALOG_REMOVE_CLASSES_MESSAGE = "This operation will remove selected test classes " +
			"from the model. All generated test cases will be permanently deleted.";
	public static final String DIALOG_REMOVE_CLASS_TITLE = "Remove classes"; 
	public static final String DIALOG_REMOVE_CLASS_MESSAGE = "This operation will remove this test class " +
			"from the model. All generated test cases will be permanently deleted.";
	public static final String DIALOG_REMOVE_TEST_CASES_TITLE = "Remove test cases";
	public static final String DIALOG_REMOVE_TEST_CASES_MESSAGE = "Delete selected test cases?";
	public static final String DIALOG_REMOVE_METHODS_TITLE = "Remove methods";
	public static final String DIALOG_REMOVE_METHODS_MESSAGE = "Remove selected methods from the model?\n" +
			"All generated test cases will be lost.";
	public static final String DIALOG_REMOVE_TEST_SUITES_TITLE = "Remove test suites";
	public static final String DIALOG_REMOVE_TEST_SUITES_MESSAGE = "Select test suites to remove.";
	public static final String DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE = "Name of a test suite must be between 1 and 64 characters long.";
	public static final String DIALOG_PARTITION_NAME_PROBLEM_TITLE = "Wrong partition name";
	public static final String DIALOG_PARTITION_NAME_PROBLEM_MESSAGE = "Partition name must have between 1 and 64 characters, " +
			"must not contain only white characters and must be unique within category.";
	public static final String DIALOG_PARTITION_VALUE_PROBLEM_TITLE = "Wrong partition value";
	public static final String DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE = "Partition value must fit to type and range of the variable " +
			"represented by the partition.";
	public static final String DIALOG_REMOVE_PARTITIONS_TITLE = "Remove partitions";
	public static final String DIALOG_REMOVE_PARTITIONS_MESSAGE = "Removing partitions will remove also all test cases " +
			"referencing those partitions. Are you sure that you want to proceed?";
	public static final String DIALOG_PARTITION_SETTINGS_DIALOG_TITLE = "Partition settings";
	public static final String DIALOG_PARTITION_SETTINGS_DIALOG_MESSAGE = "Enter partition name and value.";
	public static final String DIALOG_TEST_SUITE_NAME_PROBLEM_TITLE = "Wrong test suite name";
	public static final String DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE = "Test suite name mus contain between 1 and 64 characters " +
			"and may not contain only white characters.";
	public static final String DIALOG_CLASS_EXISTS_TITLE = "Class exists";
	public static final String DIALOG_CLASS_EXISTS_MESSAGE = "Selected class is already contained in the model.";
	public static final String DIALOG_RENAME_MODEL_TITLE = "Rename model";
	public static final String DIALOG_RENAME_MODEL_MESSAGE = "Enter new name for the model. This name has only aesthetic purposes, " +
			"so feel free to provide any name you want. "
			+ "\nTo have some minimal tidiness however, keep the name between 1 and 64 characters long and let it not "
			+ "contain white characters only.";
	public static final String DIALOG_TEST_CLASS_SELECTION_TITLE = "Test class selection";
	public static final String DIALOG_TEST_CLASS_SELECTION_MESSAGE = "Select a class with at lease one method annotated with @Test.";
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
}
