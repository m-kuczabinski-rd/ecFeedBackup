/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DiskFileHelper {

	public static final String JAVA_EXTENSION = "java";
	public static final String FILE_SEPARATOR = File.separator; // platform independent
	public static final String CURRENT_DIR = ".";
	public static final String EXTENSION_SEPARATOR = ".";
	public static final String APK_EXTENSION = "apk";
	public static final String BIN_SUBDIRECTORY = "bin";
	public static final String ALLOWED_CHARS_FOR_ECT_NAME = "[a-zA-Z0-9_\\.]";

	public static String pathSeparator() {
		return FILE_SEPARATOR;
	}

	public static boolean fileExists(String pathWithFileName) {
		File file = new File(pathWithFileName);

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

	public static long fileModificationTime(String path) {
		File file = new File(path);

		if (!file.exists()) {
			return 0;
		}

		return file.lastModified();
	}

	public static String extractFileName(String pathWithFileName) {
		return StringHelper.getLastToken(pathWithFileName, FILE_SEPARATOR);
	}

	public static String extractPath(String pathWithFileName) {
		String fileName = StringHelper.getLastToken(pathWithFileName, FILE_SEPARATOR);
		return StringHelper.removeFromPostfix(fileName, pathWithFileName);
	}

	public static String extractFileNameWithoutExtension(String fileNameWithExtension) {
		return StringHelper.getFirstToken(fileNameWithExtension, EXTENSION_SEPARATOR);
	}

	public static String checkEctFileName(String fileName) {
		final String ALLOWED_CHARS = "[a-zA-Z0-9_\\.]";
		final String FILE_NAME_MUST_NOT_START_WITH_SPACE = "Ect file name must not start with space."; 
		final String FILE_NAME_DOES_NOT_HAVE_ECT_SUFFIX = "Ect file name does not have .ect suffix.";
		final String FILE_NAME_MUST_NOT_CONTAIN_DOTS = "Ect file name must not contain 'dot' character.";

		final String ECT_FILE_NAME_WITH_INVALID_CHAR_1 = "Ect file name: <";
		final String ECT_FILE_NAME_WITH_INVALID_CHAR_2 = "> contains invalid character: <";
		final String ECT_FILE_NAME_WITH_INVALID_CHAR_3 = ">.";

		if (fileName.startsWith(" ")) {
			return FILE_NAME_MUST_NOT_START_WITH_SPACE;
		}

		if (!fileName.endsWith(".ect")) {
			return FILE_NAME_DOES_NOT_HAVE_ECT_SUFFIX;
		}

		int occurencesOfDot = StringHelper.countOccurencesOfChar(fileName, '.');
		if (occurencesOfDot > 1) {
			return FILE_NAME_MUST_NOT_CONTAIN_DOTS;
		}

		String invalidChar = StringHelper.containsOnlyAllowedChars(fileName, ALLOWED_CHARS);
		if (invalidChar != null) {
			return ECT_FILE_NAME_WITH_INVALID_CHAR_1 + fileName + 
					ECT_FILE_NAME_WITH_INVALID_CHAR_2 + invalidChar + 
					ECT_FILE_NAME_WITH_INVALID_CHAR_3;
		}

		return null;
	}

	public static void createNewFile(String pathWithFileName) {
		File file = new File(pathWithFileName);
		if (file.exists()) {
			ExceptionHelper.reportRuntimeException("File: " + pathWithFileName + " already exists.");
		}

		boolean fileCreated = false; 
		try {
			fileCreated = file.createNewFile();
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}

		if (!fileCreated) {
			ExceptionHelper.reportRuntimeException("Can not create file: " + pathWithFileName + " .");
		}
	}

	public static void deleteFile(String pathWithFileName) {
		File file = new File(pathWithFileName);
		if (!file.delete()) {
			ExceptionHelper.reportRuntimeException("Can not delete file: " + pathWithFileName + " .");
		}
	}

	public static void saveStringToFile(String pathWithFileName, String newContents) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(pathWithFileName, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			ExceptionHelper.reportRuntimeException(
					"Can not write string to file: " + pathWithFileName + " ,reason: " + e.getMessage());
		}
		writer.print(newContents);
		writer.close();
	}

	public static String readStringFromFile(String pathWithFileName) {
		String result = null;
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(pathWithFileName));
			result = new String(encoded, "UTF-8");
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException(
					"Can not read string from file: " + pathWithFileName + " ,reason: " + e.getMessage());
		}
		return result;
	}

}
