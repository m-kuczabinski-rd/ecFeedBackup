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

/**
 * @author patryk
 *
 */
public class Constants {
	public static final String EQUIVALENCE_CLASS_FILE_EXTENSION = "ect";
	public static final String DEFAULT_NEW_ECT_MODEL_NAME = "newEct";
	public static final String DEFAULT_NEW_ECT_FILE_NAME = DEFAULT_NEW_ECT_MODEL_NAME + "." + EQUIVALENCE_CLASS_FILE_EXTENSION;
	
	public static final String ROOT_NODE_NAME = "Model";
	public static final String CLASS_NODE_NAME = "Class";
	public static final String METHOD_NODE_NAME = "Method";
	public static final String CATEGORY_NODE_NAME = "Category";
	public static final String EXPECTED_VALUE_CATEGORY_NODE_NAME = "ExpectedValueCategory";
	public static final String PARTITION_NODE_NAME = "Partition";
	public static final String TEST_CASE_NODE_NAME = "TestCase";
	public static final String TEST_PARAMETER_NODE_NAME = "TestParameter";
	public static final String CONSTRAINT_NODE_NAME = "Constraint";
	public static final String CONSTRAINT_PREMISE_NODE_NAME = "Premise";
	public static final String CONSTRAINT_CONSEQUENCE_NODE_NAME = "Consequence";
	public static final String CONSTRAINT_STATEMENT_NODE_NAME = "Statement";
	public static final String CONSTRAINT_STATIC_STATEMENT_NODE_NAME = "StaticStatement";
	public static final String CONSTRAINT_STATEMENT_ARRAY_NODE_NAME = "StatementArray";
	public static final String DEFAULT_NODE_NAME = "Node";

	public static final String NODE_NAME_ATTRIBUTE = "name";
	public static final String QUALIFIED_NAME_ATTRIBUTE = "qualifiedName";
	public static final String TYPE_NAME_ATTRIBUTE = "type";
	public static final String DEFAULT_EXPECTED_VALUE_ATTRIBUTE = "expected";
	public static final String VALUE_ATTRIBUTE = "value";
	public static final String NULL_VALUE_STRING_REPRESENTATION = "/null";
	public static final String TEST_SUITE_NAME_ATTRIBUTE = "testSuite";
	public static final String PARTITION_ATTRIBUTE_NAME = "partition";
	public static final String STATIC_VALUE_ATTRIBUTE_NAME = "value";
	public static final String STATEMENT_CATEGORY_ATTRIBUTE_NAME = "category";
	public static final String STATEMENT_PARTITION_ATTRIBUTE_NAME = "partition";
	public static final String STATEMENT_RELATION_ATTRIBUTE_NAME = "relation";
	public static final String STATEMENT_OPERATOR_ATTRIBUTE_NAME = "operator";
	public static final String STATEMENT_STATIC_VALUE_ATTRIBUTE_NAME = "value";
	public static final String STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE = "and";
	public static final String STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE = "or";
	public static final String STATIC_STATEMENT_TRUE_VALUE = "true";
	public static final String STATIC_STATEMENT_FALSE_VALUE = "false";
	public static final String RELATION_LESS = "<";
	public static final String RELATION_LESS_EQUAL = "\u2264";
	public static final String RELATION_EQUAL = "=";
	public static final String RELATION_GREATER_EQUAL = "\u2265";
	public static final String RELATION_GREATER = ">";
	public static final String RELATION_NOT = "\u2260";
	
	public static final String DEFAULT_TEST_SUITE_NAME = "default suite";
	public static final String DEFAULT_CONSTRAINT_NAME = "constraint";
	
	public static final String DEFAULT_ECT_EDITOR_ID = "com.testify.ecfeed.editors.EcMultiPageEditor";

	public static final int MAX_PARTITION_NAME_LENGTH = 64;
	public static final int MAX_MODEL_NAME_LENGTH = 64;
	
	public static final String TYPE_NAME_BOOLEAN = "boolean";
	public static final String TYPE_NAME_BYTE = "byte";
	public static final String TYPE_NAME_CHAR = "char";
	public static final String TYPE_NAME_DOUBLE = "double";
	public static final String TYPE_NAME_FLOAT = "float";
	public static final String TYPE_NAME_INT = "int";
	public static final String TYPE_NAME_LONG = "long";
	public static final String TYPE_NAME_SHORT = "short";
	public static final String TYPE_NAME_STRING = "String";
	public static final String TYPE_NAME_UNSUPPORTED = "unsupported";

	public static final String EXPECTED_VALUE_PARTITION_NAME = "default value";
	public static final boolean DEFAULT_EXPECTED_BOOLEAN_VALUE = false;
	public static final byte DEFAULT_EXPECTED_BYTE_VALUE = 0;
	public static final char DEFAULT_EXPECTED_CHAR_VALUE = '\0';
	public static final double DEFAULT_EXPECTED_DOUBLE_VALUE = 0.0;
	public static final float DEFAULT_EXPECTED_FLOAT_VALUE = 0;
	public static final int DEFAULT_EXPECTED_INT_VALUE = 0;
	public static final long DEFAULT_EXPECTED_LONG_VALUE = 0;
	public static final short DEFAULT_EXPECTED_SHORT_VALUE = 0;
	public static final String DEFAULT_EXPECTED_STRING_VALUE = "";

	public static final String TEST_GEN_ALGORITHM_EXTENSION_POINT_ID = "com.testify.ecfeed.algorithm";
	public static final String TEST_GEN_ALGORITHM_IMPLEMENTATION_ATTRIBUTE = "implementation";
	public static final String ALGORITHM_NAME_ATTRIBUTE = "name";
	
	
	/**
	 * Size of a model subtree, after which elements are collapsed after each operation. 
	 * This constant is introduced for performance gain.
	 */
	public static final int MAX_DISPLAYED_CHILDREN_PER_NODE = 1000;
	public static final int MAX_DISPLAYED_TEST_CASES_PER_SUITE = 500;
	public static final int TEST_SUITE_SIZE_WARNING_LIMIT = 20000;
}
