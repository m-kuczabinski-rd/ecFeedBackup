/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import com.testify.ecfeed.ui.common.external.IFileInfoProvider;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.testify.ecfeed.utils.ExceptionHelper;

public class ApkInstaller {

	public static void installApk(IFileInfoProvider fileInfoProvider) throws InvocationTargetException {
		String apkPathAndName = EclipseProjectHelper.getApkPathAndName(fileInfoProvider);

		if (apkPathAndName == null) {
			ExceptionHelper.reportRuntimeException("Can not install apk file.");
		}

		System.out.println("Installing apk...");
		Process process = startProcess(apkPathAndName);
		logOutput(process);
		waitFor(process);
	}	

	private static Process startProcess(String apkPathAndName) {

		ProcessBuilder pb
		= new ProcessBuilder(
				"adb", 
				"install",
				"-r",
				apkPathAndName);

		Process process = null;
		try {
			process = pb.start();
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException("Can not create install process.");
		}
		return process;
	}

	private static void logOutput(Process process) {
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		String line;
		try {
			while ((line = br.readLine()) != null) {
				System.out.println("\t" + line);
			}
		} catch (IOException e) {
			System.out.println("Can not log output.");
		}
	}

	private static void waitFor(Process process) {
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}	
}
