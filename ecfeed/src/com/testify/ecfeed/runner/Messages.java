package com.testify.ecfeed.runner;

public class Messages {
	public final static String CANNOT_FIND_MODEL = 
		"Cannot locate model path. Make sure that the test class is annotated with EcModel annotation with right path";
	public static final String CANNOT_BUILD_MODEL = 
		"Unexpected problems while creating class model";
	public static final String NO_VALID_GENERATOR(String name)
		{return "No valid generator was defined for the method" + name;}
	public static final String CLASS_NOT_FOUND_IN_THE_MODEL(String name)
		{return "Class " + name + " not found in the model";}
	public static final String MISSING_REQUIRED_PARAMETER(String name) 
		{return "Missing required parameter: " + name;}
	public static String WRONG_PARAMETER_TYPE(String name, String message) 
		{return "Error when parsing parameter " + name + ": " + message;}

}
