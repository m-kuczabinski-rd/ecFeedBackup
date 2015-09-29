/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;

import java.lang.reflect.InvocationTargetException;

import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.testify.ecfeed.utils.ExceptionHelper;

public class ApkInstallerExt { 

	public static void installApplicationsIfModified(
			IFileInfoProvider fileInfoProvider) throws InvocationTargetException {

		if (EclipseProjectHelper.isNoInstallDevelopmentHook(fileInfoProvider)) {
			return;
		}

		final IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();
		final IApkInstallerExt apkInstaller = androidFactory.createApkInstaller();

		final String testingApk = getTestingApkPathAndName(fileInfoProvider);
		final String testedApk = getTestedApkPathAndName(fileInfoProvider);

		apkInstaller.installApplicationsIfModified(testedApk, testingApk);
	}

	private static String getTestingApkPathAndName(final IFileInfoProvider fileInfoProvider) {
		final String testingApk = EclipseProjectHelper.getApkPathAndName(fileInfoProvider);

		if (testingApk == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_CAN_NOT_INSTALL_APK_FILE);
		}

		return testingApk;
	}

	private static String getTestedApkPathAndName(final IFileInfoProvider fileInfoProvider) {
		final String testedApk = EclipseProjectHelper.getReferencedApkPathAndName(fileInfoProvider);

		if (testedApk == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_CAN_NOT_INSTALL_APK_FILE);
		}

		return testedApk;
	}

}

