package com.testify.ecfeed.modelif.java.common;

public class Messages {

	public static final String PARTITION_NAME_NOT_UNIQUE_PROBLEM = "Partition name must be unique within a category or parent partition";
	public static final String PARTITION_NAME_REGEX_PROBLEM = "Partition name must contain between 1 and 64 characters and do not contain only white space characters";
	public static final String PARTITION_VALUE_PROBLEM(String value){
		return "Value " + value + " is not valid for given parameter.\n\n" + 
				"Partition value must fit to type and range of the represented parameter.\n" +
				"Partitions of user defined type must follow Java enum defining rules.";
	}
	public static final String PROBLEM_WITH_BULK_OPERATION = "Some problems occured during the operation";
	public static final String NEGATIVE_INDEX_PROBLEM = "The index of the element must be non-negative";
	public static final String TOO_HIGH_INDEX_PROBLEM = "The index of a class must not be higher than number of classes in the model";
	public static final String MODEL_NAME_REGEX_PROBLEM = "Model name must contain between 1 and 64 alphanumeric characters or spaces.\n The model name must not start with space.";
	public static final String CLASS_NAME_REGEX_PROBLEM = "The provided name must fulfill all rules for a qualified name of a class in Java";
	public static final String CLASS_NAME_CONTAINS_KEYWORD_PROBLEM = "The new class name contains Java keyword";
	public static final String CLASS_NAME_DUPLICATE_PROBLEM = "The model already contains a class with this name";
	public static final String MISSING_PARENT_PROBLEM = "Missing current or new parent of moved class";
	public static final String METHOD_NAME_REGEX_PROBLEM = "The method name should fulfill all rules for naming method in Java";
	public static final String METHOD_SIGNATURE_DUPLICATE_PROBLEM = "The class already contains model of a method with identical signature";
	public static final String UNEXPECTED_PROBLEM_WHILE_ADDING_METHOD = "Method could not be added to the class model";
	public static final String UNEXPECTED_PROBLEM_WHILE_REMOVING_METHOD = "Method could not be removed from the class model";
	public static final String METHODS_INCOMPATIBLE_PROBLEM = "The converted methods do not have the same parameter count and types";
	public static final String CATEGORY_NAME_DUPLICATE_PROBLEM = "The method already contains a parameter with this name";
	public static final String CATEGORY_NAME_REGEX_PROBLEM = "Parameter name must be a valid java identifier";
	public static final String CATEGORY_TYPE_REGEX_PROBLEM = "Parameter type must be a valid type identifier in Java, i.e. it must be either a primitive type name or String or a valid qualified type name of user type";
	public static final String CATEGORY_DEFAULT_VALUE_REGEX_PROBLEM = "The entered value is not compatible with parameter type";

}
