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
	public static final String FILE_SEPARATOR = File.separator;
	public static final String CURRENT_DIR = ".";
	public static final String EXTENSION_SEPARATOR = ".";
	public static final String APK_EXTENSION = "apk";
	public static final String BIN_SUBDIRECTORY = "bin";

	public static boolean fileExists(String pathWithName) {
		File file = new File(pathWithName);

		if(file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}

	public static String createFileName(String fileNameWithoutExtension, String extension) {
		return fileNameWithoutExtension + EXTENSION_SEPARATOR + extension;
	}

	public static String joinPathWithFile(String path, String file) {
		return joinItems(path, file);
	}

	public static String joinSubdirectory(String path, String subdir) {
		return joinItems(path, subdir);
	}	

	private static String joinItems(String item1, String item2) {
		return item1 + FILE_SEPARATOR + item2;
	}


}
