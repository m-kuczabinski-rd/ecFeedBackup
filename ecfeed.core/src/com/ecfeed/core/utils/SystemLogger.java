/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

public class SystemLogger {

	private static final String EC_FEED_ERROR = "ECFEEDERR"; 
	private static final String EC_FEED_INFO = "ECFEEDINF";
	private static final int ONE_LEVEL_DOWN_ON_STACK = 1;  // level1: logCatch
	private static final int TWO_LEVELS_DOWN_ON_STACK = 2; // level1: logThrow, level2 SomeException.report

	public static void logThrow(String message) {
		logLine( EC_FEED_ERROR + ": Exception thrown");
		logIndentedLine("Message: " + message);

		StackTraceElement[] stackElements = new Throwable().getStackTrace(); 
		StackTraceElement currentElement = stackElements[TWO_LEVELS_DOWN_ON_STACK];
		logCurrentStackElement(currentElement);
		logStack(stackElements);
	}

	public static void logCatch(String message) {
		logLine(EC_FEED_ERROR + ": Exception caught");
		logIndentedLine("Message: " + message);
		StackTraceElement element = new Throwable().getStackTrace()[ONE_LEVEL_DOWN_ON_STACK];
		logCurrentStackElement(element);
	}

	private static void logCurrentStackElement(StackTraceElement element) {
		logIndentedLine("File: " + element.getFileName());
		logIndentedLine("Class: " + element.getClassName());
		logIndentedLine("Method: " + element.getMethodName());
		logIndentedLine("Line: " + element.getLineNumber());		
	}

	public static void logInfoWithStack(String message) {
		logLine(EC_FEED_INFO + ": " + message);
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		StackTraceElement element = stackElements[ONE_LEVEL_DOWN_ON_STACK];
		logCurrentStackElement(element);
		logStack(stackElements);
	}

	private static void logStack(StackTraceElement[] stackElements) {
		logIndentedLine("Stack:");
		for (StackTraceElement element : stackElements) {
			logStackElement(element);
		}
		logEmptyLine();
	}

	private static void logStackElement(StackTraceElement element) {
		logIndented2Line(
				"Class: " + element.getClassName() + ", " + 
						"Method: " + element.getMethodName() + ", " +
						"Line: " + element.getLineNumber());
	}

	private static void logIndentedLine(String line) {
		System.out.println("\t" + line);
	}

	private static void logIndented2Line(String line) {
		System.out.println("\t\t" + line);
	}	

	private static void logLine(String line) {
		System.out.println(line);
	}

	private static void logEmptyLine() {
		System.out.println("");
	}	
}
