/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.android.junit.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArgParserInvoker {

	static final String ARG_START_TAG = "[";
	static final String ARG_END_TAG = "]";

	private ILogger fLogger;

	ArgParserInvoker(ILogger logger) {
		fLogger = logger;
	}

	public void invoke(Object object, String testArguments) {

		if(testArguments == null) {
			logAndThrow("Error. Can not invoke test method. No test arguments.");
			return;
		}

		String methodName = null;
		Method method = null;
		Object[] arguments = null;

		try {
			List<String> argsList = createArgList(testArguments);
			Iterator<String> argsIterator = argsList.iterator();

			Class<?> theClass = createClass(argsIterator);
			methodName = createMethodName(argsIterator);

			List<ParamTypeWithArg> paramsWithArgs = createParamsWithArgs(argsIterator);

			method = createMethod(paramsWithArgs, theClass, methodName);
			arguments = createMethodArguments(paramsWithArgs);

		} catch (RuntimeException exc) {
			logAndThrow("Error while parsing parameters. Cause:" + exc.getMessage());
			return;
		}

		invokeMethod(object, method, arguments);
	}
	
	private void invokeMethod(Object object, Method method, Object[] arguments) {
		try {
			method.invoke(object, arguments);
		} catch (RuntimeException | IllegalAccessException | InvocationTargetException exception) {
			handleException(exception, object, method, arguments);
		}
	}

	private void handleException(Exception exception, Object object, Method method, Object[] arguments) {
		Throwable internalException = exception.getCause();

		if (internalException instanceof junit.framework.AssertionFailedError)
		{
			String description 
			= createInvokeErrorDescription(internalException.getMessage(), object, method, arguments);

			fLogger.log("ASSERTION FAILED: " + description);
			throw new junit.framework.AssertionFailedError(description);
		}

		logAndThrow(createInvokeErrorDescription(exception.getCause().getMessage(), object, method, arguments));
	}
	
	private void logAndThrow(String message) {
		fLogger.log(message);
		throw new RuntimeException(message);
	}

	private String createInvokeErrorDescription(
			String internalExceptionMessage, 
			Object object, 
			Method method, 
			Object[] arguments) {
		return internalExceptionMessage
				+ "\n"
				+ "\t class: " + object.getClass().getName() + "\n"
				+ "\t method: " + method.getName() + "\n"
				+ "\t arguments:"
				+ createArgumentsDescription(arguments); 
	}

	private String createArgumentsDescription(Object[] arguments) {
		String result = "";

		for (Object argument : arguments) {
			result = result + "\n\t  <" + argument.toString() + ">";
		}

		return result;
	}

	private List<String> createArgList(String arguments) {
		List<String> result = new ArrayList<String>();

		final String SEPARATOR = ","; 
		int startPos = 0;
		int indexOfSeparator = 0;

		for(;;) {
			indexOfSeparator = arguments.indexOf(SEPARATOR, startPos);

			if (indexOfSeparator == -1) {
				break;
			}

			result.add(arguments.substring(startPos, indexOfSeparator).trim());
			startPos = indexOfSeparator + 1;
		}

		result.add(arguments.substring(startPos).trim());
		return result;
	}

	private Class<?> createClass(Iterator<String> argsIterator) throws RuntimeException{

		if (!argsIterator.hasNext()) {
			throw new RuntimeException("No class description in parameter ecFeed");
		}

		String className = argsIterator.next();

		Class<?> theClass = null;

		try {
			theClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("No class for type:" + className);
		}

		return theClass;
	}

	private String createMethodName(Iterator<String> argsIterator) throws RuntimeException {
		if (!argsIterator.hasNext()) {
			throw new RuntimeException("No method description in parameter ecFeed");
		}

		return argsIterator.next();
	}

	private List<ParamTypeWithArg> createParamsWithArgs(Iterator<String> argsIterator) throws RuntimeException {
		List<ParamTypeWithArg> result = new ArrayList<ParamTypeWithArg>();

		Iterator<String> iterator = argsIterator;

		while(iterator.hasNext()) {
			String paramWithArgStr = iterator.next();

			String parameter = extractParameter(paramWithArgStr);
			String argument = extractArgument(paramWithArgStr);

			ParamTypeWithArg paramWithArg = new ParamTypeWithArg(parameter, argument);
			result.add(paramWithArg);
		}

		return result;
	}

	private Class<?>[] createMethodParameters(final List<ParamTypeWithArg> paramsWithArgs) throws RuntimeException {
		int paramCount = paramsWithArgs.size();
		Class<?>[] outParameters = new Class[paramCount];

		int index = 0;
		for (ParamTypeWithArg paramWithArg: paramsWithArgs)
		{
			String parameter = paramWithArg.parameter();
			outParameters[index++] = getClassForParameter(parameter);
		}

		return outParameters;
	}

	private Class<?> getClassForParameter(String parameter) throws RuntimeException {
		switch(parameter) {
		case "int":
			return int.class;
		case "boolean":
			return boolean.class;
		case "long":
			return long.class;
		case "short":
			return short.class;
		case "byte":
			return byte.class;
		case "double":
			return double.class;
		case "float":
			return float.class;
		case "char":
			return char.class;
		case "java.lang.String":
			return java.lang.String.class;
		default:
			try {
				return Class.forName(classNameToDollarFormat(parameter));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("No class for user type:" + parameter);
			}
		}
	}

	private Method createMethod(
			List<ParamTypeWithArg> paramsWithArgs, 
			Class<?> theClass, 
			String methodName) throws RuntimeException {

		Class<?>[] parameters = createMethodParameters(paramsWithArgs);

		Method method = null;

		try {
			method = theClass.getMethod(methodName, parameters);
		} catch (NoSuchMethodException exc1) {
			throw new RuntimeException("In class: " + theClass.getName() +
					" there is no method: " + methodName +  
					"(" + createDescrOfParameters(parameters) + ")");
		}

		return method;
	}

	private String createDescrOfParameters(Class<?>[] parameters) {
		final String SEPARATOR = ", ";
		String description = "";

		for(Class<?> parameter : parameters) {
			description = description + classNameToDotFormat(parameter.getName()) + SEPARATOR;
		}

		int lastIndex = description.lastIndexOf(SEPARATOR);
		return description.substring(0, lastIndex);
	}

	private Object[] createMethodArguments(final List<ParamTypeWithArg> paramsWithArgs) throws RuntimeException {

		int argsCount = paramsWithArgs.size();
		Object[] outArguments = new Object[argsCount];

		int index = 0;
		for (ParamTypeWithArg paramWithArg: paramsWithArgs)
		{
			outArguments[index++] = paramWithArg.convertArgument();
		}

		return outArguments;
	}	

	private String extractParameter(String paramWithArgument) throws RuntimeException {
		int endPos = paramWithArgument.indexOf(ARG_START_TAG);

		if (endPos == -1) {
			throw new RuntimeException("Argument start tag:" + ARG_START_TAG + " not found.");
		}

		return paramWithArgument.substring(0, endPos);
	}

	private String extractArgument(String paramWithArgument) throws RuntimeException {

		int startPos = paramWithArgument.indexOf(ARG_START_TAG);

		if (startPos == -1) {
			throw new RuntimeException("Argument start tag:" + ARG_START_TAG + " not found.");
		}

		int endPos = paramWithArgument.indexOf(ARG_END_TAG);

		if (endPos == -1) {
			throw new RuntimeException("Argument end tag:" + ARG_END_TAG + " not found.");
		}

		return paramWithArgument.substring(startPos + 1, endPos);
	}

	private String classNameToDollarFormat(String className) {
		return replaceLastSeparator(className, ".", "$");
	}

	private String classNameToDotFormat(String className) {
		return replaceLastSeparator(className, "$", ".");
	}

	private String replaceLastSeparator(String strToConvert, String separatorFrom, String separatorTo) {
		int lastSeparator = strToConvert.lastIndexOf(separatorFrom);

		if (lastSeparator == -1) {
			return strToConvert;
		}

		return strToConvert.substring(0, lastSeparator) + 
				separatorTo + 
				strToConvert.substring(lastSeparator + 1);
	}

	private class ParamTypeWithArg {

		private String fParameterType;
		private String fArgumentStr;

		public ParamTypeWithArg(String parameter, String argumentStr){
			fParameterType = parameter;
			fArgumentStr = argumentStr;
		}

		public String parameter() {
			return fParameterType;
		}

		public Object convertArgument() throws RuntimeException {
			switch(fParameterType) {
			case "int":
				return convertInt();
			case "boolean":
				return convertBoolean();
			case "long":
				return convertLong();
			case "short":
				return convertShort();
			case "byte":
				return convertByte();
			case "double":
				return convertDouble();
			case "float":
				return convertFloat();
			case "char":
				return convertChar();
			case "java.lang.String":
				return fArgumentStr;
			default:
				return convertEnum();
			}
		}

		private Object convertInt() throws RuntimeException {
			try {
				return Integer.decode(fArgumentStr);	
			} catch (NumberFormatException exc) {
				throwConversionException();
			}

			return null;
		}

		private Object convertBoolean() throws RuntimeException {
			try {
				return Boolean.parseBoolean(fArgumentStr);	
			} catch (NumberFormatException exc) {
				throwConversionException();
			}

			return null;
		}

		private Object convertLong() throws RuntimeException {
			try {
				return Long.decode(fArgumentStr);	
			} catch (NumberFormatException exc) {
				throwConversionException();
			}

			return null;
		}

		private Object convertShort() throws RuntimeException {
			try {
				return Short.decode(fArgumentStr);	
			} catch (NumberFormatException exc) {
				throwConversionException();
			}

			return null;
		}

		private Object convertByte() throws RuntimeException {
			try {
				return Byte.decode(fArgumentStr);	
			} catch (NumberFormatException exc) {
				throwConversionException();
			}

			return null;
		}

		private Object convertDouble() throws RuntimeException {
			try {
				return Double.parseDouble(fArgumentStr);	
			} catch (NumberFormatException exc) {
				throwConversionException();
			}

			return null;
		}

		private Object convertFloat() throws RuntimeException {
			try {
				return Float.parseFloat(fArgumentStr);	
			} catch (NumberFormatException exc) {
				throwConversionException();
			}

			return null;
		}		

		private Object convertChar() throws RuntimeException {
			if (fArgumentStr.length() != 1) {
				throwConversionException();
			}

			return fArgumentStr.charAt(0);
		}

		private Object convertEnum() throws RuntimeException {

			Class<?> theClass;
			try {
				theClass = Class.forName(classNameToDollarFormat(fParameterType));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Class not found for enum type:" + fParameterType);
			}

			for (Object enumConstant : theClass.getEnumConstants()) {
				if (enumConstant.toString().equals(fArgumentStr)) {
					return enumConstant;
				}
			}

			throwConversionException();
			return null;
		}

		private void throwConversionException() throws RuntimeException {
			throw new RuntimeException("Invalid argument:" + fArgumentStr + " of type:" + fParameterType);
		}
	}
}

