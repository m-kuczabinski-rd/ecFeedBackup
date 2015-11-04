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

import com.testify.ecfeed.android.external.AndroidFactoryDistributorExt;
import com.testify.ecfeed.android.external.IInstallationDirFileHelper;
import com.testify.ecfeed.utils.DiskFileHelper;
import com.testify.ecfeed.utils.ExceptionHelper;
import com.testify.ecfeed.utils.StringHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseInstallationDirFileHelper implements IInstallationDirFileHelper {

	private static final String PREFIX_FILE = "file:";	

	@Override
	public String getFullPathToFile(String relativePathToFile) {
		final String androidToolsJarUrl = getAbsoluteDistributionFileUrl(relativePathToFile);

		if (androidToolsJarUrl != null) {
			return StringHelper.removePrefix(PREFIX_FILE, androidToolsJarUrl);
		}

		final String ecfeedJarUrl = getAbsoluteDistributionFileUrl(DiskFileHelper.CURRENT_DIR);
		if (null == ecfeedJarUrl) {
			ExceptionHelper.reportRuntimeException(
					EXCEPTION_FILE_NOT_FOUND_IN_INSTALLATION_DIR(relativePathToFile));
		}

		ExceptionHelper.reportRuntimeException(
				EXCEPTION_FILE_NOT_FOUND_IN_INSTALLATION_DIR2(
						relativePathToFile, getInstallationDir(ecfeedJarUrl)));

		return null;
	}

	private String getAbsoluteDistributionFileUrl(final String relativePath) {
		final Bundle bundle = FrameworkUtil.getBundle(getAnyClassFromAndroidPlugin());

		final URL url = FileLocator.find(bundle, new Path(relativePath), null);
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

	private Class<?> getAnyClassFromAndroidPlugin() {
		return (AndroidFactoryDistributorExt.getFactory()).getClass();
	}

	private String getInstallationDir(String ecfeedJarUrl) {
		final String postfix = 
				DiskFileHelper.FILE_SEPARATOR + DiskFileHelper.CURRENT_DIR + DiskFileHelper.FILE_SEPARATOR;

		return StringHelper.removePostfix(postfix, ecfeedJarUrl);
	}

	private static final String EXCEPTION_FILE_NOT_FOUND_IN_INSTALLATION_DIR(final String relativePath) {
		return "Can not find: " + relativePath + " in ecFeed installation directory."; 
	}

	private static final String EXCEPTION_FILE_NOT_FOUND_IN_INSTALLATION_DIR2(
			final String relativePath, final String installationDir) {
		return "Can not find: " + relativePath + " in ecFeed installation directory: " + installationDir; 
	}	
}
