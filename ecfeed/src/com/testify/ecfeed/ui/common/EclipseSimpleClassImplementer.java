package com.testify.ecfeed.ui.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public abstract class EclipseSimpleClassImplementer {

	IFileInfoProvider fFileInfoProvider;

	public EclipseSimpleClassImplementer(IFileInfoProvider fileInfoProvider) {
		fFileInfoProvider = fileInfoProvider;	
	}

	abstract protected String getPackageName();
	abstract protected String getClassName();
	abstract protected void createUnitContent(ICompilationUnit unit) throws JavaModelException;

	public void implementContent() throws CoreException {
		String unitName = getClassName() + ".java";

		IPackageFragment packageFragment = 
				EclipsePackageFragmentGetter.getPackageFragment(getPackageName(), fFileInfoProvider);
		ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);

		createUnitContent(unit);

		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
	}

	public boolean classDefinitionImplemented() {
		return classDefinitionImplemented(getPackageName(), getClassName());
	}

	private boolean classDefinitionImplemented(String packageName, String className) {

		String qualifiedName = packageName + "." + className;
		IType type = JavaModelAnalyser.getIType(qualifiedName);
		try {
			return  type != null && type.isClass();
		} catch (JavaModelException e) {}
		return false;
	}
}
