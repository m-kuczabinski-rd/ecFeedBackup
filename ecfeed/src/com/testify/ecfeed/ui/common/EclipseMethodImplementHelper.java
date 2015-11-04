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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.android.external.IMethodImplementHelper;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.utils.ExceptionHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseMethodImplementHelper implements IMethodImplementHelper {

	public static final String EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL = "File info provider must not be null.";
	public static final String EXCEPTION_TYPE_IS_NULL = "Can not implement method. Type is null.";

	final IFileInfoProvider fFileInfoProvider;
	final String fClassQuafiedName;
	final MethodNode fMethodNode;
	final IType fClassType;


	public EclipseMethodImplementHelper(
			final IFileInfoProvider fileInfoProvider, 
			final String classQuafiedName,
			final MethodNode methodNode) {
		if (fileInfoProvider == null) { 
			ExceptionHelper.reportRuntimeException(EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}

		fFileInfoProvider = fileInfoProvider;
		fClassQuafiedName = classQuafiedName;
		fMethodNode = methodNode;

		IType classType = null;
		try {
			classType = getJavaProject(fileInfoProvider).findType(classQuafiedName);
		} catch (CoreException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
		if(classType == null){
			ExceptionHelper.reportRuntimeException(EXCEPTION_TYPE_IS_NULL);
		}

		fClassType = classType;
	}

	@Override
	public void createMethod(final String methodContent) {
		try {
			fClassType.createMethod(methodContent, null, false, null);
		} catch (JavaModelException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}

	@Override
	public void createImport(final String type) {
		try {
			fClassType.getCompilationUnit().createImport(type, null, null);
		} catch (JavaModelException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}

	@Override
	public void commitChanges() {
		final ICompilationUnit unit = fClassType.getCompilationUnit();
		try {
			unit.becomeWorkingCopy(null);
			unit.commitWorkingCopy(true, null);
		} catch (JavaModelException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}

	public boolean methodDefinitionImplemented() {
		try{
			final IType type = getJavaProject(fFileInfoProvider).findType(fClassQuafiedName);
			if(type == null){
				return false;
			}
			final EclipseModelBuilder builder = new EclipseModelBuilder();
			for(IMethod method : type.getMethods()){

				final MethodNode model = builder.buildMethodModel(method);
				if (model != null 
						&& model.getName().equals(fMethodNode.getName()) 
						&& model.getParametersTypes().equals(fMethodNode.getParametersTypes())){
					return true;
				}
			}
		}catch(CoreException e) { 
			SystemLogger.logCatch(e.getMessage());
		}

		return false;
	}

	private IJavaProject getJavaProject(final IFileInfoProvider fileInfoProvider) throws CoreException{
		if(fileInfoProvider.getProject().hasNature(JavaCore.NATURE_ID)){
			return JavaCore.create(fileInfoProvider.getProject());
		}
		return null;
	}	
}
