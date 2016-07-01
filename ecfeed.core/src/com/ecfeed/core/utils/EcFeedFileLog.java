/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.core.utils;

import java.io.IOException;

public class EcFeedFileLog {

	private static final String LOG = "ecFeedLog.txt";

	public static void appendLine(String message) throws IOException {
		TextFileHelper.appendLine(LOG, message);
	}

	public static void appendLineNoThrow(String message) {
		try {
			TextFileHelper.appendLine(LOG, message);
		} catch (IOException e) {}
	}	
}
