/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;

import java.lang.reflect.InvocationTargetException;

public class ApkInstallerExt { 

	public static void installApplicationsIfModified(
			IFileInfoProvider fileInfoProvider) throws InvocationTargetException {

		IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();
		IApkInstallerExt apkInstaller = androidFactory.createApkInstaller();
		apkInstaller.installApplicationsIfModified(fileInfoProvider);
	}
}
