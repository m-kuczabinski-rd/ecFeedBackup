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
	final String METHOD_NAME_INT = "stubMethodInt";

	private ArgParser fArgParserInvoker = null;
	private MethodInvoker fMethodInvoker = null;
	private boolean fWasInvoked = false;
	private String fErrorMessage = null;


	public void stubMethodInt(int arg) {
		fWasInvoked = true;
	}

	public void setErrorMessage(String errorMessage) {
		fErrorMessage = errorMessage;
	}

	@Before
	public void setUp() {

		LoggerStub loggerStub = new LoggerStub(this);  
		fArgParserInvoker = new ArgParser(loggerStub);
		fMethodInvoker = new MethodInvoker(loggerStub);

		fWasInvoked = false;
	}

	@Test
	public void shouldReportErrorWhenNoTestArguments(){
		invokeWithError("", ArgParser.ErrorCode.NO_TEST_ARGUMENTS);
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
	public void shouldReportErrorWhenNoParam(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT), 
				ArgParser.ErrorCode.NO_METHOD_PARAMETERS);
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
	public void shouldInvokeForIntParam(){
		invokeWithSuccess(joinParams(CLASS_NAME, METHOD_NAME_INT, "int[0]"));
	}

	private void invokeWithSuccess(String params) {
		Invocable invokee 
		= fArgParserInvoker.createMethodToInvoke(
				this, 
				params);
		assertNotNull(invokee);

		fMethodInvoker.invokeMethod(invokee);
		assertTrue(fWasInvoked);
	}

	private void invokeWithError(String params, ArgParser.ErrorCode errorCode) {

		Invocable invokee = null;

		try {
			invokee = fArgParserInvoker.createMethodToInvokeWithErrorCodes(this, params);

		} catch (RuntimeException exception) {

			String errorCodeStr = "ERROR_CODE: " + errorCode.toString();

			if (!errorCodeInErrorMessage(errorCodeStr)) {
				fail("Required message: " + errorCodeStr + " was not logged");
			}

			assertNull(invokee);

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
}
