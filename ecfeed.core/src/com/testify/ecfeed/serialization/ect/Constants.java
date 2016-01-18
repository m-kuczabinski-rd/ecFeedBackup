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

package com.testify.ecfeed.serialization.ect;

public class Constants {
	public static final String ROOT_NODE_NAME = "Model";
	public static final String CLASS_NODE_NAME = "Class";
	public static final String METHOD_NODE_NAME = "Method";

	public static final String EXPECTED_VALUE_PARAMETER_NODE_NAME = "ExpectedValueParameter";
	public static final String LABEL_NODE_NAME = "Label";
	public static final String TEST_CASE_NODE_NAME = "TestCase";
	public static final String TEST_PARAMETER_NODE_NAME = "TestParameter";
	public static final String EXPECTED_PARAMETER_NODE_NAME = "ExpectedValue";
	public static final String CONSTRAINT_NODE_NAME = "Constraint";
	public static final String CONSTRAINT_PREMISE_NODE_NAME = "Premise";
	public static final String CONSTRAINT_CONSEQUENCE_NODE_NAME = "Consequence";
	public static final String CONSTRAINT_CHOICE_STATEMENT_NODE_NAME = "Statement";
	public static final String CONSTRAINT_LABEL_STATEMENT_NODE_NAME = "LabelStatement";
	public static final String CONSTRAINT_STATIC_STATEMENT_NODE_NAME = "StaticStatement";
	public static final String CONSTRAINT_STATEMENT_ARRAY_NODE_NAME = "StatementArray";
	public static final String CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME = "ExpectedValueStatement";
	public static final String COMMENTS_BLOCK_TAG_NAME = "Comments";
	public static final String BASIC_COMMENTS_BLOCK_TAG_NAME = "BasicComments";
	public static final String TYPE_COMMENTS_BLOCK_TAG_NAME = "TypeComments";

	public static final String NODE_NAME_ATTRIBUTE = "name";
	public static final String VERSION_ATTRIBUTE = "version";
	public static final String QUALIFIED_NAME_ATTRIBUTE = "qualifiedName";
	public static final String TYPE_NAME_ATTRIBUTE = "type";
	public static final String DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME = "expected";
	public static final String PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME = "isExpected";
	public static final String PARAMETER_IS_LINKED_ATTRIBUTE_NAME = "linked";
	public static final String PARAMETER_LINK_ATTRIBUTE_NAME = "link";
	public static final String VALUE_ATTRIBUTE = "value";
	public static final String NULL_VALUE_STRING_REPRESENTATION = "/null";
	public static final String TEST_SUITE_NAME_ATTRIBUTE = "testSuite";
	public static final String LABEL_ATTRIBUTE_NAME = "label";
	public static final String VALUE_ATTRIBUTE_NAME = "value";
	public static final String STATIC_VALUE_ATTRIBUTE_NAME = "value";
	public static final String STATEMENT_LABEL_ATTRIBUTE_NAME = "label";
	public static final String STATEMENT_RELATION_ATTRIBUTE_NAME = "relation";
	public static final String STATEMENT_OPERATOR_ATTRIBUTE_NAME = "operator";
	public static final String STATEMENT_STATIC_VALUE_ATTRIBUTE_NAME = "value";
	public static final String STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE = "and";
	public static final String STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE = "or";
	public static final String STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME = "value";
	public static final String PARAMETER_IS_RUN_ON_ANDROID_ATTRIBUTE_NAME = "runOnAndroid";
	public static final String ANDROID_RUNNER_ATTRIBUTE_NAME = "androidRunner";

	public static final String STATIC_STATEMENT_TRUE_VALUE = "true";
	public static final String STATIC_STATEMENT_FALSE_VALUE = "false";

	public static final String RELATION_EQUAL = "=";
	public static final String RELATION_NOT = "\u2260";
	public static final String RELATION_NOT_ASCII = "!=";

	public static final String EXPECTED_VALUE_CHOICE_NAME = "@expected";

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

}
