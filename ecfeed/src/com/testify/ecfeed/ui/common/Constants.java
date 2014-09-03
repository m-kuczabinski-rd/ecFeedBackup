package com.testify.ecfeed.ui.common;

public class Constants {
	public static final String EQUIVALENCE_CLASS_FILE_EXTENSION = "ect";
	public static final String DEFAULT_NEW_ECT_MODEL_NAME = "newEct";
	public static final String DEFAULT_NEW_ECT_FILE_NAME = DEFAULT_NEW_ECT_MODEL_NAME + "." + EQUIVALENCE_CLASS_FILE_EXTENSION;

	public static final String DEFAULT_NEW_PACKAGE_NAME = "com.example.test";
	public static final String DEFAULT_NEW_CLASS_NAME = "TestClass";
	public static final String DEFAULT_NEW_METHOD_NAME = "testMethod";
	public static final String DEFAULT_NEW_PARAMETER_NAME = "arg";
	public static final String DEFAULT_USER_TYPE_NAME = "default.UserType";
	public static final String DEFAULT_NEW_TEST_SUITE_NAME = "default suite";
	public static final String DEFAULT_NEW_CONSTRAINT_NAME = "constraint";
	public static final String DEFAULT_NEW_PARTITION_NAME = "partition";
	public static final String DEFAULT_NEW_PARTITION_LABEL = "label";

	public static final String NULL_VALUE_STRING_REPRESENTATION = "/null";
	public static final String MAX_VALUE_STRING_REPRESENTATION = "MAX_VALUE";
	public static final String MIN_VALUE_STRING_REPRESENTATION = "MIN_VALUE";
	public static final String BOOLEAN_TRUE_STRING_REPRESENTATION = "true";
	public static final String BOOLEAN_FALSE_STRING_REPRESENTATION = "false";
	public static final String POSITIVE_INFINITY_STRING_REPRESENTATION = "POSITIVE_INFINITY";
	public static final String NEGATIVE_INFINITY_STRING_REPRESENTATION = "NEGATIVE_INFINITY";

	public static final String[] BOOLEAN_SPECIAL_VALUES = {BOOLEAN_TRUE_STRING_REPRESENTATION, BOOLEAN_FALSE_STRING_REPRESENTATION};
	public static final String[] INTEGER_SPECIAL_VALUES = {MIN_VALUE_STRING_REPRESENTATION, MAX_VALUE_STRING_REPRESENTATION};
	public static final String[] FLOAT_SPECIAL_VALUES = {NEGATIVE_INFINITY_STRING_REPRESENTATION, MIN_VALUE_STRING_REPRESENTATION, MAX_VALUE_STRING_REPRESENTATION, POSITIVE_INFINITY_STRING_REPRESENTATION};
	public static final String[] STRING_SPECIAL_VALUES = {NULL_VALUE_STRING_REPRESENTATION};
	public static final String[] SHORT_SPECIAL_VALUES = INTEGER_SPECIAL_VALUES;
	public static final String[] LONG_SPECIAL_VALUES = INTEGER_SPECIAL_VALUES;
	public static final String[] BYTE_SPECIAL_VALUES = INTEGER_SPECIAL_VALUES;
	public static final String[] DOUBLE_SPECIAL_VALUES = FLOAT_SPECIAL_VALUES;

	
	public static final String DEFAULT_EXPECTED_NUMERIC_VALUE = "0";
	public static final String DEFAULT_EXPECTED_FLOATING_POINT_VALUE = "0.0";
	public static final String DEFAULT_EXPECTED_BOOLEAN_VALUE = BOOLEAN_FALSE_STRING_REPRESENTATION;
	public static final String DEFAULT_EXPECTED_CHAR_VALUE = "0";
	public static final String DEFAULT_EXPECTED_BYTE_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_DOUBLE_VALUE = DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
	public static final String DEFAULT_EXPECTED_FLOAT_VALUE = DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
	public static final String DEFAULT_EXPECTED_INT_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_LONG_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_SHORT_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_STRING_VALUE = "";
	public static final String DEFAULT_EXPECTED_ENUM_VALUE = "VALUE";

	public static final int TEST_SUITE_SIZE_WARNING_LIMIT = 1000;
	public static final int MAX_DISPLAYED_TEST_CASES_PER_SUITE = 500;
	public static final int MAX_DISPLAYED_CHILDREN_PER_NODE = 1000;
}
