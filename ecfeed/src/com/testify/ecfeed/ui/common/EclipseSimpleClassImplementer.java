package com.testify.ecfeed.ui.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public abstract class EclipseSimpleClassImplementer {

	IFileInfoProvider fFileInfoProvider;
	String fTestingAppPackage;
	String fTestingAppClass;

	public EclipseSimpleClassImplementer(
			IFileInfoProvider fileInfoProvider, 
			String testingAppPackage, 
			String testingAppClass) {
		fFileInfoProvider = fileInfoProvider;
		fTestingAppPackage = testingAppPackage;
		fTestingAppClass = testingAppClass;
	}

	abstract protected void createUnitContent(ICompilationUnit unit) throws JavaModelException;

	public void implementContent() throws CoreException {
		String unitName = fTestingAppClass + ".java";

		IPackageFragment packageFragment = 
				EclipsePackageFragmentGetter.getPackageFragment(filePackage(), fFileInfoProvider);
		ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);

		createUnitContent(unit);

		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
	}

	public boolean classDefinitionImplemented() {
		return classDefinitionImplemented(filePackage(), fTestingAppClass);
	}

	private boolean classDefinitionImplemented(String packageName, String className) {

		String qualifiedName = packageName + "." + className;

		IType type = JavaModelAnalyser.getIType(qualifiedName);
		try {
			return  type != null && type.isClass();
		} catch (JavaModelException e) {
		}

		return false;
	}

	private String filePackage() {
		return fTestingAppPackage  + "ecFeed.android";
	}
}
