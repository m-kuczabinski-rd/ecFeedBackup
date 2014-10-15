package com.testify.ecfeed.ui.common;

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

public class JavaModelAnalyser {

	public IType getVariableType(String signature){
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

	public IType getVariableType(IMethod method, ILocalVariable var){
		IType type = getLocalVariableType(method, var);
		if(type == null){
			type = getPackageVariableType(method, var);
		}
		if(type == null){
			type = getImportedVariableType(method, var);
		}
		return type;
	}

	public boolean isEnumType(IMethod method, ILocalVariable var) {
		IType type = getVariableType(method, var);
		try {
			return type != null && type.isEnum();
		} catch (JavaModelException e) {
			return false;
		}
	}

	public IType getIType(String qualifiedName) {
		try {
			for(IJavaProject project : JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects()){
				if(project.findType(qualifiedName) != null){
					return project.findType(qualifiedName);
				}
			}
		} catch (JavaModelException e) {}
		return null;
	}

	public boolean isAnnotated(ILocalVariable parameter, String annotationType){
		try{
			IAnnotation[] annotations = parameter.getAnnotations();
			for(IAnnotation annotation : annotations){
				if(annotation.getElementName().equals(annotationType)){
					return true;
				}
			}
		}catch(JavaModelException e){}
		return false;
	}

	public boolean isAnnotated(IMethod method, String name){
		try{
			IAnnotation[] annotations = method.getAnnotations();
			for(IAnnotation annotation : annotations){
				if(annotation.getElementName().equals(name)){
					return true;
				}
			}
		}catch(JavaModelException e){}
		return false;
	}

	public boolean isPublicVoid(IMethod method){
		return isPublic(method) && isVoid(method);
	}
	
	public boolean isPublic(IMethod method){
		try {
			return Flags.isPublic(method.getFlags());
		} catch (JavaModelException e) {}
		return false;
	}

	public boolean isVoid(IMethod method){
		try {
			return method.getReturnType().equals(Signature.SIG_VOID);
		} catch (JavaModelException e) {}
		return false;
	}
	
	public String getTypeName(IMethod method, ILocalVariable parameter){
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


	protected IType getLocalVariableType(IMethod method, ILocalVariable var){
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

	protected IType getPackageVariableType(IMethod method, ILocalVariable var){
		IType declaringType = method.getDeclaringType();
		String packageName = declaringType.getPackageFragment().getElementName();
		String variableTypeName = Signature.toString(var.getTypeSignature());
		String qualifiedName = packageName + "." + variableTypeName;
		IType type = getIType(qualifiedName);
		return type;
	}

	protected IType getImportedVariableType(IMethod method, ILocalVariable var) {
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
