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

import com.testify.ecfeed.external.IImplementerExt;
import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseAndroidImplementerForClassNode {

	public boolean contentImplemented(ClassNode classNode, IFileInfoProvider fileInfoProvider) throws EcException {
		String baseRunner = classNode.getAndroidBaseRunner();
		IImplementerExt implementer = createExternalImplementer(baseRunner, fileInfoProvider);

		boolean result = false;
		try {
			result = implementer.contentImplemented();
		} catch (Exception ex) {
			EcException.report(ex.getMessage());
		}

		return result;
	}

	public void implementContent(ClassNode classNode, IFileInfoProvider fileInfoProvider) throws EcException {
		String baseRunner = classNode.getAndroidBaseRunner();
		IImplementerExt implementer = createExternalImplementer(baseRunner, fileInfoProvider);

		try {
			implementer.implementContent();
		} catch (Exception ex) {
			EcException.report(ex.getMessage());
		}
	}

	private IImplementerExt createExternalImplementer(String baseRunner, IFileInfoProvider fileInfoProvider) throws EcException {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		final String ANDROID_IMPLEMENTER_ID = "com.testify.ecfeed.extensionpoint.definition.androidimplementer";

		IConfigurationElement[] config =
				registry.getConfigurationElementsFor(ANDROID_IMPLEMENTER_ID);		

		try {
			for (IConfigurationElement element : config) {
				final Object obj = element.createExecutableExtension("class");

				if (obj instanceof IImplementerExt) {
					IImplementerExt invoker = (IImplementerExt)obj;
					invoker.initialize(baseRunner, fileInfoProvider);
					return invoker;
				}
			}
		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
		}	

		EcException.report(Messages.EXCEPTION_EXTERNAL_IMPLEMENTER_NOT_FOUND);
		return null;
	}
}
