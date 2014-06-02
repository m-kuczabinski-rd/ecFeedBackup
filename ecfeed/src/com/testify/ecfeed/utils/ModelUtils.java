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
import java.util.Collection;
import java.util.HashMap;
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

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
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
			return ClassUtils.defaultEnumPartitions(typeSignature);
		}
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
		if(name.length() < 1) return false;
		if(!name.matches("(^[a-zA-Z][a-zA-Z0-9_$]*)|(^[_][a-zA-Z0-9_$]+)")) return false;
		return assertNotKeyword(name);
	}
	
	public static boolean assertNotKeyword(String name){
		String[] javaKeywords =
				{ "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do",
						"if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public",
						"throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char",
						"final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
						"native", "super", "while", "null", "true", "false" };
		for(String keyword : javaKeywords){
			if(name.equals(keyword)) return false;
		}
		return true;
	}

	public static boolean isClassQualifiedNameValid(String qualifiedName) {
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		boolean valid = (lastDotIndex != -1) ? true : false;
		if (valid) {
			String packageName = qualifiedName.substring(0, lastDotIndex);
			String className = qualifiedName.substring(lastDotIndex + 1, qualifiedName.length());
			valid = ModelUtils.validateNodeName(packageName) && ModelUtils.validateNodeName(className);
		}
		return valid;
	}

	public static boolean validatePartitionStringValue(String valueString, String type){
		if(type.equals(com.testify.ecfeed.model.Constants.TYPE_NAME_STRING)) return true;
		return (getPartitionValueFromString(valueString, type) != null);
	}

	private static boolean isPredefinedValueString(String valueString) {
		return valueString.equals(Constants.NULL_VALUE_STRING_REPRESENTATION) ||
				valueString.equals(Constants.BOOLEAN_FALSE_STRING_REPRESENTATION) ||
				valueString.equals(Constants.BOOLEAN_TRUE_STRING_REPRESENTATION) ||
				valueString.equals(Constants.MIN_VALUE_STRING_REPRESENTATION) ||
				valueString.equals(Constants.MAX_VALUE_STRING_REPRESENTATION) ||
				valueString.equals(Constants.POSITIVE_INFINITY_STRING_REPRESENTATION) ||
				valueString.equals(Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION);
	}

	public static Object getBooleanPredefinedValueFromString(String valueString) {
		if (valueString.equals(Constants.BOOLEAN_FALSE_STRING_REPRESENTATION)) {
			return Boolean.FALSE;
		} else if (valueString.equals(Constants.BOOLEAN_FALSE_STRING_REPRESENTATION)) {
			return Boolean.TRUE;
		}
		return null;
	}

	public static Object getBytePredefinedValueFromString(String valueString) {
		if (valueString.equals(Constants.MIN_VALUE_STRING_REPRESENTATION)) {
			return Byte.MIN_VALUE;
		} else if (valueString.equals(Constants.MAX_VALUE_STRING_REPRESENTATION)) {
			return Byte.MAX_VALUE;
		}
		return null;
	}

	public static Object getCharPredefinedValueFromString(String valueString) {
		if (valueString.equals(Constants.MIN_VALUE_STRING_REPRESENTATION)) {
			return Character.MIN_VALUE;
		} else if (valueString.equals(Constants.MAX_VALUE_STRING_REPRESENTATION)) {
			return Character.MAX_VALUE;
		}
		return null;
	}

	public static Object getIntegerPredefinedValueFromString(String valueString) {
		if (valueString.equals(Constants.MIN_VALUE_STRING_REPRESENTATION)) {
			return Integer.MIN_VALUE;
		} else if (valueString.equals(Constants.MAX_VALUE_STRING_REPRESENTATION)) {
			return Integer.MAX_VALUE;
		}
		return null;
	}

	public static Object getLongPredefinedValueFromString(String valueString) {
		if (valueString.equals(Constants.MIN_VALUE_STRING_REPRESENTATION)) {
			return Long.MIN_VALUE;
		} else if (valueString.equals(Constants.MAX_VALUE_STRING_REPRESENTATION)) {
			return Long.MAX_VALUE;
		}
		return null;
	}

	public static Object getShortPredefinedValueFromString(String valueString) {
		if (valueString.equals(Constants.MIN_VALUE_STRING_REPRESENTATION)) {
			return Short.MIN_VALUE;
		} else if (valueString.equals(Constants.MAX_VALUE_STRING_REPRESENTATION)) {
			return Short.MAX_VALUE;
		}
		return null;
	}

	public static Object getDoublePredefinedValueFromString(String valueString) {
		if (valueString.equals(Constants.MIN_VALUE_STRING_REPRESENTATION)) {
			return Double.MIN_VALUE;
		} else if (valueString.equals(Constants.MAX_VALUE_STRING_REPRESENTATION)) {
			return Double.MAX_VALUE;
		} else if (valueString.equals(Constants.POSITIVE_INFINITY_STRING_REPRESENTATION)) {
			return Double.POSITIVE_INFINITY;
		} else if (valueString.equals(Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION)) {
			return Double.NEGATIVE_INFINITY;
		}
		return null;
	}

	public static Object getFloatPredefinedValueFromString(String valueString) {
		if (valueString.equals(Constants.MIN_VALUE_STRING_REPRESENTATION)) {
			return Float.MIN_VALUE;
		} else if (valueString.equals(Constants.MAX_VALUE_STRING_REPRESENTATION)) {
			return Float.MAX_VALUE;
		} else if (valueString.equals(Constants.POSITIVE_INFINITY_STRING_REPRESENTATION)) {
			return Float.POSITIVE_INFINITY;
		} else if (valueString.equals(Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION)) {
			return Float.NEGATIVE_INFINITY;
		}
		return null;
	}

	public static Object getPredefinedValueFromString(String valueString, String type){
		switch (type) {
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			return getBooleanPredefinedValueFromString(valueString);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			return getBytePredefinedValueFromString(valueString);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			return getCharPredefinedValueFromString(valueString);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			return getDoublePredefinedValueFromString(valueString);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			return getFloatPredefinedValueFromString(valueString);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			return getIntegerPredefinedValueFromString(valueString);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			return getLongPredefinedValueFromString(valueString);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			return getShortPredefinedValueFromString(valueString);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			if (valueString.equals(Constants.NULL_VALUE_STRING_REPRESENTATION)) {
				return null;
			}
		default:
			return null;
		}
	}

	public static Object getPartitionValueFromString(String valueString, String type) {
		if (isPredefinedValueString(valueString)) {
			return getPredefinedValueFromString(valueString, type);
		} else {
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
					return ClassUtils.enumPartitionValue(valueString, type, ClassUtils.getClassLoader(false, null));
				}
			} catch (NumberFormatException|IndexOutOfBoundsException e) {
				return null;
			}
		}
	}

	public static boolean classDefinitionImplemented(ClassNode node) {
		boolean implemented = false;
		try {
			Class<?> typeClass = ClassUtils.getClassLoader(true, null).loadClass(node.getQualifiedName());
			if (typeClass != null) {
				implemented = true;
			}
		} catch (Throwable e) {
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
		boolean implemented = (getPartitionValueFromString(node.getValueString(), node.getCategory().getType()) != null);
		if (!implemented && node.getCategory().getType().equals(com.testify.ecfeed.model.Constants.TYPE_NAME_STRING)) {
			implemented = true;
		}
		return implemented;
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
		boolean implemented = (partitions.size() > 0) ? true : false;

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
	
	public static boolean isCategoryImplemented(CategoryNode node) {
		if(node.isExpected()){
			return ModelUtils.isPartitionImplemented(node.getDefaultValuePartition());
		}
		else{
			return allPartitionsImplemented(node.getPartitions());
		}
	}
	
	public static boolean isCategoryPartiallyImplemented(CategoryNode node) {
		return anyPartitionImplemented(node.getPartitions());
	}

	public static boolean methodDefinitionImplemented(MethodNode methodModel) {
		boolean implemented = false;
		
		try {
			IType type = getTypeObject(methodModel.getClassNode().getQualifiedName());
			Class<?> testClass = ClassUtils.getClassLoader(true, null).loadClass(methodModel.getClassNode().getQualifiedName());
			if ((type != null) && (testClass != null)) {
				for (IMethod method : type.getMethods()){
					if (method.getElementName().equals(methodModel.getName())) {
						List<String> argTypes = getArgTypes(method, testClass);
						List<String> paramNames = getParamNames(method);
						if (methodModel.getCategoriesTypes().equals(argTypes) &&
								methodModel.getCategoriesNames().equals(paramNames)) {
							implemented = true;
							
							List<CategoryNode> categories = methodModel.getCategories();
							for (int i = 0; i < categories.size(); ++i) {
								if (categories.get(i).isExpected()) {
									ILocalVariable parameter = method.getParameters()[i];
									IAnnotation[] annotations = parameter.getAnnotations();
									if ((annotations.length < 1) || !annotations[0].getElementName().equals("expected")) {
										implemented = false;
										break;
									}
								}
							}
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
	
	private static List<String> getParamNames(IMethod method) {
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
	
}
