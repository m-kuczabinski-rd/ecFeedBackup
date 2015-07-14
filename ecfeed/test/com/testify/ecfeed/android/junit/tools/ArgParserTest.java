/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.android.junit.tools;

import com.testify.ecfeed.android.junit.tools.ArgParser;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

class LoggerStub implements ILogger {

	ArgParserTest fArgParserInvokerTest;

	LoggerStub(ArgParserTest argParserInvokerTest) {
		fArgParserInvokerTest = argParserInvokerTest;
	}

	@Override
	public void log(String message) {
		fArgParserInvokerTest.setErrorMessage(message);
		//		System.out.println(message);
	}
}

public class ArgParserTest{

	final String CLASS_NAME = "com.testify.ecfeed.android.junit.tools.ArgParserTest";
	final String METHOD_NAME = "stubMethod";
	final String METHOD_NAME_INT = "stubMethodInt";
	final String METHOD_NAME_BOOLEAN = "stubMethodBoolean";
	final String METHOD_NAME_LONG = "stubMethodLong";
	final String METHOD_NAME_SHORT = "stubMethodShort";
	final String METHOD_NAME_BYTE = "stubMethodByte";
	final String METHOD_NAME_DOUBLE = "stubMethodDouble";
	final String METHOD_NAME_FLOAT = "stubMethodFloat";
	final String METHOD_NAME_CHAR = "stubMethodChar";
	final String METHOD_NAME_STRING = "stubMethodString";
	final String METHOD_NAME_USER_ENUM = "stubMethodUserEnum";
	final String METHOD_NAME_INT_BOOLEAN = "stubMethodIntBoolean";
	final String METHOD_NAME_LONG_SHORT_BYTE = "stubMethodLongShortByte";
	final String METHOD_NAME_DOUBLE_FLOAT_CHAR_STRING = "stubMethodDoubleFloatCharString";

	enum UserEnum {
		ONE,
		TWO
	}

	private ArgParser fArgParser = null;
	private MethodInvoker fMethodInvoker = null;
	private boolean fWasInvoked = false;
	private String fErrorMessage = null;


	public void stubMethod() {
		fWasInvoked = true;
	}

	public void stubMethodInt(int arg) {
		fWasInvoked = true;
	}

	public void stubMethodBoolean(boolean arg) {
		fWasInvoked = true;
	}

	public void stubMethodLong(long arg) {
		fWasInvoked = true;
	}	

	public void stubMethodShort(short arg) {
		fWasInvoked = true;
	}	

	public void stubMethodByte(byte arg) {
		fWasInvoked = true;
	}	

	public void stubMethodDouble(double arg) {
		fWasInvoked = true;
	}	

	public void stubMethodFloat(float arg) {
		fWasInvoked = true;
	}	

	public void stubMethodChar(char arg) {
		fWasInvoked = true;
	}	

	public void stubMethodString(String arg) {
		fWasInvoked = true;
	}

	public void stubMethodUserEnum(ArgParserTest.UserEnum arg) {
		fWasInvoked = true;
	}

	public void stubMethodIntBoolean(int arg1, boolean arg2) {
		fWasInvoked = true;
	}	

	public void stubMethodLongShortByte(long arg1, short arg2, byte arg3) {
		fWasInvoked = true;
	}	

	public void stubMethodDoubleFloatCharString(double arg1, float arg2, char arg3, String arg4) {
		fWasInvoked = true;
	}	


	@Before
	public void setUp() {

		LoggerStub loggerStub = new LoggerStub(this);  
		fArgParser = new ArgParser(loggerStub);
		fMethodInvoker = new MethodInvoker(loggerStub);

		fWasInvoked = false;
	}

	@Test
	public void shouldReportNoErrorWhenNoTestArguments(){
		Invocable invocable = fArgParser.createMethodToInvoke(this, "");
		assertNull(invocable);

	}

	@Test
	public void shouldReportErrorWhenNoClass(){
		invokeWithError("DummyClass", ArgParser.ErrorCode.NO_CLASS_FOR_TYPE);
	}

	@Test
	public void shouldReportErrorWhenNoMethodDescription(){
		invokeWithError(CLASS_NAME, ArgParser.ErrorCode.NO_METHOD_DESCRIPTION);
	}

	@Test
	public void shouldReportErrorWhenNoMethodDescription2(){
		invokeWithError(CLASS_NAME + ", ", ArgParser.ErrorCode.NO_METHOD_DESCRIPTION);
	}	

