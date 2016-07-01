/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.android.external;


public class DeviceCheckerExt {

	public static void checkIfOneDeviceAttached() {
		final IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();
		final IDeviceCheckerExt deviceChecker = androidFactory.createDeviceChecker();
		deviceChecker.checkIfOneDeviceAttached();
	}
}
