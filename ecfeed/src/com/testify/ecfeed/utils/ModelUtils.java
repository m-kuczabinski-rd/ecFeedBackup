/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.utils;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class ModelUtils {
	
	public static  ClassNode generateClassModel(IType type, boolean testMethodsOnly){ 
		ClassNode classNode = new ClassNode(type.getFullyQualifiedName());
		try{
			Class<?> testClass = ClassUtils.loadClass(ClassUtils.getClassLoader(true, null), type.getFullyQualifiedName());
			for(IMethod method : type.getMethods()){
				if((testMethodsOnly && isAnnotated(method, "Test")) || (!testMethodsOnly && isPublicVoid(method))){
					try{
						MethodNode methodModel = generateMethodModel(method, testClass);
						if(methodModel != null){
							classNode.addMethod(methodModel);
						}
					} catch(Throwable e){		
						System.err.println("Unexpected error during method import");
					}
				}
			}
		}
		catch(Throwable e){
			System.err.println("Unexpected error during class import");
		}
		return classNode;
	}
	
	private static boolean isAnnotated(IMethod method, String name) throws JavaModelException{
		IAnnotation[] annotations = method.getAnnotations();
		for(IAnnotation annotation : annotations){
			if(annotation.getElementName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	private static boolean isPublicVoid(IMethod method) throws JavaModelException{
		return (method.getReturnType().equals(Signature.SIG_VOID) && Flags.isPublic(method.getFlags()));
	}
	
	public static ClassNode generateClassModel(IType type){
		return generateClassModel(type, true);
	}

	public static boolean isClassModelUpToDate(ClassNode classNode) throws JavaModelException{
		return(getObsoleteMethods(classNode, classNode.getQualifiedName()).size() == 0 && 
			   getNotContainedMethods(classNode, classNode.getQualifiedName(), false).size() == 0);
	}
	
	public static void setUniqueNodeName(GenericNode child, GenericNode desiredParent){
		String name = child.getName();
		int i = 1;
		while(desiredParent.getChild(name) != null){
			name = child.getName() + i++;
		}
		child.setName(name);
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
	public static List<MethodNode> getNotContainedMethods(ClassNode classNode, String qualifiedTypeName, boolean testOnly){
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

		ClassNode model = generateClassModel(type, testOnly);
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
		List<MethodNode> potentialMatches = getNotContainedMethods(parent, parent.getQualifiedName(), false);
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
			methodNode.addCategory(generateCategoryModel(parameter, getTypeName(parameter, method, testClass), isExpected(parameter)));
		}
		return methodNode;
	}
	
	private static CategoryNode generateCategoryModel(ILocalVariable parameter, String type, boolean expected){
		CategoryNode category = new CategoryNode(parameter.getElementName(), type, expected);
		if(expected){
			category.setDefaultValueString(getDefaultExpectedValueString(type));
			return category;
		} else{
			ArrayList<PartitionNode> defaultPartitions = generateDefaultPartitions(type);
			for(PartitionNode partition : defaultPartitions){
				category.addPartition(partition);
			}
			return category;
		}
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

	public static boolean isMethodWithParameters(MethodNode methodNode) {
		return (methodNode.getCategories().size() > 0);
	}

	public static String getDefaultExpectedValueString(String type) {
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
			return ClassUtils.defaultEnumExpectedValueString(type);
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

	public static ArrayList<PartitionNode> generateDefaultPartitions(String typeSignature) {
		switch(typeSignature){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			return defaultBooleanPartitions();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			return defaultFloatPartitions();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			return defaultFloatPartitions();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return defaultStringPartitions();
		default:
			return defaultEnumPartitions(typeSignature);
		}
	}

	public static boolean isTypePrimitive(String typeSignature){
		switch(typeSignature){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return true;
		default:
			return false;
		}
	}
	
	public static ArrayList<PartitionNode> defaultEnumPartitions(String typeName) {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		Class<?> typeClass = ClassUtils.loadClass(ClassUtils.getClassLoader(true, null), typeName);
		if (typeClass != null) {
			for (Object object: typeClass.getEnumConstants()) {
				partitions.add(new PartitionNode(object.toString(), ((Enum<?>)object).name()));
			}
		}
		return partitions;
	}

	public static HashMap<String, String> generatePredefinedValues(String type) {
		switch(type){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			return predefinedBooleanValues();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			return predefinedIntegerValues();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			return predefinedIntegerValues();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			return predefinedFloatValues();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			return predefinedFloatValues();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			return predefinedIntegerValues();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			return predefinedIntegerValues();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			return predefinedIntegerValues();
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return predefinedStringValues();
		default:
			return ClassUtils.defaultEnumValues(type);
		}
	}

	private static ArrayList<PartitionNode> defaultBooleanPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedBooleanValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	private static HashMap<String, String> predefinedBooleanValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("true", Constants.BOOLEAN_TRUE_STRING_REPRESENTATION);
		values.put("false", Constants.BOOLEAN_FALSE_STRING_REPRESENTATION);
		return values;
	}

	private static ArrayList<PartitionNode> defaultIntegerPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedIntegerValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	private static HashMap<String, String> predefinedIntegerValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		return values;
	}

	private static ArrayList<PartitionNode> defaultFloatPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedFloatValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	private static HashMap<String, String> predefinedFloatValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		values.put("positive infinity", Constants.POSITIVE_INFINITY_STRING_REPRESENTATION);
		values.put("negative infinity", Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION);
		return values;
	}

	private static ArrayList<PartitionNode> defaultStringPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedStringValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	private static HashMap<String, String> predefinedStringValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("null", Constants.NULL_VALUE_STRING_REPRESENTATION);
		return values;
	}

	public static boolean validateConstraintName(String name) {
		if(name.length() < 1 || name.length() > 64) return false;
		if(name.matches("[ ]+.*")) return false;
		return true;
	}
	
	public static boolean validateNodeName(String name){
		if(name.length() < 1 || name.length() > com.testify.ecfeed.model.Constants.MAX_NODE_NAME_LENGTH) return false;
		if(!name.matches("(^[a-zA-Z][a-zA-Z0-9_$]*)|(^[_][a-zA-Z0-9_$]+)")) return false;
		return isKeyword(name);
	}
	
	public static boolean isKeyword(String name){
		String[] javaKeywords =
				{ "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do",
						"if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public",
						"throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char",
						"final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
						"native", "super", "while", "null", "true", "false" };
		
		return !Arrays.asList(javaKeywords).contains(name);
	}

	public static boolean isClassQualifiedNameValid(String qualifiedName) {
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		if(!( (lastDotIndex != -1) ? true : false)) return false;
			String path[] = qualifiedName.split("\\.", -1);
			for(String name: path){
				if(!validateNodeName(name)) return false;
			}
		
		return true;
	}

	public static boolean validatePartitionStringValue(String valueString, String type) {
		if (type.equals(com.testify.ecfeed.model.Constants.TYPE_NAME_STRING)) return true;

		if (valueString.length() == 0) return false;
		if (valueString.length() > com.testify.ecfeed.model.Constants.MAX_NODE_NAME_LENGTH) return false;

		if (ClassUtils.getPartitionValueFromString(valueString, type, ClassUtils.getClassLoader(true, null)) != null){
			return true;
		} else if (!getJavaTypes().contains(type)) {
				return valueString.matches("^[a-zA-Z_$][a-zA-Z0-9_$]*$");
		}
		return false;
	}

	public static boolean classDefinitionImplemented(ClassNode node) {
		boolean implemented = false;
		Class<?> typeClass = ClassUtils.loadClass(ClassUtils.getClassLoader(true, null), node.getQualifiedName());
		if (typeClass != null) {
			implemented = true;
		}
		return implemented;
	}

	public static boolean classMethodsImplemented(ClassNode node) {
		boolean implemented = true;

		for (MethodNode method : node.getMethods()) {
			if (!isMethodImplemented(method)) {
				implemented = false;
				break;
			}
		}

		return implemented;
	}

	public static boolean isClassImplemented(ClassNode node) {
		return classDefinitionImplemented(node) && classMethodsImplemented(node);
	}

	public static boolean isClassPartiallyImplemented(ClassNode node) {
		return classDefinitionImplemented(node) && !classMethodsImplemented(node);
	}

	public static boolean isPartitionImplemented(PartitionNode node) {
		if(node.isAbstract()){
			for(PartitionNode child : node.getPartitions()){
				if(!isPartitionImplemented(child)){
					return false;
				}
			}
			return true;
		}
		else{
			URLClassLoader loader = ClassUtils.getClassLoader(false, null);
			return ClassUtils.isPartitionImplemented(node.getExactValueString(), node.getCategory().getType(), loader);
		}
	}

	public static boolean isPartitionPartiallyImplemented(PartitionNode node) {
		if(node.isAbstract()){
			boolean hasUnimplemented = false;
			boolean hasNotUnimplemented = false;
			for(PartitionNode child : node.getPartitions()){
				if(isPartitionImplemented(child) || isPartitionPartiallyImplemented(child)){
					hasNotUnimplemented = true;
				}
				else{
					hasUnimplemented = true;
				}
			}
			if(hasUnimplemented && hasNotUnimplemented){
				return true;
			}
		}
		return false;
	}

	public static boolean isTestCaseImplemented(TestCaseNode node) {
		return allPartitionsImplemented(node.getTestData());
	}
	
	public static boolean isTestCasePartiallyImplemented(TestCaseNode node) {
		return anyPartitionImplemented(node.getTestData());
	}

	public static boolean isTestSuiteImplemented(MethodNode methodNode, String suiteName) {
		Collection<TestCaseNode> testSuite = methodNode.getTestCases(suiteName);
		boolean implemented = (testSuite.size() > 0) ? true : false;

		for (TestCaseNode testCase : testSuite) {
			implemented = ModelUtils.isTestCaseImplemented(testCase);
			if (implemented == false) {
				break;
			}
		}

		return implemented;
	}

	private static boolean allPartitionsImplemented(List<PartitionNode> partitions) {
		boolean implemented = true;

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

	public static boolean categoryDefinitionImplemented(CategoryNode node) {
		boolean implemented = false;
		Class<?> typeClass = ClassUtils.loadClass(ClassUtils.getClassLoader(true, null), node.getType());
		if (typeClass != null) {
			implemented = true;
		}
		return implemented;
	}

	public static boolean isCategoryImplemented(CategoryNode node) {
		if(node.isExpected()){
			return ModelUtils.isPartitionImplemented(node.getDefaultValuePartition());
		}
		else{
			return (allPartitionsImplemented(node.getPartitions())
					&& !node.getPartitions().isEmpty());
		}
	}
	
	public static boolean isCategoryPartiallyImplemented(CategoryNode node) {
		return anyPartitionImplemented(node.getPartitions());
	}

	public static boolean methodDefinitionImplemented(MethodNode methodModel) {
		boolean implemented = false;
		
		try {
			IType type = getTypeObject(methodModel.getClassNode().getQualifiedName());
			Class<?> testClass = ClassUtils.loadClass(ClassUtils.getClassLoader(true, null), methodModel.getClassNode().getQualifiedName());
			if ((type != null) && (testClass != null)) {
				for (IMethod method : type.getMethods()){
					if (method.getElementName().equals(methodModel.getName())) {
						List<String> argTypes = getArgTypes(method, testClass);
						if (methodModel.getCategoriesTypes().equals(argTypes)){
							implemented = true;
							break;
						}
					}
				}
			}
		} catch (Throwable e) {
		}
		
		return implemented;
	}

	public static boolean methodCategoriesImplemented(MethodNode methodModel) {
		boolean implemented = true;

		for (CategoryNode category : methodModel.getCategories()) {
			if (!isCategoryImplemented(category)) {
				implemented = false;
				break;
			}
		}

		return implemented;
	}

	public static boolean isMethodImplemented(MethodNode methodModel) {
		return methodDefinitionImplemented(methodModel) && methodCategoriesImplemented(methodModel);
	}

	public static boolean isMethodPartiallyImplemented(MethodNode methodModel) {
		return methodDefinitionImplemented(methodModel) && !methodCategoriesImplemented(methodModel);
	}

	private static List<String> getArgTypes(IMethod method, Class<?> testClass) {
		List<String> argTypes = new ArrayList<String>();
		
		try {
			for (ILocalVariable arg : method.getParameters()){
				argTypes.add(getTypeName(arg, method, testClass));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return argTypes;
	}
	
	public static List<String> getParamNames(IMethod method) {
		List<String> argTypes = new ArrayList<String>();
		
		try {
			for (ILocalVariable arg : method.getParameters()){
				argTypes.add(arg.getElementName());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return argTypes;
	}

	public static ArrayList<String> getJavaTypes() {
		ArrayList<String> types = new ArrayList<String>();
		types.add(com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN);
		types.add(com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE);
		types.add(com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR);
		types.add(com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT);
		types.add(com.testify.ecfeed.model.Constants.TYPE_NAME_INT);
		types.add(com.testify.ecfeed.model.Constants.TYPE_NAME_LONG);
		types.add(com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT);
		types.add(com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE);
		types.add(com.testify.ecfeed.model.Constants.TYPE_NAME_STRING);
		return types;
	}
}
