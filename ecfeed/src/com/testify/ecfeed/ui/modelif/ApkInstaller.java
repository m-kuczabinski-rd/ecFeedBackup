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

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.external.IFileInfoProvider;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.testify.ecfeed.utils.DiskFileHelper;
import com.testify.ecfeed.utils.ExceptionHelper;

public class ApkInstaller {

	static class FileDescription {
		String pathAndName = null;
		long modificationTime = 0;
	}

	static FileDescription fTestingApkDescription;
	static FileDescription fTestedApkDescription;

	static {
		fTestingApkDescription = new FileDescription();
		fTestedApkDescription = new FileDescription();
	}

	public static void installApplicationsIfModified(IFileInfoProvider fileInfoProvider) throws InvocationTargetException {
		String testingApk = getTestingApkPathAndName(fileInfoProvider);
		installApkIfModified(testingApk, fTestingApkDescription, Messages.INSTALLING_TESTING_APP);

		String testedApk = getTestedApkPathAndName(fileInfoProvider);
		installApkIfModified(testedApk, fTestedApkDescription, Messages.INSTALLING_TESTED_APP);
	}

	private static String getTestingApkPathAndName(IFileInfoProvider fileInfoProvider) {
		String testingApk = EclipseProjectHelper.getApkPathAndName(fileInfoProvider);

		if (testingApk == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_CAN_NOT_INSTALL_APK_FILE);
		}

		return testingApk;
	}

	private static String getTestedApkPathAndName(IFileInfoProvider fileInfoProvider) {
		String testedApk = EclipseProjectHelper.getReferencedApkPathAndName(fileInfoProvider);

		if (testedApk == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_CAN_NOT_INSTALL_APK_FILE);
		}

		return testedApk;
	}

	private static void installApkIfModified(
			String apkPathAndName, FileDescription apkDescription, String installMessage) {

		if (!apkWasModified(apkPathAndName, apkDescription)) {
			return;
		}

		installApk(apkPathAndName, installMessage);

		apkDescription.pathAndName = apkPathAndName;
		apkDescription.modificationTime = DiskFileHelper.fileModificationTime(apkPathAndName);
	}

	private static boolean apkWasModified(String apkPathAndName, FileDescription fileDescription) {
		if (apkPathAndName == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_APK_MUST_NOT_BE_NULL);
		}
		if (fileDescription.pathAndName == null) {
			return true;
		}
		if (!apkPathAndName.equals(fileDescription.pathAndName)) {
			return true;
		}

		long currentModificationTime = DiskFileHelper.fileModificationTime(apkPathAndName);
		if (currentModificationTime == 0) {
			return true;
		}
		if (fileDescription.modificationTime != currentModificationTime) {
			return true;
		}
		return false;
	}

	private static void installApk(String apkPathAndName, String installMessage) {
		System.out.println(installMessage);
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
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_CAN_NOT_CREATE_INSTALL_PROCESS);
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
			System.out.println(Messages.CAN_NOT_LOG_OUTPUT);
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
