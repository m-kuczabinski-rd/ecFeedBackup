/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.android.AndroidBaseRunnerHelper;
import com.testify.ecfeed.generators.api.EcException;

public abstract class EclipseProjectSpecificClassImplementer extends EclipseClassImplementer {

	public EclipseProjectSpecificClassImplementer(
			IFileInfoProvider fileInfoProvider, 
			String testingAppPackage, 
			String testingAppClass) throws EcException {
		super(fileInfoProvider, 
				testingAppPackage + "." + AndroidBaseRunnerHelper.getEcFeedTestRunnerPrefix(), 
				testingAppClass);
	}
}
