/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.ui.common.external.IPluginVersionDistributorExt;
import com.testify.ecfeed.utils.ExceptionHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class PluginVersionExceptionReporter {

	public static void reportEcException(
			String message, String interfaceName, String expectedInterfaceVersion) throws EcException {

		EcException.report(createThrowMessage(message, interfaceName, expectedInterfaceVersion));
	}

	public static void reportRuntimeException(
			String message, String interfaceName, String expectedInterfaceVersion) {

		ExceptionHelper.reportRuntimeException(createThrowMessage(message, interfaceName, expectedInterfaceVersion));			
	}	

	private static String createThrowMessage(
			String message, String interfaceName, String expectedInterfaceVersion) {

		String pluginVersion = getPluginVersion(interfaceName);

		if (pluginVersion == null) {
			return message;
		}
		if (pluginVersion == expectedInterfaceVersion) {
			return message + "  Info.  Plugin version: " + pluginVersion + ",  Expected version: " + expectedInterfaceVersion;
		}
		return message + "  Warning!  Plugin version: " + pluginVersion + ",  Expected version: " + expectedInterfaceVersion;
	}

	private static String getPluginVersion(String interfaceName) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		final String ANDROID_IMPLEMENTER_ID = "com.testify.ecfeed.extensionpoint.definition.versiondistributor";

		IConfigurationElement[] config =
				registry.getConfigurationElementsFor(ANDROID_IMPLEMENTER_ID);		

		try {
			for (IConfigurationElement element : config) {
				final Object obj = element.createExecutableExtension("class");

				if (obj instanceof IPluginVersionDistributorExt) {
					IPluginVersionDistributorExt distributor = (IPluginVersionDistributorExt)obj;
					return distributor.getVersionOfInterface(interfaceName);
				}
			}
		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
		}

		return null;
	}
}
