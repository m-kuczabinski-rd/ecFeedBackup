/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import java.lang.reflect.InvocationTargetException;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.external.IApkInstallerExt;
import com.testify.ecfeed.ui.common.external.IFileInfoProvider;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.testify.ecfeed.ui.common.utils.ExternalProcess;
import com.testify.ecfeed.ui.common.utils.IOutputLineProcessor;
import com.testify.ecfeed.utils.DiskFileHelper;
import com.testify.ecfeed.utils.ExceptionHelper;

public class ApkInstaller implements IApkInstallerExt {

	private static class FileDescription {
		String pathAndName = null;
		long modificationTime = 0;
	}

	static FileDescription fTestingApkDescription;
	static FileDescription fTestedApkDescription;

	static {
		fTestingApkDescription = new FileDescription();
		fTestedApkDescription = new FileDescription();
	}

	private static class InstallApkLineProcessor implements IOutputLineProcessor {

		private String fErrorLine = null;

		@Override
		public boolean printLine(String line) {
			if (fErrorLine == null && line.contains("Error")){
				fErrorLine = line;
			}
			return true;
		}

		public String getErrorLine() {
			return fErrorLine;
		}
	}

	@Override
	public void installApplicationsIfModified(IFileInfoProvider fileInfoProvider) throws InvocationTargetException {
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
		if (pathNameWasModified(apkPathAndName, fileDescription)) {
			return true;
		}
		if (fileWasModified(apkPathAndName, fileDescription)) {
			return true;
		}
		return false;
	}

	private static boolean pathNameWasModified(String apkPathAndName, FileDescription fileDescription) {
		if (fileDescription.pathAndName == null) {
			return true;
		}
		if (!apkPathAndName.equals(fileDescription.pathAndName)) {
			return true;
		}
		return false;
	}

	private static boolean fileWasModified(String apkPathAndName, FileDescription fileDescription) {
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

		ExternalProcess externalProcess = 
				new ExternalProcess(
						Messages.EXCEPTION_CAN_NOT_CREATE_INSTALL_PROCESS,
						"adb", "install", "-r", apkPathAndName);

		InstallApkLineProcessor lineProcessor = new InstallApkLineProcessor();
		externalProcess.waitForEnd(lineProcessor);

		String errorLine = lineProcessor.getErrorLine();
		if (errorLine == null) {
			return;
		}

		ExceptionHelper.reportRuntimeException("Can not install application. " + errorLine);
	}
}
