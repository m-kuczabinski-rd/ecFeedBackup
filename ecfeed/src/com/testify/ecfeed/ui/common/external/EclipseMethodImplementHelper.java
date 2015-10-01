/*******************************************************************************
 * Copyright (c) 2015 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.utils.ExceptionHelper;

public class EclipseMethodImplementHelper implements IMethodImplementHelper {

	public static final String EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL = "File info provider must not be null.";
	public static final String EXCEPTION_TYPE_IS_NULL = "Can not implement method. Type is null.";

	final IType fClassType;

	public EclipseMethodImplementHelper(
			final IFileInfoProvider fileInfoProvider, 
			final String classQuafiedName
			) throws JavaModelException, CoreException {
		if (fileInfoProvider == null) { 
			ExceptionHelper.reportRuntimeException(EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}

		final IType classType = getJavaProject(fileInfoProvider).findType(classQuafiedName);
		if(classType == null){
			ExceptionHelper.reportRuntimeException(EXCEPTION_TYPE_IS_NULL);
		}

		fClassType = classType;
	}

	@Override
	public void createMethod(final String methodContent) throws JavaModelException {
		fClassType.createMethod(methodContent, null, false, null);
	}

	@Override
	public void createImport(final String type) throws JavaModelException {
		fClassType.getCompilationUnit().createImport(type, null, null);
	}

	@Override
	public void commitChanges() throws JavaModelException {
		final ICompilationUnit unit = fClassType.getCompilationUnit();
		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
	}

	private IJavaProject getJavaProject(final IFileInfoProvider fileInfoProvider) throws CoreException{
		if(fileInfoProvider.getProject().hasNature(JavaCore.NATURE_ID)){
			return JavaCore.create(fileInfoProvider.getProject());
		}
		return null;
	}	
}
