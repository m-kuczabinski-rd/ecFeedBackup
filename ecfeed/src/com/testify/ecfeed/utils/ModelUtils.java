package com.testify.ecfeed.utils;

/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ExpectedCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedCategoryNode;
import com.testify.ecfeed.model.TestCaseNode;

public class ModelUtils {
	
	public static ClassNode generateClassModel(IType type){ 
		ClassNode classNode = new ClassNode(type.getFullyQualifiedName());
		try{
			Class<?> testClass = ClassUtils.getClassLoader(true, null).loadClass(type.getFullyQualifiedName());
			for(IMethod method : type.getMethods()){
				IAnnotation[] annotations = method.getAnnotations();
				if(method.getParameters().length > 0){
					for(IAnnotation annotation : annotations){
						if(annotation.getElementName().equals("Test")){
							MethodNode methodModel = generateMethodModel(method, testClass);
							if(methodModel != null){
								classNode.addMethod(methodModel);
							}
							break;
						}
					}
				}
			}
		}
		catch(Throwable e){
			System.out.println("Unexpected error");
		}
		return classNode;
	}

	public static boolean isClassModelUpToDate(ClassNode classNode) throws JavaModelException{
		return(getObsoleteMethods(classNode, classNode.getQualifiedName()).size() == 0 && 
			   getNotContainedMethods(classNode, classNode.getQualifiedName()).size() == 0);
	}
	
	//TODO Unit tests
	/**
	 * Returns list of MethodNode elements in the provided class model that are not existing in the
	 * type with qualifiedTypeName;
	 * @throws JavaModelException 
	 */
	public static List<MethodNode> getObsoleteMethods(ClassNode classNode, String qualifiedTypeName){
		List<MethodNode> empty = new ArrayList<MethodNode>();
		if(classNode == null){
			return empty;
		}
		IType type;
		try {
			type = getTypeObject(qualifiedTypeName);
		} catch (JavaModelException e) {
			return classNode.getMethods();
		}
		if(type == null){
			return classNode.getMethods();
		}

		return diff(classNode.getMethods(), generateClassModel(type).getMethods());
	}

	/**
	 * Returns list generated models of test method that are not contained in the provided class model;
	 * @throws JavaModelException 
	 */
	public static List<MethodNode> getNotContainedMethods(ClassNode classNode, String qualifiedTypeName){
		ArrayList<MethodNode> empty = new ArrayList<MethodNode>();
		IType type;
		
		//if we cannot generate model from type, return empty vector, i.e. no new methods are in the second class
		try {
			type = getTypeObject(qualifiedTypeName);
		} catch (JavaModelException e) {
			return empty;
		}
		if(type == null){
			return empty;
		}

		ClassNode model = generateClassModel(type);
		if(classNode == null){
			return model.getMethods();
		}
		
		return diff(model.getMethods(), classNode.getMethods());
	}
	
	/**
	 * Returns method not contained in the parent class node that have the same set of parameter types
	 * @param method MethodNode for which the compatible sibling methods are searched for
	 * @return
	 */
	public static List<MethodNode> getCompatibleMethods(MethodNode method) {
		ClassNode parent = (ClassNode)method.getParent();
		List<MethodNode> potentialMatches = getNotContainedMethods(parent, parent.getQualifiedName());
		List<MethodNode> matches = new ArrayList<MethodNode>();
		for(MethodNode potentialMatch : potentialMatches){
			if (potentialMatch.getCategoriesTypes().equals(method.getCategoriesTypes())){
				matches.add(potentialMatch);
			}
		}
		return matches;
	}

	/**
	 * Returns elements in v1 that are not mentioned in v2 by checking toString() value; 
	 */
	private static List<MethodNode> diff(List<MethodNode> v1, List<MethodNode> v2){
		ArrayList<MethodNode> diff = new ArrayList<MethodNode>();
		
		for(MethodNode method1 : v1){
			boolean nodeMentioned = false;
			for(MethodNode method2 : v2){
				if(method1.toString().equals(method2.toString())){
					nodeMentioned = true;
					break;
				}
			}
			if(nodeMentioned == false){
				diff.add(method1);
			}
		}				
		return diff;
	}
	
