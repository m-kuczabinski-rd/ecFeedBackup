/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

public class PackageClassHelper {
	public static String getPackage(String packageWithClass) {

		int separatorPosition = packageWithClass.lastIndexOf(".");
		return packageWithClass.substring(0, separatorPosition);

	}
	public static String getClass(String packageWithClass) {

		int separatorPosition = packageWithClass.lastIndexOf(".");
		return packageWithClass.substring(separatorPosition+1);
	}	
}