	@Test
	public void shouldReportErrorWhenNoMethod(){
		invokeWithError(
				joinParams(CLASS_NAME, "dummyMethod", "int[1]"), 
				ArgParser.ErrorCode.NO_PUBLIC_METHOD);
	}	

	@Test
	public void shouldReportErrorWhenNoOpeningBracket(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT, "int"), 
				ArgParser.ErrorCode.NO_ARGUMENT_START_TAG);
	}	

	@Test
	public void shouldReportErrorWhenNoClosingBracket(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT, "int[0"), 
				ArgParser.ErrorCode.NO_ARGUMENT_END_TAG);
	}

	@Test
	public void shouldReportErrorWhenInvalidType(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT, "abc[x]"), 
				ArgParser.ErrorCode.NO_CLASS_FOR_USER_TYPE);
	}	

	@Test
	public void shouldReportErrorWhenInvalidArgument(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT, "int[abc]"), 
				ArgParser.ErrorCode.INVALID_ARGUMENT);
	}

	@Test
	public void shouldInvokeWhenNoParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME));
	}

	@Test
	public void shouldInvokeForIntParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_INT, "int[0]"));
	}

	@Test
	public void shouldInvokeForBooleanFalseParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_BOOLEAN, "boolean[false]"));
	}	

	@Test
	public void shouldInvokeForBooleanTrueParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_BOOLEAN, "boolean[true]"));
	}	

	@Test
	public void shouldInvokeForLongParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_LONG, "long[1]"));
	}	

	@Test
	public void shouldInvokeForShortParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_SHORT, "short[1]"));
	}	

	@Test
	public void shouldInvokeForByteParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_BYTE, "byte[1]"));
	}	

	@Test
	public void shouldInvokeForDoubleParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_DOUBLE, "double[1.0]"));
	}	

	@Test
	public void shouldInvokeForFloatParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_FLOAT, "float[1.0]"));
	}	

	@Test
	public void shouldInvokeForCharParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_CHAR, "char[a]"));
	}	

	@Test
	public void shouldInvokeForStringParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_STRING, "java.lang.String[abc]"));
	}	

	@Test
	public void shouldInvokeForUserEnumParam(){
		invokeWithSuccess(
				joinParams(
						CLASS_NAME, 
						METHOD_NAME_USER_ENUM, 
						"com.testify.ecfeed.android.junit.tools.ArgParserTest.UserEnum[ONE]"));
	}

	@Test
	public void shouldInvokeForIntBooleanParams(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_INT_BOOLEAN, "int[5], boolean[true]"));
	}	

	@Test
	public void shouldInvokeForLongShortByteParams(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_LONG_SHORT_BYTE, "long[5], short[3], byte[1]"));
	}

	@Test
	public void shouldInvokeForDoubleFloatCharStringParams(){
		invokeWithSuccess(
				joinParams(
						CLASS_NAME, 
						METHOD_NAME_DOUBLE_FLOAT_CHAR_STRING, 
						"double[1.0], float[1.0], char[a], java.lang.String[abc]"));
	}	


	private void invokeWithSuccess(String params) {
		Invocable invocable = 
				fArgParser.createMethodToInvoke(
						this, 
						params);
		assertNotNull(invocable);

		fMethodInvoker.invokeMethod(invocable);
		assertTrue(fWasInvoked);
	}

	private void invokeWithError(String params, ArgParser.ErrorCode errorCode) {

		Invocable invocable = null;

		try {
			invocable = fArgParser.createMethodToInvokeWithErrorCodes(this, params);

		} catch (RuntimeException exception) {

			String errorCodeStr = "ERROR_CODE: " + errorCode.toString();

			if (!errorCodeInErrorMessage(errorCodeStr)) {
				fail("Required message: " + errorCodeStr + " was not logged");
			}

			assertNull(invocable);

			assertFalse(fWasInvoked);
			return;
		}

		fail("RuntimeException expected but not thrown.");
	}

	private String joinParams(String param1, String params2) {
		return param1 + ", " + params2;
	}

	private String joinParams(String param1, String params2, String params3) {
		return joinParams(joinParams(param1, params2), params3);
	}

	private boolean errorCodeInErrorMessage(String errorCodeStr) {
		if (fErrorMessage.indexOf(errorCodeStr) != -1) {
			return true;
		}

		return false;
	}

	public void setErrorMessage(String errorMessage) {
		fErrorMessage = errorMessage;
	}
}
