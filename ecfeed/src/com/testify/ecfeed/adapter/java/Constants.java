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

package com.testify.ecfeed.adapter.java;

public class Constants {

	public static final int MAX_NODE_NAME_LENGTH = 64;
	public static final int MAX_PARTITION_VALUE_STRING_LENGTH = 512;

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

	public static final String[] SUPPORTED_PRIMITIVE_TYPES = new String[]{
		TYPE_NAME_INT,
		TYPE_NAME_BOOLEAN,
		TYPE_NAME_LONG,
		TYPE_NAME_SHORT,
		TYPE_NAME_BYTE,
		TYPE_NAME_DOUBLE,
		TYPE_NAME_FLOAT,
		TYPE_NAME_CHAR,
		TYPE_NAME_STRING
	};

	public static final String REGEX_JAVA_IDENTIFIER = "[A-Za-z_$][A-Za-z0-9_$]*";
	public static final String REGEX_ALPHANUMERIC_WITH_SPACES_64 = "[A-Za-z0-9_\\-]+[A-Za-z0-9_\\- ]{0,64}";
	public static final String REGEX_ROOT_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_PACKAGE_NAME = "((" + REGEX_JAVA_IDENTIFIER + ")\\.)*";
	public static final String REGEX_CLASS_NODE_NAME = REGEX_PACKAGE_NAME + REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_METHOD_NODE_NAME = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_CATEGORY_NODE_NAME = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_CATEGORY_TYPE_NAME = REGEX_CLASS_NODE_NAME;
	public static final String REGEX_CONSTRAINT_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_TEST_CASE_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_PARTITION_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_PARTITION_LABEL = REGEX_ALPHANUMERIC_WITH_SPACES_64;

	public static final String REGEX_USER_TYPE_VALUE = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_STRING_TYPE_VALUE = "[A-Za-z1-9 !@#$%^&*()_+=;':,.<>/?]{0,1024}";
	public static final String REGEX_CHAR_TYPE_VALUE = "[A-Za-z1-9 !@#$%^&*()_+=;':,.<>/?]";

	public static final String[] JAVA_KEYWORDS = new String[]
		{ "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do",
		"if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public",
		"throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char",
		"final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
		"native", "super", "while", "null", "true", "false" };

	public static final String VALUE_REPRESENTATION_NULL = "/null";
	public static final String VALUE_REPRESENTATION_MAX = "MAX_VALUE";
	public static final String VALUE_REPRESENTATION_MIN = "MIN_VALUE";
	public static final String VALUE_REPRESENTATION_TRUE = "true";
	public static final String VALUE_REPRESENTATION_FALSE = "false";
	public static final String VALUE_REPRESENTATION_POSITIVE_INF = "POSITIVE_INFINITY";
	public static final String VALUE_REPRESENTATION_NEGATIVE_INF = "NEGATIVE_INFINITY";

}
