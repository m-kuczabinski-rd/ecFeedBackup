/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.ecfeed.android.external.IMethodImplementHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.IFileInfoProvider;

public class EclipseMethodImplementHelper implements IMethodImplementHelper {

	public static final String EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL = "File info provider must not be null.";
	public static final String EXCEPTION_TYPE_IS_NULL = "Can not implement method. Type is null.";
	public static final String EXCEPTION_CLASS_DOES_NOT_EXIST = "Class: %s does not exist.";


	final IFileInfoProvider fFileInfoProvider;
	final String fClassQualifiedName;
	final MethodNode fMethodNode;

	public EclipseMethodImplementHelper(
			final IFileInfoProvider fileInfoProvider, 
			final String classQualifiedName,
			final MethodNode methodNode) {
		if (fileInfoProvider == null) { 
			ExceptionHelper.reportRuntimeException(EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}

		fFileInfoProvider = fileInfoProvider;
		fClassQualifiedName = classQualifiedName;
		fMethodNode = methodNode;
	}

	@Override
	public void createMethod(final String methodContent) {
		IType classType = getClassTypeNotNull();
		try {
			classType.createMethod(methodContent, null, false, null);
		} catch (JavaModelException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}

	@Override
	public void createImport(final String type) {
		IType classType = getClassTypeNotNull();
		try {
			classType.getCompilationUnit().createImport(type, null, null);
		} catch (JavaModelException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}

	@Override
	public void commitChanges() {
		IType classType = getClassTypeNotNull();
		final ICompilationUnit unit = classType.getCompilationUnit();
		try {
			unit.becomeWorkingCopy(null);
			unit.commitWorkingCopy(true, null);
		} catch (JavaModelException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}

	IType getClassTypeNotNull() {
		IType classType = getClassType();
		if (classType == null) {
			ExceptionHelper.reportRuntimeException(
					String.format(EXCEPTION_CLASS_DOES_NOT_EXIST, fClassQualifiedName));
		}
		return classType;
	}

	IType getClassType() {
		IType classType = null;
		try {
			classType = getJavaProject(fFileInfoProvider).findType(fClassQualifiedName);
		} catch (CoreException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
		return classType;
	}

	public boolean methodDefinitionImplemented() {
		try{
			final IType type = getJavaProject(fFileInfoProvider).findType(fClassQualifiedName);
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
