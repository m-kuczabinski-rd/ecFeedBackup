/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

public class PackageClassHelper {

	public static final String PACKAGE_CLASS_SEPARATOR = ".";

	public static String getPackage(String packageWithClass) {
		return StringHelper.getAllBeforeLastToken(packageWithClass, PACKAGE_CLASS_SEPARATOR);		
	}

	public static String getClass(String packageWithClass) {
		return StringHelper.getLastToken(packageWithClass, PACKAGE_CLASS_SEPARATOR);
	}

	public static String createPackageWithClass(String thePackage, String className) {
		return thePackage + PACKAGE_CLASS_SEPARATOR + className;
	}
}
