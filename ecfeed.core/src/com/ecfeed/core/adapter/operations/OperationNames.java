/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.ecfeed.core.adapter.operations;

public interface OperationNames {
	public static final String RENAME = "Rename";
	public static final String SET_DEFAULT_VALUE = "Set default value";
	public static final String SET_EXPECTED_STATUS = "Change expected status";
	public static final String SET_TYPE = "Set type";
	public static final String ADD_METHOD = "Add method";
	public static final String ADD_METHODS = "Add methods";
	public static final String REMOVE_METHOD = "Remove method";
	public static final String EDIT_STATEMENT = "Edit statement";
	public static final String CHANGE_STATEMENT_OPERATOR = EDIT_STATEMENT;
	public static final String REPLACE_STATEMENT = EDIT_STATEMENT;
	public static final String REMOVE_STATEMENT = "Remove statement";
	public static final String SET_STATEMENT_CONDITION = EDIT_STATEMENT;
	public static final String SET_STATEMENT_RELATION = EDIT_STATEMENT;
	public static final String ADD_CHILDREN = "Add elements";
	public static final String MOVE = "Move elements";
	public static final String REMOVE_PARTITION = "Remove choice";
	public static final String ADD_PARTITION = "Add choice";
	public static final String REMOVE_NODES = "Remove elements";
	public static final String ADD_CONSTRAINT = "Add constraint";
	public static final String ADD_PARAMETER = "Add parameter";
	public static final String ADD_TEST_CASE = "Add test case";
	public static final String ADD_TEST_CASES = "Add test cases";
	public static final String CONVERT_METHOD = "Convert method";
	public static final String MAKE_CONSISTENT = "Make method consistent";
	public static final String REMOVE_CONSTRAINT = "Remove constraint";
	public static final String REMOVE_METHOD_PARAMETER = "Remove method parameter";
	public static final String REMOVE_GLOBAL_PARAMETER = "Remove global parameter";
	public static final String REMOVE_TEST_CASE = "Remove test case";
	public static final String RENAME_TEST_CASE = "Rename test case";
	public static final String ADD_PARTITION_LABEL = "Add choice label";
	public static final String ADD_PARTITION_LABELS = "Add choice labels";
	public static final String REMOVE_PARTITION_LABEL = "Remove choice label";
	public static final String REMOVE_PARTITION_LABELS = "Remove choice labels";
	public static final String RENAME_LABEL = "Rename label";
	public static final String SET_PARTITION_VALUE = "Set choice value";
	public static final String ADD_CLASSES = "Add classes";
	public static final String ADD_CLASS = "Add class";
	public static final String REMOVE_CLASS = "Remove class";
	public static final String ADD_STATEMENT = "Add statement";
	public static final String UPDATE_TEST_DATA = "Update test data";
	public static final String SET_LINKED = "Set linked";
	public static final String SET_LINK = "Set link";
	public static final String SET_COMMENTS = "Set comments";
	public static final String REPLACE_PARAMETERS = "Replace method parameters with links";
	public static final String REPLACE_PARAMETER_WITH_LINK = "Replace parameter with link";
	public static final String SET_ANDROID_BASE_RUNNER = "Set Android base runner";
}
