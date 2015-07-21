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

import com.testify.ecfeed.utils.PackageClassHelper;

public class EclipseRunnerClassImplementer extends EclipseSimpleClassImplementer {

	String fBaseRunner;

	public EclipseRunnerClassImplementer(
			IFileInfoProvider fileInfoProvider, 
			String testingAppPackage,
			String baseRunnerPackage,
			String baseRunnerClass) {

		super(fileInfoProvider, testingAppPackage, "EcFeedTestRunner");
		fBaseRunner = PackageClassHelper.createQualifiedName(baseRunnerPackage, baseRunnerClass); 
	}

	@ Override
	protected void createUnitContent(ICompilationUnit unit) throws JavaModelException {

		unit.createType(getClassContent(), null, false, null);
		unit.createImport("android.os.Bundle", null, null);
		unit.createImport("android.test.InstrumentationTestRunner", null, null);
		unit.createImport("com.testify.ecfeed.android.junit.tools.TestHelper", null, null);
	}

	private String getClassContent() {
		return
				"public class EcFeedTestRunner extends " + fBaseRunner + " {\n" + 
				"\n" +
				"\t@Override" +
				"\tpublic void onCreate(Bundle arguments) {\n" + 
				"\tTestHelper.prepareTestArguments(arguments.getString(\"ecFeed\"))n" +
				"\tsuper.onCreate(arguments)" +
				"\t}\n" + 
				"}\n";
	}

	@Override
	public boolean contentImplemented() {
		return classImplemented(fBaseRunner);
	}
}
