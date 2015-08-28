/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.generators.api.EcException;

public class EclipseTestClassImplementer extends EclipseProjectSpecificClassImplementer {

	String fTestingAppSuperClass;
	String fTestedAppPackage;
	String fTestedAppMainActivity;

	public EclipseTestClassImplementer(
			IFileInfoProvider fileInfoProvider, 
			String testingAppPackage,
			String testingAppClass,
			String testingAppSuperClass,
			String testedAppPackage,
			String testedAppMainActivity) throws EcException {
		super(fileInfoProvider, testingAppPackage, testingAppClass);
		fTestingAppSuperClass = testingAppSuperClass;
		fTestedAppPackage = testedAppPackage;
		fTestedAppMainActivity = testedAppMainActivity;
	}

	@ Override
	protected String createUnitContent() {
		return
				"package com.mamlambo.article.simplecalc.test.ecFeed.android;\n" + 
				"\n" +
				"import android.test.ActivityInstrumentationTestCase2;\n" +
				"import com.testify.ecfeed.android.junit.tools.TestHelper;\n" +
				"import com.mamlambo.article.simplecalc.MainActivity;\n" +		
				"\n" +
				"public class EcFeedTest extends " + superClassName() + " {\n" +
				"\n" +
				"\tpublic EcFeedTest() {\n" + 
				"\t\tsuper(" + fTestedAppMainActivity + ".class);\n" +
				"\t}\n" + 
				"\n" +
				"\tpublic void ecFeedTest() {\n" + 
				"\t\tTestHelper.invokeTestMethod(this, new Logger());\n" +
				"\t}\n" +
				"}\n";
	}

	private String superClassName() {

		if (fTestedAppMainActivity == null) {
			return fTestingAppSuperClass;
		}

		return fTestingAppSuperClass + "<" + fTestedAppMainActivity + ">" ;
	}
}
