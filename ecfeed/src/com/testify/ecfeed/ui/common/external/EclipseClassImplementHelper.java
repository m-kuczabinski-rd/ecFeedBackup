/*******************************************************************************
 * Copyright (c) 2015 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.common.external.IClassImplementHelper;
import com.testify.ecfeed.ui.common.utils.EclipsePackageFragmentGetter;
import com.testify.ecfeed.utils.ExceptionHelper;
import com.testify.ecfeed.utils.PackageClassHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseClassImplementHelper implements IClassImplementHelper {

	public static final String EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL = "File info provider must not be null.";

	final IFileInfoProvider fFileInfoProvider;

	public EclipseClassImplementHelper(IFileInfoProvider fileInfoProvider) {
		if (fileInfoProvider == null) { 
			ExceptionHelper.reportRuntimeException(EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	public boolean classImplemented(
			final String thePackage, final String classNameWithoutExtension, final String superclassName) {
		final IType type = getTestingClassType(thePackage, classNameWithoutExtension);

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

	@Override
	public boolean classImplemented(final String thePackage, final String classNameWithoutExtension) {
		return classImplemented(thePackage, classNameWithoutExtension, null);
	}

	private IType getTestingClassType(String thePackage, String classNameWithoutExtension) {
		final String classType = 
				PackageClassHelper.createPackageWithClass(thePackage, classNameWithoutExtension); 

		return getIType(classType);
	}	

	private IType getIType(final String qualifiedName) {
		try {
			for(IJavaProject project : JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects()){
				if(project.findType(qualifiedName) != null){
					return project.findType(qualifiedName);
				}
			}
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return null;
	}

	private boolean isClass(final IType type) {
		try {
			if (type.isClass()) {
				return true;
			}
		} catch (JavaModelException e) {
			return false;
		}

		return true;
	}

	private String getSuperclassName(final IType type) {
		try {
			return type.getSuperclassName();
		} catch (JavaModelException e) {
			return null;
		}
	}

	@Override
	public void implementClass(
			final String thePackage, 
			final String classNameWithoutExtension, 
			final String content) {
		try {
			final IPackageFragment packageFragment = 
					EclipsePackageFragmentGetter.getPackageFragment(
							thePackage, fFileInfoProvider);

			final String unitName = classNameWithoutExtension + ".java";

			final ICompilationUnit unit = 
					packageFragment.createCompilationUnit(unitName, content, false, null);

			unit.becomeWorkingCopy(null);
			unit.commitWorkingCopy(true, null);
		} catch (CoreException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}
}
