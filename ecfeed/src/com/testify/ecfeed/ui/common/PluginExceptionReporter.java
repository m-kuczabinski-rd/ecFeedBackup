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
import com.testify.ecfeed.utils.SystemLogger;

public class PluginExceptionReporter {

	public static void reportEcException(
			String message, String interfaceName, String expectedInterfaceVersion) throws EcException {
		String pluginVersion = getPluginVersion(interfaceName);

		if (pluginVersion == null) {
			EcException.report(message);
		}
		if (pluginVersion == expectedInterfaceVersion) {
			EcException.report(message + "  Info.  Plugin version: " + pluginVersion + ",  Expected version: " + expectedInterfaceVersion);
		}
		EcException.report(message + "  Warning!  Plugin version: " + pluginVersion + ",  Expected version: " + expectedInterfaceVersion);
	}

	private static String getPluginVersion(String interfaceName) throws EcException {
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
