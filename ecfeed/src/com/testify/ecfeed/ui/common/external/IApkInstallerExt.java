/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;

import java.lang.reflect.InvocationTargetException;

public interface IApkInstallerExt {

	public final String INTERFACE_NAME = "INSTALLER";
	public final String INTERFACE_VERSION = "1.0";

	public void installApplicationsIfModified(IFileInfoProvider fileInfoProvider) throws InvocationTargetException;
}