	private static IType getTypeObject(String qualifiedName) throws JavaModelException{
		IJavaProject[] projects = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
		for(IJavaProject project : projects){
			IType type = project.findType(qualifiedName);
			if(type != null){
				return type; 
			}
		}
		return null;
	}
	
	private static MethodNode generateMethodModel(IMethod method, Class<?> testClass) throws JavaModelException {
		MethodNode methodNode = new MethodNode(method.getElementName());
		for(ILocalVariable parameter : method.getParameters()){
			if(isExpected(parameter)){
				methodNode.addCategory(generateExpectedCategoryModel(parameter, getTypeName(parameter, method, testClass)));
			}
			else{
				methodNode.addCategory(generatePartitionedCategoryModel(parameter, getTypeName(parameter, method, testClass)));
			}
		}
		return methodNode;
	}

	private static ExpectedCategoryNode generateExpectedCategoryModel(ILocalVariable parameter, String type) {
		return new ExpectedCategoryNode(parameter.getElementName(), type, getDefaultExpectedValue(type));
	}

	private static PartitionedCategoryNode generatePartitionedCategoryModel(ILocalVariable parameter, String type) {
		PartitionedCategoryNode category = new PartitionedCategoryNode(parameter.getElementName(), type);
		ArrayList<PartitionNode> defaultPartitions = generateDefaultPartitions(type);
		for(PartitionNode partition : defaultPartitions){
			category.addPartition(partition);
		}
		return category;
	}

	private static boolean isExpected(ILocalVariable parameter) throws JavaModelException {
		IAnnotation[] annotations = parameter.getAnnotations();
		for(IAnnotation annotation : annotations){
			if(annotation.getElementName().equals("expected")){
				return true;
			}
		}
		return false;
	}

	public static Object getDefaultExpectedValue(String type) {
		switch(type){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			return Constants.DEFAULT_EXPECTED_BYTE_VALUE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			return Constants.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			return Constants.DEFAULT_EXPECTED_CHAR_VALUE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			return Constants.DEFAULT_EXPECTED_DOUBLE_VALUE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			return Constants.DEFAULT_EXPECTED_FLOAT_VALUE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			return Constants.DEFAULT_EXPECTED_INT_VALUE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			return Constants.DEFAULT_EXPECTED_LONG_VALUE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			return Constants.DEFAULT_EXPECTED_SHORT_VALUE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return Constants.DEFAULT_EXPECTED_STRING_VALUE;
		default:
			return ClassUtils.defaultEnumExpectedValue(type);
		}
	}

	private static String getTypeName(ILocalVariable parameter, IMethod method, Class<?> testClass) {
		String typeSignature = parameter.getTypeSignature();
		switch(typeSignature){
		case Signature.SIG_BOOLEAN:
			return com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN;
		case Signature.SIG_BYTE:
			return com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE;
		case Signature.SIG_CHAR:
			return com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR;
		case Signature.SIG_DOUBLE:
			return com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE;
		case Signature.SIG_FLOAT:
			return com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT;
		case Signature.SIG_INT:
			return com.testify.ecfeed.model.Constants.TYPE_NAME_INT;
		case Signature.SIG_LONG:
			return com.testify.ecfeed.model.Constants.TYPE_NAME_LONG;
		case Signature.SIG_SHORT:
			return com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT;
		case "QString;":
			return com.testify.ecfeed.model.Constants.TYPE_NAME_STRING;
		default:
			if (typeSignature.startsWith("Q") && typeSignature.endsWith(";")){
				for (Method reflection : testClass.getMethods()) {
					if (method.getElementName().equals(reflection.getName())) {
						for (Class<?> type : reflection.getParameterTypes()) {
							if (type.getSimpleName().equals(typeSignature.substring(1, typeSignature.lastIndexOf(";")))) {
								return type.getCanonicalName();
							}
						}
					}
				}
			}
			return com.testify.ecfeed.model.Constants.TYPE_NAME_UNSUPPORTED;
		}
	}

