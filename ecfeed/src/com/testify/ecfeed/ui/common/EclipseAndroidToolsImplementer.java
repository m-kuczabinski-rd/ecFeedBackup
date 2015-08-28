/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.utils.StringHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseAndroidToolsImplementer implements IImplementer {

	private static final String TOOLS_RELATIVE_PATH = "lib/android_tools.jar"; 

	public EclipseAndroidToolsImplementer(
			IFileInfoProvider fileInfoProvider) throws EcException {
	}

	public void implementContent() throws EcException {
	}

	public boolean contentImplemented() throws EcException {
		return true;
	}

	@SuppressWarnings("unused")
	private String getAndroidToolsJarPath() throws EcException {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());

		URL url = FileLocator.find(bundle, new Path(TOOLS_RELATIVE_PATH), null);
		if (url == null) {
			EcException.report("Can not find: " + TOOLS_RELATIVE_PATH + " in ecFeed installation directory.");
		}

		URL resolvedUrl = null;
		try {
			resolvedUrl = FileLocator.resolve(url);
		} catch (IOException e) {
			SystemLogger.logCatch(e.getMessage());
			EcException.report(e.getMessage());
		} 

		return StringHelper.removePrefix("file:", resolvedUrl.toString());
	}
}
