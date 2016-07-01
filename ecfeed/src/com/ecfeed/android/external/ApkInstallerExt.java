/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.android.external;

import java.lang.reflect.InvocationTargetException;

import com.ecfeed.core.utils.ExceptionHelper;
import com.testify.ecfeed.ui.common.Messages;

public class ApkInstallerExt { 

	private IProjectHelper fProjectHelper;
	
	public ApkInstallerExt(IProjectHelper projectHelper) {
		fProjectHelper = projectHelper;
	}
	
	public void installApplicationsIfModified() throws InvocationTargetException {

		if (fProjectHelper.isNoInstallDevelopmentHook()) {
			return;
		}

		final IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();
		final IApkInstallerExt apkInstaller = androidFactory.createApkInstaller();

		final String testingApk = getTestingApkPathAndName();
		final String testedApk = getTestedApkPathAndName();

		apkInstaller.installApplicationsIfModified(testedApk, testingApk);
	}

	private String getTestingApkPathAndName() {
		final String testingApk = fProjectHelper.getApkPathAndName();

		if (testingApk == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_CAN_NOT_INSTALL_APK_FILE);
		}

		return testingApk;
	}

	private String getTestedApkPathAndName() {
		final String testedApk = fProjectHelper.getReferencedApkPathAndName();

		if (testedApk == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_CAN_NOT_INSTALL_APK_FILE);
		}

		return testedApk;
	}

}

