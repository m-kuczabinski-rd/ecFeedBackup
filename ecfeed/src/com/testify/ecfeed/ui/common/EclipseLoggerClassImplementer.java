package com.testify.ecfeed.ui.common;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

public class EclipseLoggerClassImplementer extends EclipseSimpleClassImplementer {

	public EclipseLoggerClassImplementer(IFileInfoProvider fileInfoProvider) {
		super(fileInfoProvider);
	}

	@ Override
	protected String getPackageName() {
		return "com.testify.ecfeed.android.junit";
	}

	@ Override
	protected String getClassName() {
		return "Logger";
	}	

	@ Override
	protected void createUnitContent(ICompilationUnit unit) throws JavaModelException {
		unit.createType(getClassContent(), null, false, null);
		unit.createImport("android.util.Log", null, null);
		unit.createImport("com.testify.ecfeed.android.junit.tools.ILogger", null, null);
	}

	private String getClassContent() {
		return
				"public class Logger implements ILogger {\n" +
				"\n" +
				"\tfinal String TAG = \"ecFeed\";\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic void log(String message) {\n" +
				"\t\tLog.d(TAG, message);\n" +
				"\t}\n" + 
				"}\n";
	}
}
