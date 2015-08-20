/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

public class SystemLogger {

	private static final int ONE_LEVEL_DOWN_ON_STACK = 1;

	public static void logCatch(String message) {
		logLine("ECFEEDERR: Exception caught");
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
