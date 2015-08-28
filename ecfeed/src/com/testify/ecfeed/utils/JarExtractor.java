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

	public static String getFileContents(String filePathName, String jarPathName) throws EcException {
		File jarFile = new File(jarPathName);
		JarFile jar = null;
		String fileContents = null;
		String exceptionMessage = null;

		try {
			jar = new JarFile(jarFile);
			fileContents = readFileContents(filePathName, jar);
		} catch (IOException | EcException e) {
			exceptionMessage = e.getMessage();
		} finally {
			tryCloseJar(jar);
		}

		if (exceptionMessage != null) {
			EcException.report(exceptionMessage);
		}

		return fileContents;
	}

	private static String readFileContents(String filePathName, JarFile jarFile) throws IOException, EcException {
		Enumeration<JarEntry> jarEntries = jarFile.entries();

		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = (JarEntry)jarEntries.nextElement();

			String jarEntryName = jarEntry.getName(); 
			if (jarEntryName.equals(filePathName)) {
				return readJarEntryContents(jarEntry, jarFile);
			}
		}

		return null;
	}

	private static String readJarEntryContents(JarEntry jarEntry, JarFile jarFile) throws EcException, IOException {
		String contents = null;

		InputStream inputStream = jarFile.getInputStream(jarEntry);
		contents = streamToString(inputStream);
		tryCloseInputStream(inputStream);

		return contents;
	}

	private static String streamToString(java.io.InputStream is) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();

		for(;;) {
			String dataChunk = readDataChunk(is);

			if (dataChunk == null) {
				break;
			}

			stringBuilder.append(dataChunk);
		}

		return stringBuilder.toString();
	}

	private static String readDataChunk(InputStream is) throws IOException {
		byte[] arr = new byte[1000];

		int bytesRead = is.read(arr);
		if (bytesRead <= 0) {
			return null;
		}

		return new String(arr);
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
