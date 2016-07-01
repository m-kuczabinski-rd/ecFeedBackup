/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.android.external;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.Messages;

public class AndroidFactoryDistributorExt {

	private static IAndroidFactoryExt fAndroidFactoryExt = null;

	public static IAndroidFactoryExt getFactory() {
		if (fAndroidFactoryExt == null) {
			fAndroidFactoryExt = getFactoryFromRegistry();
		}
		return fAndroidFactoryExt;
	}

	public static IAndroidFactoryExt getFactoryFromRegistry() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final String ID = "com.testify.ecfeed.extensionpoint.definition.androidfactory";

		IConfigurationElement[] config =
				registry.getConfigurationElementsFor(ID);		

		try {
			for (IConfigurationElement element : config) {
				final Object obj = element.createExecutableExtension("class");

				if (obj instanceof IAndroidFactoryExt) {
					return (IAndroidFactoryExt)obj;
				}
			}
		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}	

		ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_EXTERNAL_DEVICE_CHECKER_NOT_FOUND);
		return null;
	}
}
