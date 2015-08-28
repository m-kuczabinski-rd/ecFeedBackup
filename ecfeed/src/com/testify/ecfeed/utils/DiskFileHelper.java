/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

import java.io.File;

public class DiskFileHelper {

	public static final String JAVA_EXTENSION = "java";

	public static boolean fileExists(String pathWithName) {
		File file = new File(pathWithName);

		if(file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}

	public static String joinPathWithFile(String path, String file) {
		return path + File.separator + file; 
	}
}
