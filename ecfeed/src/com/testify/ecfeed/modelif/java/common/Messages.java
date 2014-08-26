package com.testify.ecfeed.modelif.java.common;

public class Messages {

	public static final String PARTITION_NAME_NOT_UNIQUE_PROBLEM = "Partition name must be unique within a category or parent partition";
	public static final String PARTITION_NAME_REGEX_PROBLEM = "Partition name must contain between 1 and 64 characters and must not contain only white space characters";
	public static final String PARTITION_VALUE_PROBLEM(String value){
		return "Value " + value + " is not valid for given parameter.\n\n" + 
				"Partition value must fit to type and range of the represented parameter.\n" +
				"Partitions of user defined type must follow Java enum defining rules.";
	}
	public static final String PROBLEM_WITH_BULK_OPERATION = "Some problems occured during the operation:";
	public static final String NEGATIVE_INDEX_PROBLEM = "The index of the element must be non-negative";
	public static final String TOO_HIGH_INDEX_PROBLEM = "The index of the element is too high";
	public static final String MODEL_NAME_REGEX_PROBLEM = "Model name must contain between 1 and 64 alphanumeric characters or spaces.\n The model name must not start with space.";
	public static final String CLASS_NAME_REGEX_PROBLEM = "The provided name must fulfill all rules for a qualified name of a class in Java";
	public static final String CLASS_NAME_CONTAINS_KEYWORD_PROBLEM = "The new class name contains Java keyword";
	public static final String CLASS_NAME_DUPLICATE_PROBLEM = "The model already contains a class with this name";
	public static final String MISSING_PARENT_PROBLEM = "Missing current or new parent of moved class";
	public static final String METHOD_NAME_REGEX_PROBLEM = "The method name should fulfill all rules for naming method in Java";
	public static final String METHOD_SIGNATURE_DUPLICATE_PROBLEM = "The class already contains model of a method with identical signature";
	public static final String UNEXPECTED_PROBLEM_WHILE_ADDING_ELEMENT = "Element could not be added to the model";
	public static final String UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT = "Element could not be removed from the model";
	public static final String METHODS_INCOMPATIBLE_PROBLEM = "The converted methods do not have the same parameter count and types";
	public static final String CATEGORY_NAME_DUPLICATE_PROBLEM = "The method already contains a parameter with this name";
	public static final String CATEGORY_NAME_REGEX_PROBLEM = "Parameter name must be a valid java identifier";
	public static final String CATEGORY_TYPE_REGEX_PROBLEM = "Parameter type must be a valid type identifier in Java, i.e. it must be either a primitive type name or String or a valid qualified type name of user type";
	public static final String CATEGORY_DEFAULT_VALUE_REGEX_PROBLEM = "The entered value is not compatible with parameter type";
	public static final String CONSTRAINT_NAME_REGEX_PROBLEM = "Constraint name not allowed";
	public static final String TEST_CASE_NAME_REGEX_PROBLEM = "Test case name not allowed";
	public static final String INCOMPATIBLE_CONSTRAINT_PROBLEM = "The added constraint does not match the method model";
	public static final String DIALOG_UNALLOWED_RELATION_MESSAGE = "This relation is not allowed for given statement";
	public static final String NULL_POINTER_TARGET = "The target of operation is invalid";
	public static final String TARGET_STATEMENT_NOT_FOUND_PROBLEM = "The target statement for this operation could not be found";
	
	public static final String OPERATION_NOT_SUPPORTED_PROBLEM = "Operation not supported";
	public static final String TEST_CASE_INCOMPATIBLE_WITH_METHOD = "Target method must have the same number of parameters and corresponding partition names as added test case.";
	public static final String TEST_DATA_CATEGORY_MISMATCH_PROBLEM = "New test value has wrong parent category.";
	public static final String PARTITION_NAME_DUPLICATE_PROBLEM = "The partition with this name already exists";
	public static final String EXPECTED_USER_TYPE_CATEGORY_LAST_PARTITION_PROBLEM = "User type expected parameters must have at least one partition. It's value will define the default expected value of the parameter";

}
