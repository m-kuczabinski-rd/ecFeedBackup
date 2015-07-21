/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

public class EclipseTestClassImplementer extends EclipseSimpleClassImplementer {

	String fTestingAppSuperClass;
	String fTestedAppPackage;
	String fTestedAppMainActivity;

	public EclipseTestClassImplementer(
			IFileInfoProvider fileInfoProvider, 
			String testingAppPackage,
			String testingAppClass,
			String testingAppSuperClass,
			String testedAppPackage,
			String testedAppMainActivity) {
		super(fileInfoProvider, testingAppPackage, testingAppClass);
		fTestingAppSuperClass = testingAppSuperClass;
		fTestedAppPackage = testedAppPackage;
		fTestedAppMainActivity = testedAppMainActivity;
	}

	@ Override
	protected void createUnitContent(ICompilationUnit unit) throws JavaModelException {
		unit.createType(getClassContent(), null, false, null);

		unit.createImport("android.test." + fTestingAppSuperClass, null, null);
		unit.createImport("com.testify.ecfeed.android.junit.tools.TestHelper", null, null);
		unit.createImport(fTestedAppPackage + "." + fTestedAppMainActivity, null, null);
	}

	private String getClassContent() {
		return
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
