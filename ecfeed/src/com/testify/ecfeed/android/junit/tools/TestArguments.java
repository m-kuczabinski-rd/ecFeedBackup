/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.android.junit.tools;

public final class TestArguments {
	
	private static TestArguments fInstance = null;
	String fArguments = null;
	
	private TestArguments() {
	}
	
	private static TestArguments getInstance() {
		if(fInstance == null) {
			fInstance = new TestArguments(); 
		}
		
		return fInstance;
	}
	
	private void setArgs(String params) {
		fArguments = params;
	}

	private String getArgs() {
		return fArguments;
	}
	
	public static void set(String testArguments) {
		getInstance().setArgs(testArguments);
	}
	
	public static String get() {
		return getInstance().getArgs();
	}	
}
