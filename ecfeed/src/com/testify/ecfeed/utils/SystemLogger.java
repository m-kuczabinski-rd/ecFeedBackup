/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

public class SystemLogger {

	private static final String EC_FEED_ERROR = "ECFEEDERR"; 
	private static final int ONE_LEVEL_DOWN_ON_STACK = 1;  // level1: logCatch
	private static final int TWO_LEVELS_DOWN_ON_STACK = 2; // level1: logThrow, level2 SomeException.report

	public static void logThrow(String message) {
		logLine( EC_FEED_ERROR + ": Exception thrown");
		logLine("\tMessage: " + message);
		StackTraceElement element = new Throwable().getStackTrace()[TWO_LEVELS_DOWN_ON_STACK];
		printStackTraceElement(element);
	}
	
	public static void logCatch(String message) {
		logLine(EC_FEED_ERROR + ": Exception caught");
		logLine("\tMessage: " + message);
		StackTraceElement element = new Throwable().getStackTrace()[ONE_LEVEL_DOWN_ON_STACK];
		printStackTraceElement(element);
	}

	private static void printStackTraceElement(StackTraceElement element) {
		logLine("\tFile: " + element.getFileName());
		logLine("\tClass: " + element.getClassName());
		logLine("\tMethod: " + element.getMethodName());
		logLine("\tline: " + element.getLineNumber());		
	}

	private static void logLine(String line) {
		System.out.println(line);
	}
}
