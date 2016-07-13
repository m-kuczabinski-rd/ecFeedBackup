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

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class SystemLogger {

	private static final String EC_FEED_ERROR = "ECFEEDERR"; 
	private static final String EC_FEED_INFO = "ECFEEDINF";
	private static final int ONE_LEVEL_DOWN_ON_STACK = 1;  // level1: logCatch
	private static final int TWO_LEVELS_DOWN_ON_STACK = 2; // level1: logThrow, level2 SomeException.report
	
	private static final String LOG = "ecFeedLog.txt";
	private static boolean fLogToFile = false;
	private static boolean fFirstLogError = true;
	
	public static void setLogToFileAndOutput() {
		fLogToFile = true;
	}

	public static void logThrow(String message) {
		logSimpleLine( EC_FEED_ERROR + ": Exception thrown");
		logIndentedLine("Message: " + message);

		StackTraceElement[] stackElements = new Throwable().getStackTrace(); 
		StackTraceElement currentElement = stackElements[TWO_LEVELS_DOWN_ON_STACK];
		logCurrentStackElement(currentElement);
		logStack(stackElements);
	}

	public static void logCatch(String message) {
		logSimpleLine(EC_FEED_ERROR + ": Exception caught");
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
		logSimpleLine(EC_FEED_INFO + ": " + message);
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
		logLine("\t" + line);
	}

	private static void logIndented2Line(String line) {
		logLine("\t\t" + line);
	}	

	private static void logSimpleLine(String line) {
		logLine(line);
	}

	private static void logEmptyLine() {
		logLine("");
	}
	
	public static void logLine(String line) {
		System.out.println(line);
		
		if (!fLogToFile) {
			return;
		}
		
		try {
			TextFileHelper.appendLine(LOG, line);
		} catch (IOException e) {
			if (fFirstLogError) {
				fFirstLogError = false;
				final String DIALOG_TITLE = "Reported problem";
				final String MSG = "Can not write to log file !";
				MessageDialog.openError(Display.getDefault().getActiveShell(), DIALOG_TITLE, MSG);
				System.out.println(MSG);
			}
		}
	}
}
