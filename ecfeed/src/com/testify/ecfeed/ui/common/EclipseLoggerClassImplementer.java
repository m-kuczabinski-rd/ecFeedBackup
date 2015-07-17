package com.testify.ecfeed.ui.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class EclipseLoggerClassImplementer {

	IFileInfoProvider fFileInfoProvider;

	public EclipseLoggerClassImplementer(IFileInfoProvider fileInfoProvider) {
		fFileInfoProvider = fileInfoProvider;	
	}

	public void implementContent() throws CoreException {
		implementClassDefinition();
	}

	private void implementClassDefinition() throws CoreException {
		String packageName = "com.testify.ecfeed.android.junit";
		String className = "Logger";
		String unitName = className + ".java";

		IPackageFragment packageFragment = 
				EclipsePackageFragmentGetter.getPackageFragment(packageName, fFileInfoProvider);
		ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);

		unit.createType(createClassContent(), null, false, null);
		unit.createImport("android.util.Log", null, null);
		unit.createImport("com.testify.ecfeed.android.junit.tools.ILogger", null, null);

		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
	}

	private String createClassContent() {
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

	public boolean classDefinitionImplemented() {
		return classDefinitionImplemented("com.testify.ecfeed.android.junit.Logger");
	}

	private boolean classDefinitionImplemented(String qualifiedName) {
		IType type = JavaModelAnalyser.getIType(qualifiedName);
		try {
			return  type != null && type.isClass();
		} catch (JavaModelException e) {}
		return false;
	}
}
