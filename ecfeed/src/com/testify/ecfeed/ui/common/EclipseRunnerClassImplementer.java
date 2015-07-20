package com.testify.ecfeed.ui.common;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

public class EclipseRunnerClassImplementer extends EclipseSimpleClassImplementer {

	public EclipseRunnerClassImplementer(IFileInfoProvider fileInfoProvider, String testingAppPackage) {
		super(fileInfoProvider, testingAppPackage, "EcFeedTestRunner");
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
				"public class EcFeedTestRunner extends InstrumentationTestRunner {\n" +
				"\n" +
				"\t@Override" +
				"\tpublic void onCreate(Bundle arguments) {\n" + 
				"\tTestHelper.prepareTestArguments(arguments.getString(\"ecFeed\"))n" +
				"\tsuper.onCreate(arguments)" +
				"\t}\n" + 
				"}\n";
	}
}
