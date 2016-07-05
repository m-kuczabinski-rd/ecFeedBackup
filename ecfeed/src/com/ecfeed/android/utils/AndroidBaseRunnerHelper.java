/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.android.utils;

import com.ecfeed.core.utils.EcException;

public class AndroidBaseRunnerHelper {

	public static String getDefaultAndroidBaseRunnerName() {
		return "android.test.InstrumentationTestRunner";
	}

	public static String getEcFeedTestRunnerName() {
		return "EcFeedTestRunner";
	}

	public static String createFullAndroidRunnerName(String projectPath) throws EcException {
		String testingAppPackage = getTestingAppPackage(projectPath);

		if (testingAppPackage == null) {
			return null;
		}

		return testingAppPackage + "/" + qualifiedRunnerName(testingAppPackage); 
	}

	public static String createAndroidBaseRunnerName(String projectPath) throws EcException {
		String testingAppPackage = getTestingAppPackage(projectPath);

		if (testingAppPackage == null) {
			return null;
		}

		return qualifiedRunnerName(testingAppPackage);
	}

	private static String getTestingAppPackage(String projectPath) throws EcException {
		String testingAppPackage = new AndroidManifestAccessor(projectPath).getTestingAppPackage();

		if (testingAppPackage == null || testingAppPackage.isEmpty()) {
			return null;
		}

		return testingAppPackage;
	}

	private static String qualifiedRunnerName(String testingAppPackage) {
		return testingAppPackage + "." + 
				AndroidHelper.getEcFeedAndroidPackagePrefix() + "." + 
				getEcFeedTestRunnerName();
	}
}
