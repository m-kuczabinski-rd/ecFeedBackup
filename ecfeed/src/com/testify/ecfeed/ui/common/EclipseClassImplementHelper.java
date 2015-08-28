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

public class EclipseClassImplementHelper {

	public static boolean classImplemented(String thePackage, String classNameWithoutExtension, String superclassName) {
		IType type = getTestingClassType(thePackage, classNameWithoutExtension);

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

	public static boolean classImplemented(String thePackage, String classNameWithoutExtension) {
		return classImplemented(thePackage, classNameWithoutExtension, null);
	}

	private static IType getTestingClassType(String thePackage, String classNameWithoutExtension) {
		String classType = 
				PackageClassHelper.createQualifiedName(thePackage, classNameWithoutExtension); 

		return JavaModelAnalyser.getIType(classType);
	}	

	private static boolean isClass(IType type) {
		try {
			if (type.isClass()) {
				return true;
			}
		} catch (JavaModelException e) {
			return false;
		}

		return true;
	}

	private static String getSuperclassName(IType type) {
		try {
			return type.getSuperclassName();
		} catch (JavaModelException e) {
			return null;
		}
	}

	public static void implementClass(
			String thePackage, 
			String classNameWithoutExtension, 
			String contents,
			IFileInfoProvider fileInfoProvider) throws EcException {
		try {
			String unitName = classNameWithoutExtension + ".java";

			IPackageFragment packageFragment = 
					EclipsePackageFragmentGetter.getPackageFragment(
							thePackage, fileInfoProvider);

			ICompilationUnit unit = 
					packageFragment.createCompilationUnit(unitName, contents, false, null);

			unit.becomeWorkingCopy(null);
			unit.commitWorkingCopy(true, null);
		} catch (CoreException e) {
			EcException.report(e.getMessage());
		}
	}
}
