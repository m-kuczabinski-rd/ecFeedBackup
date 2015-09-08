/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.android.AndroidBaseRunnerHelper;
import com.testify.ecfeed.ui.common.external.IFileInfoProvider;

public class EclipseRunnerClassImplementer extends EclipseProjectSpecificClassImplementer {

	String fBaseRunner;

	public EclipseRunnerClassImplementer(
			String testingAppPackage,
			String baseRunner,
			IFileInfoProvider fileInfoProvider) {
		super(testingAppPackage, AndroidBaseRunnerHelper.getEcFeedTestRunnerName(), fileInfoProvider);
		fBaseRunner = baseRunner;
	}

	@ Override
	protected String createUnitContent() {
		return
				"package com.mamlambo.article.simplecalc.test.ecFeed.android;\n" +
				"\n" +
				"import android.os.Bundle;\n" + 
				"import com.testify.ecfeed.android.junit.tools.TestHelper;\n" + 
				"\n" +
				"public class "+ AndroidBaseRunnerHelper.getEcFeedTestRunnerName() + " extends " + fBaseRunner + " {\n" + 
				"\n" +
				"\t@Override\n" +
				"\tpublic void onCreate(Bundle arguments) {\n" + 
				"\tTestHelper.prepareTestArguments(arguments.getString(\"ecFeed\"));\n" +
				"\tsuper.onCreate(arguments);\n" +
				"\t}\n" + 
				"}\n";
	}

	@Override
	public boolean contentImplemented() {
		return classImplemented(fBaseRunner);
	}
}
