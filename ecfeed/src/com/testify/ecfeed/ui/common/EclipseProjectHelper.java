/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.utils.DiskFileHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseProjectHelper {

	private static final String DEV_HOOK_ANDROID = "dvhAndroid";
	private static final String VALUE_ANDROID = "Android";
	private static final String ANDROID_ECLIPSE_QUALIFIER = "com.android.ide.eclipse.adt";
	private static final String ANDROID_TARGET_CACHE = "androidTargetCache";

	private static boolean fWasCalculated = false;
	private static boolean fIsAndroidProject = false;

	public static String getProjectPath(IFileInfoProvider fileInfoProvider) throws EcException {

		if (fileInfoProvider == null) {
			EcException.report(Messages.EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}

		return fileInfoProvider.getProject().getLocation().toOSString();
	}

	public static boolean isAndroidProject(IFileInfoProvider fileInfoProvider) {
		if(fWasCalculated) {
			return fIsAndroidProject;
		}
		if(isAndroidProjectDevelopmentHook(fileInfoProvider)
				|| calculateFlagIsAndroidProject(fileInfoProvider)) {
			fIsAndroidProject = true;
		}
		fWasCalculated = true;
		return fIsAndroidProject;
	}

	public static boolean isAndroidProjectDevelopmentHook(IFileInfoProvider fileInfoProvider) {
		String projectPath = null;
		try {
			projectPath = EclipseProjectHelper.getProjectPath(fileInfoProvider);
		} catch (Exception e){
			SystemLogger.logCatch(e.getMessage());
			return false;
		}

		String qualifiedName = DiskFileHelper.joinPathWithFile(projectPath, DEV_HOOK_ANDROID);
		if(DiskFileHelper.fileExists(qualifiedName)) {
			return true;
		}
		return false;
	}

	private static boolean calculateFlagIsAndroidProject(IFileInfoProvider fileInfoProvider) {
		IProject project = fileInfoProvider.getProject();

		Map<QualifiedName, String> properties = null;
		try {
			properties = project.getPersistentProperties();
		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
		}

		for(Map.Entry<QualifiedName, String> property : properties.entrySet()) {
			if(isAndroidProperty(property)) {
				return true;
			}
		}
		return false;
	}	

	private static boolean isAndroidProperty(Map.Entry<QualifiedName, String> property) {
		//e.g. com.android.ide.eclipse.adt:androidTargetCache/Android 4.4.2
		if (!property.getValue().contains(VALUE_ANDROID)) {
			return false;
		}
		QualifiedName key = property.getKey();
		if (!key.getQualifier().equals(ANDROID_ECLIPSE_QUALIFIER)) {
			return false;
		}
		if (!key.getLocalName().equals(ANDROID_TARGET_CACHE)) {
			return false;
		}
		return true;
	}
}
