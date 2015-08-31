/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.testify.ecfeed.generators.api.EcException;

public class StreamHelper {

	public static String streamToString(InputStream is) throws EcException {
		String exceptionMessage = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder stringBuilder = null;

		try {
			stringBuilder = readLines(reader);
		} catch (IOException e) {
			exceptionMessage = e.getMessage();
		} finally {
			tryCloseReader(reader);
		}

		if (exceptionMessage != null) {
			EcException.report(exceptionMessage);
		}

		return stringBuilder.toString();
	}

	private static StringBuilder readLines(BufferedReader reader) throws IOException {
		StringBuilder out = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			out.append(line);
			out.append("\n");
		}

		return out;
	}

	private static void tryCloseReader(BufferedReader reader) {
		try {
			reader.close();
		} catch (IOException e) {
			SystemLogger.logCatch(e.getMessage());
		}
	}	
}
