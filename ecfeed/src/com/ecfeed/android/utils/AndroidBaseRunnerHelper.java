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
