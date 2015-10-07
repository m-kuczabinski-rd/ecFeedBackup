/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

public class StringHelper {

	public static boolean isNullOrEmpty(String str) {
		if (str == null) {
			return true;
		}
		if (str.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isTrimmedEmpty(String str) {
		return str.trim().isEmpty();
	}

	public static String removePrefix(String prefix, String fromStr) {
		int index = fromStr.indexOf(prefix);

		if (index == -1) {
			return fromStr;
		}
		return fromStr.substring(index + prefix.length());
	}

	public static String removePostfix(String postfix, String fromStr) {
		int index = fromStr.lastIndexOf(postfix);

		if (index == -1) {
			return fromStr;
		}
		return fromStr.substring(0, index);
	}

	public static String getLastToken(String tokenizedString, String tokenSeparator) {
		int separatorPosition = tokenizedString.lastIndexOf(tokenSeparator);

		if (separatorPosition == -1) {
			return null;
		}
		return tokenizedString.substring(separatorPosition+1);
	}

	public static String getAllBeforeLastToken(String packageWithClass, String tokenSeparator) {
		int separatorPosition = packageWithClass.lastIndexOf(tokenSeparator);

		if (separatorPosition == -1) {
			return null;
		}
		return packageWithClass.substring(0, separatorPosition);
	}

}
