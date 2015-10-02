/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.adapter.java.JavaPrimitiveTypePredicate;
import com.testify.ecfeed.external.IClassImplementHelper;
import com.testify.ecfeed.external.IProjectHelper;
import com.testify.ecfeed.external.ImplementerExt;
import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseImplementationStatusResolver extends AbstractJavaImplementationStatusResolver{

	IFileInfoProvider fFileInfoProvider;

	public EclipseImplementationStatusResolver(IFileInfoProvider fileInfoProvider){
		super(new JavaPrimitiveTypePredicate(), new EclipseProjectHelper(fileInfoProvider).isAndroidProject());
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	protected boolean androidCodeImplemented(ClassNode classNode) throws EcException {
		String baseRunner = classNode.getAndroidBaseRunner();

		IProjectHelper projectHelper = new EclipseProjectHelper(fFileInfoProvider);
		IClassImplementHelper classImplementHelper = new EclipseClassImplementHelper(fFileInfoProvider);

		ImplementerExt implementer = new ImplementerExt(baseRunner, projectHelper, classImplementHelper); 
		return implementer.contentImplemented();
	}

	@Override
	protected boolean classDefinitionImplemented(String qualifiedName) {
		IType type = JavaModelAnalyser.getIType(qualifiedName);
		try {
			return type != null && type.isClass();
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	@Override
	protected boolean methodDefinitionImplemented(MethodNode method) {
		return JavaModelAnalyser.getIMethod(method) != null;
	}

	@Override
	protected boolean enumDefinitionImplemented(String qualifiedName) {
		IType type = JavaModelAnalyser.getIType(qualifiedName);
		try {
			return  type != null && type.isEnum();
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	@Override
	protected boolean enumValueImplemented(String qualifiedName, String value) {
		IType type = JavaModelAnalyser.getIType(qualifiedName);
		try {
			if(type == null || type.isEnum() == false){
				return false;
			}
			for(IField field : type.getFields()){
				if(field.isEnumConstant() && field.getElementName().equals(value)){
					return true;
				}
			}
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return false;
	}
}
