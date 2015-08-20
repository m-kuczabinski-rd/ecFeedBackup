/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;


public class EclipseProjectHelper {

	public static String getProjectPath(IFileInfoProvider fileInfoProvider) {

		if (fileInfoProvider == null) {
			throw new RuntimeException(Messages.EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}

		return fileInfoProvider.getProject().getLocation().toOSString();
	}
}
