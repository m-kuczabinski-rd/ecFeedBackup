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

package com.ecfeed.core.runner;

public class Messages {
	public final static String CANNOT_FIND_MODEL = 
			"Cannot locate model path. Make sure that the test class is annotated with EcModel annotation with right path";
	public static final String CANNOT_BUILD_MODEL = 
			"Unexpected problems while creating class model";
	public static final String PARAMETERS_ANNOTATION_LENGTH_ERROR = 
			"GeneratorParameterNames and GeneratorParameterValues must be of the same length";
	public static final String MISSING_PARAMETERS_ANNOTATION = 
			"GeneratorParameterNames and GeneratorParameterValues may be used only together";
	public static final String CANNOT_PARSE_MODEL(String message)
	{return "Model could not be parsed: " + message;}
	public static final String NO_VALID_GENERATOR(String name)
	{return "No valid generator was defined for the method " + name;}
	public static final String CLASS_NOT_FOUND_IN_THE_MODEL(String name)
	{return "Class " + name + " not found in the model";}
	public static final String MISSING_REQUIRED_PARAMETER(String name) 
	{return "Missing required parameter: " + name;}
	public static String WRONG_PARAMETER_TYPE(String name, String message) 
	{return "Error when parsing parameter " + name + ": " + message;}
	public static String CANNOT_INSTANTIATE_GENERATOR(String message) {
		{return "Cannot instantiate generator: " + message;}
	}
	public static String GENERATOR_INITIALIZATION_PROBLEM(String message) {
		{return "Generator initialization problem: " + message;}
	}
	public static String RUNNER_EXCEPTION(String message) {
		{return "Runner exception: " + message;}
	}

	///Test Runner messages
	public static final String WRONG_TEST_METHOD_SIGNATURE(String name) 
	{return "Parameter types of requested test case do not match the method signature " + name;}
	public static final String CANNOT_LOAD_CLASS(String name) 
	{return "Couldn't load class " + name;}
	public static final String METHOD_NOT_FOUND(String name) 
	{return "Method " + name + " does not exist in loaded test class";}
	public static String CANNOT_INVOKE_TEST_METHOD(String method, String parameters, String message)
	{return "Problems with invoking test method " + method + " with following parameters set: " + parameters + "\n" + message;}
	public static String TEST_METHOD_INVOCATION_EXCEPTION(String method, String testCase, String message)
	{return "Exception caught during invoking test method:\n" + method + "\nwith following parameters set:\n" + testCase /*+ "\n\n"*/ + message + "\n";}
	public static String CANNOT_PARSE_PARAMETER(String parameterType, String parameterValue)
	{return "Cannot parse value " + parameterValue + " for type " + parameterType;}
	public static String CANNOT_START_ANDROID_INSTRUMENTATION_PROCESS(String processName, String message)
	{return "Can not start Android instrumentation process: " + processName + " cause: " + message;}
	public static String IO_EXCEPITON_OCCURED(String message)
	{return "IOException occured: " + message;}	
	public static String INTERRUPTED_EXCEPTION_OCCURED(String message)
	{return "Interrupted exception occured: " + message;}	
	public static String INVALID_NUMBER_OF_PARAMS_ARGS(int parameters, int arguments)
	{return "Invalid number of parameters (" + parameters + ") and arguments (" + arguments + ")";}
	public static String CAN_NOT_READ_ANDROID_MANIFEST(String file)
	{return "Can not read Android Manifest from file: " + file;}	
	public static String CAN_NOT_OPEN_ANDROID_MANIFEST(String file)
	{return "Can not open Android Manifest file: " + file;}	
	public static String CAN_NOT_SERIALIZE_TO_ANDROID_MANIFEST(String file)
	{return "Can not serialize data to Android Manifest file: " + file;}	

}
