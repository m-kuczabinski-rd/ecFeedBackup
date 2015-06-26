/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.android.junit.tools;

import com.testify.ecfeed.android.junit.tools.ArgParserInvoker;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

class LoggerStub implements ILogger {

	ArgParserInvokerTest fArgParserInvokerTest;

	LoggerStub(ArgParserInvokerTest argParserInvokerTest) {
		fArgParserInvokerTest = argParserInvokerTest;
	}

	@Override
	public void log(String message) {
		fArgParserInvokerTest.setErrorMessage(message);
		System.out.println(message);
	}
}

public class ArgParserInvokerTest{

	final String CLASS_NAME = "com.testify.ecfeed.android.junit.tools.ArgParserInvokerTest";
	final String METHOD_NAME_INT = "stubMethodInt";

	private ArgParserInvoker fArgParserInvoker = null;
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
		fWasInvoked = false;
		fArgParserInvoker = new ArgParserInvoker(new LoggerStub(this));
	}

	@Test
	public void shouldReportErrorWhenNoTestArguments(){
		invokeWithError("", ArgParserInvoker.ErrorCode.NO_TEST_ARGUMENTS);
	}

	@Test
	public void shouldReportErrorWhenNoClass(){
		invokeWithError("DummyClass", ArgParserInvoker.ErrorCode.NO_CLASS_FOR_TYPE);
	}

	@Test
	public void shouldReportErrorWhenNoMethodDescription(){
		invokeWithError(CLASS_NAME, ArgParserInvoker.ErrorCode.NO_METHOD_DESCRIPTION);
	}

	@Test
	public void shouldReportErrorWhenNoMethodDescription2(){
		invokeWithError(CLASS_NAME + ", ", ArgParserInvoker.ErrorCode.NO_METHOD_DESCRIPTION);
	}	

	@Test
	public void shouldReportErrorWhenNoMethod(){
		invokeWithError(
				joinParams(CLASS_NAME, "dummyMethod", "int[1]"), 
				ArgParserInvoker.ErrorCode.NO_PUBLIC_METHOD);
	}	

	@Test
	public void shouldReportErrorWhenNoParam(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT), 
				ArgParserInvoker.ErrorCode.NO_METHOD_PARAMETERS);
	}

	@Test
	public void shouldReportErrorWhenNoOpeningBracket(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT, "int"), 
				ArgParserInvoker.ErrorCode.NO_ARGUMENT_START_TAG);
	}	

	@Test
	public void shouldReportErrorWhenNoClosingBracket(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT, "int[0"), 
				ArgParserInvoker.ErrorCode.NO_ARGUMENT_END_TAG);
	}

	@Test
	public void shouldReportErrorWhenInvalidType(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT, "abc[x]"), 
				ArgParserInvoker.ErrorCode.NO_CLASS_FOR_USER_TYPE);
	}	

	@Test
	public void shouldReportErrorWhenInvalidArgument(){
		invokeWithError(
				joinParams(CLASS_NAME, METHOD_NAME_INT, "int[abc]"), 
				ArgParserInvoker.ErrorCode.INVALID_ARGUMENT);
	}

	@Test
	public void shouldInvokeForIntParam(){
		invokeWithSuccess("stubMethodInt, int[0]");
	}



	private void invokeWithSuccess(String params) {
		fArgParserInvoker.invoke(this, "com.testify.ecfeed.android.junit.tools.ArgParserInvokerTest, " + params);
		assertTrue(fWasInvoked);
	}

	private void invokeWithError(String params, ArgParserInvoker.ErrorCode errorCode) {
		try {
			fArgParserInvoker.invokeWithErrorCodes(this, params);

		} catch (RuntimeException exception) {

			String errorCodeStr = "ERROR_CODE: " + errorCode.toString();

			if (!errorCodeInErrorMessage(errorCodeStr)) {
				fail("Required message: " + errorCodeStr + " was not logged");
			}
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
