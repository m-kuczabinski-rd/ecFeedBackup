package com.testify.ecfeed.modelif.java;

import java.util.List;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.modelif.java.common.Messages;

public class JavaClassUtils {

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
	
	public static boolean validateNewMethodSignature(ClassNode parent, String methodName, 
			List<String> argTypes, List<String> problems){
		boolean valid = JavaMethodUtils.validateMethodName(methodName, problems);
		if(parent.getMethod(methodName, argTypes) != null){
			valid = false;
			if(problems != null){
				problems.add(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
			}
		}
		return valid;
	}

	public static boolean validateNewMethodSignature(ClassNode parent, String methodName, List<String> argTypes){
		return validateNewMethodSignature(parent, methodName, argTypes, null);
	}

}
