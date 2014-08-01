package com.testify.ecfeed.modelif.java.classx;

import com.testify.ecfeed.model.ClassNode;

public class JavaClassUtils {
	public static String getLocalName(ClassNode classNode){
		return getLocalName(classNode.getName());
	}

	public static String getLocalName(String qualifiedName){
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)?qualifiedName: qualifiedName.substring(lastDotIndex + 1);
	}

	public static String getQualifiedName(ClassNode classNode){
		return classNode.getName();
	}

	public static String getPackageName(ClassNode classNode){
		return getPackageName(classNode.getName());
	}
	
	public static String getQualifiedName(String packageName, String localName){
		return packageName + "." + localName;
	}

	public static String getPackageName(String qualifiedName){
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)? "" : qualifiedName.substring(0, lastDotIndex);
	}
}
