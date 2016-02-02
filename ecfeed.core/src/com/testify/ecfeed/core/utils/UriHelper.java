/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.core.utils;

import java.net.URI;

public class UriHelper {

	public static String convertUriToFilePath(URI uri) {
		String pathWithFileName = uri.toString();
		return StringHelper.removePrefix("file:", pathWithFileName);	
	}

}
