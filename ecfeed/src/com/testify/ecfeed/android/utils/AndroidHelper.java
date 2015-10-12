package com.testify.ecfeed.android.utils;

public class AndroidHelper {

	public static String getEcFeedAndroidPackagePrefix() {
		return "ecfeed.android";
	}

	public static String getAndroidToolsPackage(String testingAppPackage) {
		return testingAppPackage + "." + AndroidHelper.getEcFeedAndroidPackagePrefix() + ".tools";
	}	
}
