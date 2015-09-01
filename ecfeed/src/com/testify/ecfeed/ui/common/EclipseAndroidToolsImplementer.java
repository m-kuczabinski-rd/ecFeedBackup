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
	private static final String PREFIX_FILE = "file:";

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
		String classContents = JarExtractor.getFileContents(pathAndFileName, fAndroidToolsJarPath);

		EclipseClassImplementHelper.implementClass(PACKAGE, fileName, classContents, fFileInfoProvider);
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
		String androidToolsJarUrl = getAbsoluteUrl(RELATIVE_PATH_TO_JAR);

		if (androidToolsJarUrl != null) {
			return StringHelper.removePrefix(PREFIX_FILE, androidToolsJarUrl);
		}

		String ecfeedJarUrl = getAbsoluteUrl(DiskFileHelper.CURRENT_DIR);
		if (null == ecfeedJarUrl) {
			EcException.report(Messages.EXCEPTION_FILE_NOT_FOUND_IN_INSTALLATION_DIR(RELATIVE_PATH_TO_JAR));
		}

		String postfix = DiskFileHelper.FILE_SEPARATOR + DiskFileHelper.CURRENT_DIR + DiskFileHelper.FILE_SEPARATOR;
		String installationDir = StringHelper.removePostfix(postfix, ecfeedJarUrl);
		EcException.report(
				Messages.EXCEPTION_FILE_NOT_FOUND_IN_INSTALLATION_DIR2(RELATIVE_PATH_TO_JAR, installationDir));

		return null;
	}

	private String getAbsoluteUrl(String relativePath) {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());

		URL url = FileLocator.find(bundle, new Path(relativePath), null);
		if (url == null) {
			return null;
		}

		URL resolvedUrl = null;
		try {
			resolvedUrl = FileLocator.resolve(url);
		} catch (IOException e) {
			SystemLogger.logCatch(e.getMessage());
			return null;
		} 

		return resolvedUrl.toString();
	}
}
