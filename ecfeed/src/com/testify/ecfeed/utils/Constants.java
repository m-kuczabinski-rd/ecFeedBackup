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

package com.testify.ecfeed.utils;

/**
 * @author patryk
 *
 */
public class Constants {
	public static final String EQUIVALENCE_CLASS_FILE_EXTENSION = "ect";
	public static final String DEFAULT_NEW_ECT_MODEL_NAME = "newEct";
	public static final String DEFAULT_NEW_ECT_FILE_NAME = DEFAULT_NEW_ECT_MODEL_NAME + "." + EQUIVALENCE_CLASS_FILE_EXTENSION;
	
	public static final String DEFAULT_TEST_SUITE_NAME = "default suite";
	public static final String DEFAULT_CONSTRAINT_NAME = "constraint";
	public static final String DEFAULT_NEW_PARTITION_NAME = "partition";

	public static final String DEFAULT_ECT_EDITOR_ID = "com.testify.ecfeed.editors.EcMultiPageEditor";
	public static final String EXPECTED_VALUE_PARTITION_NAME = "@expected";
	public static final String DEFAULT_EXPECTED_BOOLEAN_VALUE = "false";
	public static final String DEFAULT_EXPECTED_BYTE_VALUE = "0";
	public static final String DEFAULT_EXPECTED_CHAR_VALUE = "0";
	public static final String DEFAULT_EXPECTED_DOUBLE_VALUE = "0.0";
	public static final String DEFAULT_EXPECTED_FLOAT_VALUE = "0";
	public static final String DEFAULT_EXPECTED_INT_VALUE = "0";
	public static final String DEFAULT_EXPECTED_LONG_VALUE = "0";
	public static final String DEFAULT_EXPECTED_SHORT_VALUE = "0";
	public static final String DEFAULT_EXPECTED_STRING_VALUE = "";

	/**
	 * Size of a model subtree, after which elements are collapsed after each operation. 
	 * This constant is introduced for performance gain.
	 */
	public static final int MAX_DISPLAYED_CHILDREN_PER_NODE = 1000;
	public static final int MAX_DISPLAYED_TEST_CASES_PER_SUITE = 500;
	public static final int TEST_SUITE_SIZE_WARNING_LIMIT = 20000;
	public static final String DEFAULT_LABEL = "newLabel";

	public static final String NULL_VALUE_STRING_REPRESENTATION = "/null";
	public static final String MAX_VALUE_STRING_REPRESENTATION = "MAX_VALUE";
	public static final String MIN_VALUE_STRING_REPRESENTATION = "MIN_VALUE";
	public static final String BOOLEAN_TRUE_STRING_REPRESENTATION = "true";
	public static final String BOOLEAN_FALSE_STRING_REPRESENTATION = "false";
	public static final String POSITIVE_INFINITY_STRING_REPRESENTATION = "POSITIVE_INFINITY";
	public static final String NEGATIVE_INFINITY_STRING_REPRESENTATION = "NEGATIVE_INFINITY";
	
	public static final String DEFAULT_NEW_CATEGORY_NAME = "arg";
	public static final String DEFAULT_NEW_CATEGORY_TYPE = "int";
	public static final String DEFAULT_NEW_METHOD_NAME = "testMethod";
	public static final String DEFAULT_NEW_PACKAGE_NAME = "com.example.test";
	public static final String DEFAULT_NEW_CLASS_NAME = "TestClass";
	public static final String DEFAULT_USER_TYPE_NAME = "default.UserType";
}
