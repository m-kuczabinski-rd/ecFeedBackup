/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.testify.ecfeed.generators.api.EcException;

public class JarExtractor {

	public static String getFileContent(String filePathName, String jarPathName) {
		File jarFile = new File(jarPathName);
		JarFile jar = null;
		String fileContent = null;
		String exceptionMessage = null;

		try {
			jar = new JarFile(jarFile);
			fileContent = readFileFromJar(filePathName, jar);
		} catch (IOException | EcException e) {
			SystemLogger.logCatch(e.getMessage());
			exceptionMessage = e.getMessage();
		} finally {
			tryCloseJar(jar);
		}

		if (exceptionMessage != null) {
			ExceptionHelper.reportRuntimeException(exceptionMessage);
		}

		return fileContent;
	}

	private static String readFileFromJar(String filePathName, JarFile jarFile) throws EcException {
		Enumeration<JarEntry> jarEntries = jarFile.entries();

		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = (JarEntry)jarEntries.nextElement();

			String jarEntryName = jarEntry.getName(); 
			if (jarEntryName.equals(filePathName)) {
				return readJarEntry(jarEntry, jarFile);
			}
		}

		return null;
	}

	private static String readJarEntry(JarEntry jarEntry, JarFile jarFile) throws EcException {
		String exceptionMessage = null;
		String content = null;
		InputStream inputStream = null;

		try {
			inputStream = jarFile.getInputStream(jarEntry);
			content = StreamHelper.streamToString(inputStream);
		} catch (IOException e) {
			SystemLogger.logCatch(e.getMessage());
			exceptionMessage = e.getMessage();
		} finally {
			tryCloseInputStream(inputStream);
		}

		if (exceptionMessage != null) {
			EcException.report(exceptionMessage);
		}		

		return content;
	}

	private static void tryCloseJar(JarFile jar) {
		try {
			jar.close();
		} catch (IOException e) {
			SystemLogger.logCatch(e.getMessage());
		}
	}

	private static void tryCloseInputStream(InputStream inputStream) {
		try {
			inputStream.close();
		} catch (IOException e) {
			SystemLogger.logCatch(e.getMessage());
		}
	}
}
