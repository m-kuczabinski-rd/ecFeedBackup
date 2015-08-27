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

import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.utils.PackageClassHelper;
import com.testify.ecfeed.utils.SystemLogger;

public abstract class EclipseClassImplementer implements IImplementer {

	private IFileInfoProvider fFileInfoProvider;
	private String fPackage;
	private String fClassNameWithoutExtension;

	public EclipseClassImplementer(
			IFileInfoProvider fileInfoProvider, 
			String argPackage, 
			String classNameWithoutExtension) throws EcException {
		if (fileInfoProvider == null) {
			EcException.report(Messages.EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}
		fFileInfoProvider = fileInfoProvider;
		fPackage = argPackage; 
		fClassNameWithoutExtension = classNameWithoutExtension;
	}

	abstract protected void createUnitContent(ICompilationUnit unit) throws JavaModelException;

	public void implementContent() throws EcException {
		try {
			String unitName = fClassNameWithoutExtension + ".java";

			IPackageFragment packageFragment = 
					EclipsePackageFragmentGetter.getPackageFragment(
							fPackage, fFileInfoProvider);

			ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);
			createUnitContent(unit);
			unit.becomeWorkingCopy(null);
			unit.commitWorkingCopy(true, null);
		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
			EcException.report(e.getMessage());
		}
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

			if(!superclassName.endsWith(implementedSuperClass)) {
				return false;
			}
		}
		return true;
	}

	private IType getTestingClassType() {

		String classType = 
				PackageClassHelper.createQualifiedName(fPackage, fClassNameWithoutExtension); 

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
