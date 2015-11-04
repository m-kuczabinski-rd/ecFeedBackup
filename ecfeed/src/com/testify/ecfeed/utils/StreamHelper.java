/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

import java.io.InputStream;
import java.util.Scanner;

public class StreamHelper {

	private final static String UTF_8_CODING = "UTF-8";
	private final static String REGEX_BEGINNING_OF_TEXT = "\\A"; 

	public static String streamToString(InputStream is) {
		Scanner scanner = new Scanner(is, UTF_8_CODING);
		String result = null;

		try {
			result = readFromScanner(scanner);
		} finally {
			scanner.close();
		}
		return result;
	}

	private static String readFromScanner(Scanner scanner) {
		if (!scanner.hasNext()) {
			return new String();
		}
		// the entire content will be read 
		return scanner.useDelimiter(REGEX_BEGINNING_OF_TEXT).next();
	}
}
