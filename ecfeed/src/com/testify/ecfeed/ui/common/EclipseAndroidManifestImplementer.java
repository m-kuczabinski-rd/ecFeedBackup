/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.android.utils.AndroidBaseRunnerHelper;
import com.testify.ecfeed.android.utils.AndroidManifestAccessor;
import com.testify.ecfeed.ui.common.external.IFileInfoProvider;
import com.testify.ecfeed.ui.common.external.IImplementer;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;

public class EclipseAndroidManifestImplementer implements IImplementer{

	AndroidManifestAccessor fManifestAccessor;
	String fEcFeedTestRunner;

	public EclipseAndroidManifestImplementer(IFileInfoProvider fileInfoProvider) {
		fManifestAccessor =
				new AndroidManifestAccessor(EclipseProjectHelper.getProjectPath(fileInfoProvider));
		fEcFeedTestRunner = 
				fManifestAccessor.getTestingAppPackage() + 
				"." + AndroidBaseRunnerHelper.getEcFeedTestRunnerPrefix() + "." + 
				AndroidBaseRunnerHelper.getEcFeedTestRunnerName();
	}

	public boolean contentImplemented() {
		return fManifestAccessor.containsRunner(fEcFeedTestRunner);
	}

	public void implementContent() {
		fManifestAccessor.supplementRunner(
				fEcFeedTestRunner,
				fManifestAccessor.getDefaultTestedAppPackage());
	}
}