	private static ArrayList<PartitionNode> generateDefaultPartitions(String typeSignature) {
		switch(typeSignature){
		case "boolean":
			return defaultBooleanPartitions();
		case "byte":
			return defaultBytePartitions();
		case "char":
			return defaultCharacterPartitions();
		case "double":
			return defaultDoublePartitions();
		case "float":
			return defaultFloatPartitions();
		case "int":
			return defaultIntegerPartitions();
		case "long":
			return defaultLongPartitions();
		case "short":
			return defaultShortPartitions();
		case "String":
			return defaultStringPartitions();
		default:
			return ClassUtils.defaultEnumPartitions(typeSignature);
		}
	}

	private static ArrayList<PartitionNode> defaultBooleanPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		partitions.add(new PartitionNode("true", true));
		partitions.add(new PartitionNode("false", false));	
		return partitions;
	}

	private static ArrayList<PartitionNode> defaultBytePartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		partitions.add(new PartitionNode("min", Byte.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (byte)-1));	
		partitions.add(new PartitionNode("zero", (byte)0));
		partitions.add(new PartitionNode("positive", (byte)1));	
		partitions.add(new PartitionNode("max", Byte.MAX_VALUE));
		return partitions;
	}

	private static ArrayList<PartitionNode> defaultCharacterPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		partitions.add(new PartitionNode("zero", '\u0000'));
		partitions.add(new PartitionNode("a", 'a'));
		partitions.add(new PartitionNode("z", 'z'));
		partitions.add(new PartitionNode("A", 'A'));
		partitions.add(new PartitionNode("Z", 'Z'));
		partitions.add(new PartitionNode("non ASCII", '\u00A7'));
		partitions.add(new PartitionNode("max", '\uffff'));
		return partitions;
	}

	private static ArrayList<PartitionNode> defaultDoublePartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		partitions.add(new PartitionNode("min", Double.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (double)-1));	
		partitions.add(new PartitionNode("zero", (double)0));
		partitions.add(new PartitionNode("positive", (double)1));	
		partitions.add(new PartitionNode("max", Double.MAX_VALUE));
		return partitions;
	}

	private static ArrayList<PartitionNode> defaultFloatPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		partitions.add(new PartitionNode("min", Float.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (float)-1));	
		partitions.add(new PartitionNode("zero", (float)0));
		partitions.add(new PartitionNode("positive", (float)1));	
		partitions.add(new PartitionNode("max", Float.MAX_VALUE));
		return partitions;
	}

	private static ArrayList<PartitionNode> defaultIntegerPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		partitions.add(new PartitionNode("min", Integer.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (int)-1));	
		partitions.add(new PartitionNode("zero", (int)0));
		partitions.add(new PartitionNode("positive", (int)1));	
		partitions.add(new PartitionNode("max", Integer.MAX_VALUE));
		return partitions;
	}

	private static ArrayList<PartitionNode> defaultLongPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		partitions.add(new PartitionNode("min", Long.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (long)-1));
		partitions.add(new PartitionNode("zero", (long)0));
		partitions.add(new PartitionNode("positive", (long)1));	
		partitions.add(new PartitionNode("max", Long.MAX_VALUE));
		return partitions;
	}

	private static ArrayList<PartitionNode> defaultShortPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		partitions.add(new PartitionNode("min", Short.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (short)-1));	
		partitions.add(new PartitionNode("zero", (short)0));
		partitions.add(new PartitionNode("positive", (short)1));	
		partitions.add(new PartitionNode("max", Short.MAX_VALUE));
		return partitions;
	}

	private static ArrayList<PartitionNode> defaultStringPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		partitions.add(new PartitionNode("null", null));
		partitions.add(new PartitionNode("empty", ""));
		partitions.add(new PartitionNode("lower case", "a"));
		partitions.add(new PartitionNode("upper case", "A"));
		partitions.add(new PartitionNode("mixed cases", "aA"));
		partitions.add(new PartitionNode("all latin", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		return partitions;
	}

	public static boolean validateConstraintName(String name) {
		if(name.length() < 1 || name.length() > 64) return false;
		if(name.matches("[ ]+.*")) return false;
		return true;
	}
	
	public static boolean validatePartitionStringValue(String valueString, String type){
		if(type.equals(com.testify.ecfeed.model.Constants.TYPE_NAME_STRING)) return true;
		return (getPartitionValueFromString(valueString, type) != null);
	}

	public static Object getPartitionValueFromString(String valueString, String type){
		try{
			switch(type){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return Boolean.valueOf(valueString).booleanValue();
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return Byte.valueOf(valueString).byteValue();
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				if(valueString.charAt(0) != '\\' || valueString.length() == 1) return(valueString.charAt(0));
				return Character.toChars(Integer.parseInt(valueString.substring(1)));
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return Double.valueOf(valueString).doubleValue();
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return Float.valueOf(valueString).floatValue();
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return Integer.valueOf(valueString).intValue();
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return Long.valueOf(valueString).longValue();
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return Short.valueOf(valueString).shortValue();
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return valueString;
			default:
				return ClassUtils.enumPartitionValue(valueString, type, ClassUtils.getClassLoader(true, null));
			}
		}catch(NumberFormatException|IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public static boolean isClassImplemented(ClassNode node) {
		return ClassUtils.isClassImplemented(node.getQualifiedName());
	}
	
	public static boolean isPartitionImplemented(PartitionNode node) {
		boolean implemented = true;
		
		Object value = node.getValue();
		if (value != null) {
			if (value.getClass().isEnum()) {
				if (!ClassUtils.isEnumImplemented(((Enum<?>)value).name(), value.getClass().getName())) {
					implemented = false;
				}
			}
		}
		
		return implemented;
	}
	
	public static boolean isTestCaseImplemented(TestCaseNode node) {
		return allPartitionsImplemented(node.getTestData());
	}
	
	public static boolean isTestCasePartiallyImplemented(TestCaseNode node) {
		return anyPartitionImplemented(node.getTestData());
	}
	
	private static boolean allPartitionsImplemented(List<PartitionNode> partitions) {
		boolean implemented = false;
		
		if (partitions.size() > 0) {
			implemented = true;
		}
		
		for (PartitionNode partition : partitions) {
			implemented = isPartitionImplemented(partition);
			if (implemented == false) {
				break;
			}
		}
		
		return implemented;		
	}
	
	private static boolean anyPartitionImplemented(List<PartitionNode> partitions) {
		boolean implemented = false;
		
		for (PartitionNode partition : partitions) {
			implemented = isPartitionImplemented(partition);
			if (implemented == true) {
				break;
			}
		}
		
		return implemented;		
	}
	
	public static boolean isExpectedCategoryImplemented(ExpectedCategoryNode node) {
		return ModelUtils.isPartitionImplemented(node.getDefaultValuePartition());
	}
	
	public static boolean isPartitionedCategoryImplemented(PartitionedCategoryNode node) {
		return allPartitionsImplemented(node.getPartitions());
	}
	
	public static boolean isPartitionedCategoryPartiallyImplemented(PartitionedCategoryNode node) {
		return anyPartitionImplemented(node.getPartitions());
	}
	
	public static boolean isMethodImplemented(MethodNode methodModel) {
		boolean implemented = false;
		
		try {
			Class<?> testClass = ClassUtils.getClassLoader(true, null).loadClass(methodModel.getClassNode().getQualifiedName());
			for (Method method : testClass.getMethods()){
				List<String> argTypes = getArgTypes(method);
				implemented = (methodModel.getClassNode().getMethod(method.getName(), argTypes) == methodModel);
				if (implemented == true) {
					break;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return implemented;
	}

	private static List<String> getArgTypes(Method method) {
		List<String> argTypes = new ArrayList<String>();
		
		for (Class<?> arg : method.getParameterTypes()){
			if (arg.isEnum()) {
				argTypes.add(arg.getCanonicalName());				
			} else {
				argTypes.add(arg.getSimpleName());	
			}
		}
		
		return argTypes;
	}
}