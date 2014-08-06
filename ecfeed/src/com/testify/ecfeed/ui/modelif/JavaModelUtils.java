package com.testify.ecfeed.ui.modelif;

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

import com.testify.ecfeed.modelif.java.JavaClassUtils;

public class JavaModelUtils {

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
		} catch (JavaModelException e) {}
		return null;
	}

	public static boolean isExpected(ILocalVariable parameter){
		try{
			IAnnotation[] annotations = parameter.getAnnotations();
			for(IAnnotation annotation : annotations){
				if(annotation.getElementName().equals("expected")){
					return true;
				}
			}
		}catch(JavaModelException e){}
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
		}catch(JavaModelException e){}
		return false;
	}

	public static boolean isPublicVoid(IMethod method){
		try{
		return (method.getReturnType().equals(Signature.SIG_VOID) && Flags.isPublic(method.getFlags()));
		}catch(JavaModelException e){}
		return false;
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
				if(type != null && JavaClassUtils.getLocalName(type.getFullyQualifiedName()).equals(variableTypeName)){
					return type;
				}
			}
		} catch (JavaModelException e1) {
		}
		return null;
	}

}
