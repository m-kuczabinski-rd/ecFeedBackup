/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.PluginVersionExceptionReporter;
import com.testify.ecfeed.ui.common.external.IApkInstallerExt;
import com.testify.ecfeed.ui.common.external.IFileInfoProvider;
import com.testify.ecfeed.utils.ExceptionHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class ApkInstallerExtLauncher {

	public static void installApplicationsIfModified(IFileInfoProvider fileInfoProvider) throws InvocationTargetException {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		final String APK_INSTALLER_ID = "com.testify.ecfeed.extensionpoint.definition.apkinstaller"; 

		IConfigurationElement[] config =
				registry.getConfigurationElementsFor(APK_INSTALLER_ID);		

		try {
			for (IConfigurationElement element : config) {
				final Object obj = element.createExecutableExtension("class");

				if (obj instanceof IApkInstallerExt) {
					IApkInstallerExt apkInstaller = (IApkInstallerExt)obj;
					apkInstaller.installApplicationsIfModified(fileInfoProvider);
					return;
				}
			}
		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
			PluginVersionExceptionReporter.reportRuntimeException(
					e.getMessage(), IApkInstallerExt.INTERFACE_NAME, IApkInstallerExt.INTERFACE_VERSION);
		}	

		ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_EXTERNAL_APK_INSTALLER_NOT_FOUND);		
	}
}
