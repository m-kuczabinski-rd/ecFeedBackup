/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.PluginVersionExceptionReporter;
import com.testify.ecfeed.ui.common.external.IDeviceCheckerExt;
import com.testify.ecfeed.utils.ExceptionHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class DeviceCheckerExtLauncher{

	public static void checkIfOneDeviceAttached() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		final String DEVICE_CHECKER_ID = "com.testify.ecfeed.extensionpoint.definition.devicechecker";

		IConfigurationElement[] config =
				registry.getConfigurationElementsFor(DEVICE_CHECKER_ID);		

		try {
			for (IConfigurationElement element : config) {
				final Object obj = element.createExecutableExtension("class");

				if (obj instanceof IDeviceCheckerExt) {
					IDeviceCheckerExt deviceChecker = (IDeviceCheckerExt)obj;
					deviceChecker.checkIfOneDeviceAttached();
					return;
				}
			}
		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
			PluginVersionExceptionReporter.reportRuntimeException(
					e.getMessage(), IDeviceCheckerExt.INTERFACE_NAME, IDeviceCheckerExt.INTERFACE_VERSION);
		}	

		ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_EXTERNAL_DEVICE_CHECKER_NOT_FOUND);		
	}
}
