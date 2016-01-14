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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.testify.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.model.MethodNode;

public class JavaModelAnalyser {

	public static IType getVariableType(String signature){
		for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
			IJavaProject javaProject = JavaCore.create(project);
			String qualifiedName = Signature.toString(signature);
			try {
				return javaProject.findType(qualifiedName);
			} catch (JavaModelException e) {
			}
		}
		return null;
	}

	public static IType getVariableType(IMethod method, ILocalVariable var){
		IType type = getLocalVariableType(method, var);
		if(type == null){
			type = getPackageVariableType(method, var);
		}
		if(type == null){
			type = getImportedVariableType(method, var);
		}
		return type;
	}

	public static boolean isEnumType(IMethod method, ILocalVariable var) {
		IType type = getVariableType(method, var);
		try {
			return type != null && type.isEnum();
		} catch (JavaModelException e) {
			return false;
		}
	}

	public static IType getIType(String qualifiedName) {
		try {
			for(IJavaProject project : JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects()){
				if(project.findType(qualifiedName) != null){
					return project.findType(qualifiedName);
				}
			}
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return null;
	}

	public static IMethod getIMethod(MethodNode method) {
		IType parentType = getIType(method.getClassNode().getName());

		try {
			for(IMethod methodDef : parentType.getMethods()){
				if(methodDef.getElementName().equals(method.getName()) == false ||
						methodDef.getReturnType().equals(Signature.SIG_VOID) == false){
					continue;
				}
				List<String> parameterTypes = new ArrayList<>();
				for(ILocalVariable parameter : methodDef.getParameters()){
					parameterTypes.add(getTypeName(methodDef, parameter));
				}
				if(parameterTypes.equals(method.getParametersTypes())){
					return methodDef;
				}
			}
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return null;
	}

	public static boolean isAnnotated(ILocalVariable parameter, String annotationType){
		try{
			IAnnotation[] annotations = parameter.getAnnotations();
			for(IAnnotation annotation : annotations){
				if(annotation.getElementName().equals(annotationType)){
					return true;
				}
			}
		}catch(JavaModelException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	public static boolean isAnnotated(IMethod method, String name){
		try{
			IAnnotation[] annotations = method.getAnnotations();
			for(IAnnotation annotation : annotations){
				if(annotation.getElementName().equals(name)){
					return true;
				}
			}
		}catch(JavaModelException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	public static boolean isPublicVoid(IMethod method){
		return isPublic(method) && isVoid(method);
	}

	public static boolean isPublic(IMethod method){
		try {
			return Flags.isPublic(method.getFlags());
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	public static boolean isVoid(IMethod method){
		try {
			return
					! method.getElementName().equals(method.getParent().getElementName())
					&& method.getReturnType().equals(Signature.SIG_VOID);
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	public static boolean hasParameterlessConstructor(IType type){
		try{
			for(IMethod constructor : getConstructors(type)){
				if(constructor.getParameters().length == 0){
					return true;
				}
			}
		}catch(JavaModelException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	public static boolean hasConstructor(IType type){
		return getConstructors(type).size() > 0;
	}

	public static List<IMethod> getConstructors(IType type){
		List<IMethod> result = new ArrayList<IMethod>();
		try{
		for(IMethod method : type.getMethods()){
			if(method.isConstructor()){
				result.add(method);
			}
		}
		}catch(JavaModelException e){SystemLogger.logCatch(e.getMessage());}
		return result;
	}

	public static String getTypeName(IMethod method, ILocalVariable parameter){
		String typeSignaure = parameter.getTypeSignature();
		switch(typeSignaure){
		case Signature.SIG_BOOLEAN:
			return com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_BOOLEAN;
		case Signature.SIG_BYTE:
			return com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_BYTE;
		case Signature.SIG_CHAR:
			return com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_CHAR;
		case Signature.SIG_DOUBLE:
			return com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_DOUBLE;
		case Signature.SIG_FLOAT:
			return com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_FLOAT;
		case Signature.SIG_INT:
			return com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_INT;
		case Signature.SIG_LONG:
			return com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_LONG;
		case Signature.SIG_SHORT:
			return com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_SHORT;
		case "QString;":
			return com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_STRING;
		default:
			return getVariableType(method, parameter).getFullyQualifiedName().replaceAll("\\$",	"\\.");
		}
	}


	protected static IType getLocalVariableType(IMethod method, ILocalVariable var){
		try {
			IType declaringType = method.getDeclaringType();
			String variableTypeName = Signature.toString(var.getTypeSignature());
			for(IType type : declaringType.getTypes()){
				if(type.getElementName().equals(variableTypeName)){
					return type;
				}
			}
		} catch (JavaModelException e) {
		}
		return null;
	}

	protected static IType getPackageVariableType(IMethod method, ILocalVariable var){
		IType declaringType = method.getDeclaringType();
		String packageName = declaringType.getPackageFragment().getElementName();
		String variableTypeName = Signature.toString(var.getTypeSignature());
		String qualifiedName = packageName + "." + variableTypeName;
		IType type = getIType(qualifiedName);
		return type;
	}

	protected static IType getImportedVariableType(IMethod method, ILocalVariable var) {
		String variableTypeName = Signature.toString(var.getTypeSignature());
		try {
			for(IImportDeclaration importDeclaration : method.getDeclaringType().getCompilationUnit().getImports()){
				String qualifiedName;
				if(importDeclaration.isOnDemand() == false){
					qualifiedName = importDeclaration.getElementName();
				}
				else{
					qualifiedName = importDeclaration.getElementName().replaceFirst("\\*", variableTypeName);
				}
				IType type = getIType(qualifiedName);
				if(type != null && type.getElementName().equals(variableTypeName)){
					return type;
				}
			}
		} catch (JavaModelException e1) {
		}
		return null;
	}

}
