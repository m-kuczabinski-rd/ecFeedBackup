/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.utils.PackageClassHelper;

public abstract class EclipseSimpleClassImplementer {

	private IFileInfoProvider fFileInfoProvider;
	private String fTestingAppPackage;
	private String fTestingAppSourceFilesPackage;
	private String fTestingAppClass;

	public EclipseSimpleClassImplementer(
			IFileInfoProvider fileInfoProvider, 
			String testingAppPackage, 
			String testingAppClass) {
		fFileInfoProvider = fileInfoProvider;
		fTestingAppPackage = testingAppPackage;
		fTestingAppSourceFilesPackage = fTestingAppPackage  + ".ecFeed.android";
		fTestingAppClass = testingAppClass;
	}

	abstract protected void createUnitContent(ICompilationUnit unit) throws JavaModelException;

	public void implementContent() throws CoreException {
		String unitName = fTestingAppClass + ".java";

		IPackageFragment packageFragment = 
				EclipsePackageFragmentGetter.getPackageFragment(
						fTestingAppSourceFilesPackage, fFileInfoProvider);

		ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);
		createUnitContent(unit);
		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
	}

	public boolean contentImplemented() {
		return classImplemented(null);
	}

	protected boolean classImplemented(String superclassName) {

		IType type = getTestingClassType();

		if (type == null) {
			return false;
		}

		if (!isClass(type)) {
			return false;
		}

		if (superclassName != null) {
			String implementedSuperClass = getSuperclassName(type); 
			if(superclassName != implementedSuperClass) {
				return false;
			}
		}

		return true;
	}

	private IType getTestingClassType() {

		String classType = 
				PackageClassHelper.createQualifiedName(fTestingAppSourceFilesPackage, fTestingAppClass); 

		return JavaModelAnalyser.getIType(classType);
	}	

	private boolean isClass(IType type) {

		try {
			if (type.isClass()) {
				return true;
			}
		} catch (JavaModelException e) {
			return false;
		}

		return true;
	}

	private String getSuperclassName(IType type) {

		try {
			return type.getSuperclassName();
		} catch (JavaModelException e) {
			return null;
		}
	}
}
