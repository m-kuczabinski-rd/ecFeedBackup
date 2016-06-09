/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.core.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TextFileHelper {

	public static void append(String filename, String message) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
		writer.write(message);
		writer.close();
	}

	public static void appendLine(String filename, String message) throws IOException {
		String msg = StringHelper.appendNewline(message);
		append(filename, msg);
	}	

	public static void appendLineToLog(String message) throws IOException {
		TextFileHelper.appendLine("ecFeedLog.txt", message);
	}
}
