/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.utils;

import com.testify.ecfeed.android.utils.AndroidBaseRunnerHelper;
import com.testify.ecfeed.android.utils.AndroidManifestAccessor;
import com.testify.ecfeed.ui.common.external.IClassImplementHelper;

public class AndroidTestingClassImplementer extends JavaUserClassImplementer {

	private String fTestingAppPackage;

	public AndroidTestingClassImplementer(
			String projectPath,
			String thePackage,
			String classNameWithoutExtension,
			IClassImplementHelper classImplementHelper) {
		super(projectPath, thePackage, classNameWithoutExtension, classImplementHelper);

		AndroidManifestAccessor androidManifestAccesor =
				new AndroidManifestAccessor(projectPath);

		fTestingAppPackage = androidManifestAccesor.getTestingAppPackage();
	}

	@Override
	protected String createImportLine() {
		return 
				"import " + fTestingAppPackage + "." 
				+ AndroidBaseRunnerHelper.getEcFeedAndroidPackagePrefix() + 
				".EcFeedTest;\n\n";
	}

	@Override
	protected String createExtendsClause() {
		return "extends EcFeedTest ";
	}

	@Override
	protected String createSetupMethod() {
		return "\t@Override\n\tprotected void setUp() throws Exception {\n\t\tsuper.setUp();\n\t}\n";
	}
}
