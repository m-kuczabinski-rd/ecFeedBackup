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
import com.testify.ecfeed.utils.DiskFileHelper;
import com.testify.ecfeed.utils.JarExtractor;
import com.testify.ecfeed.utils.StringHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseAndroidToolsImplementer implements IImplementer {

	private static final String RELATIVE_PATH_TO_JAR = "lib/android_tools.jar";
	private static final String RELATIVE_PATH_IN_JAR = "com/testify/ecfeed/android/junit/tools/";
	private static final String PACKAGE = "com.testify.ecfeed.android.junit.tools";

	private static final String[] FILE_NAMES = {
		"ArgParser", 
		"ILogger",
		"IMethodInvoker",
		"Invocable",
		"MethodInvoker",
		"TestArguments",
		"TestHelper"
	};

	private String fAndroidToolsJarPath;
	private IFileInfoProvider fFileInfoProvider;

	public EclipseAndroidToolsImplementer(
			IFileInfoProvider fileInfoProvider) throws EcException {
		fFileInfoProvider = fileInfoProvider;
		fAndroidToolsJarPath = getAndroidToolsJarPath(); 
	}

	@Override
	public void implementContent() throws EcException {
		for (String fileName : FILE_NAMES) {
			implementClass(fileName);
		}
	}

	private void implementClass(String fileName) throws EcException {
		if (EclipseClassImplementHelper.classImplemented(PACKAGE, fileName)) {
			return;
		}

		String pathAndFileName = RELATIVE_PATH_IN_JAR + fileName + "." + DiskFileHelper.JAVA_EXTENSION;
		String classContent = JarExtractor.getFileContents(pathAndFileName, fAndroidToolsJarPath);

		EclipseClassImplementHelper.implementClass(PACKAGE, fileName, classContent,fFileInfoProvider);
	}

	@Override
	public boolean contentImplemented() throws EcException {
		for (String fileName : FILE_NAMES) {
			if (!EclipseClassImplementHelper.classImplemented(PACKAGE, fileName)) {
				return false;
			}
		}		
		return true;
	}

	private String getAndroidToolsJarPath() throws EcException {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());

		URL url = FileLocator.find(bundle, new Path(RELATIVE_PATH_TO_JAR), null);
		if (url == null) {
			EcException.report("Can not find: " + RELATIVE_PATH_TO_JAR + " in ecFeed installation directory.");
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
