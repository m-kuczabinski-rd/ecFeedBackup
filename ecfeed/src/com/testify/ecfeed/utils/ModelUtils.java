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
import java.util.Iterator;
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
			category.setDefaultValue(getDefaultExpectedValue(type));
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
	
	public static boolean validateCategoryName(String name){
		if(name.length() < 1) return false;
		if(!name.matches("(^[a-zA-Z][a-zA-Z0-9_$]*)|(^[_][a-zA-Z0-9_$]+))")) return false;
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
	
	public static boolean isPartitionImplemented(PartitionNode node) {
		boolean implemented = true;
		
		Object value = node.getValue();
		if (value != null) {
			if (value.getClass().isEnum()) {
				ClassLoader loader = ClassUtils.getClassLoader(true, null);
				if (ClassUtils.enumPartitionValue(((Enum<?>)value).name(), value.getClass().getName(), loader) == null) {
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
	
	public static boolean isMethodImplemented(MethodNode methodModel) {
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
	
	
	public static void changeCategoryType(CategoryNode category, String type){
		String oldtype = category.getType();
		int compatibility = areTypesCompatible(oldtype, type);

		// the same type, no changes
		if(compatibility == 0){
		}
		else{
			MethodNode method = category.getMethod();
			int index = method.getCategories().indexOf(category);

			// types cannot be converted, remove everything connected
			if(compatibility == 2){
				// remove category and any mentioning constraints
				method.removeCategory(category);
				// Clear test cases
				method.getTestCases().clear();
				// add new category in place of removed one
				CategoryNode newcategory = new CategoryNode(category.getName(), type, category.isExpected());
				newcategory.setDefaultValue(getDefaultExpectedValue(type));
				newcategory.setParent(method);
				method.getCategories().add(index, newcategory);
			}
			// types can be converted
			else{
				category.setType(type);
				// Expected Category
				if(category.isExpected()){
					Object newvalue = adaptValueToType(category.getDefaultValue(), type);
					if(newvalue != null){
						category.setDefaultValue(newvalue);
					} else{
						category.setDefaultValue(getDefaultExpectedValue(type));
					}
					// adapt test cases
					Iterator<TestCaseNode> iterator = method.getTestCases().iterator();
					while(iterator.hasNext()){
						TestCaseNode testCase = iterator.next();
						Object tcvalue = adaptValueToType(testCase.getTestData().get(index).getValue(), type);
						if(tcvalue == null){
							iterator.remove();
						} else{
							testCase.getTestData().get(index).setValue(tcvalue);
						}
					}
					// adapting constraints of expected category would be really messy. Might add it at later date if need occurs.
					method.removeMentioningConstraints(category);
				} 
				// Partitioned Category
				else {
					// Try to adapt partitions; If it fails - remove partition. Mentioning test cases and constraints are handled in model.
					for(PartitionNode partition : category.getPartitions()){
						adaptOrRemovePartitions(partition, type);
					}
					category.setDefaultValue(getDefaultExpectedValue(type));
				}
			}
		}
	}
	
	private static void adaptOrRemovePartitions(PartitionNode partition, String type){
		List<PartitionNode> partitions = partition.getLeafPartitions();
		if(partitions.isEmpty()){
			Object newvalue = adaptValueToType(partition, type);
			if(newvalue != null){
				partition.setValue(newvalue);
			} else{
				partition.getParent().removePartition(partition);
			}
		} else{
			for(PartitionNode childpart : partitions){
				adaptOrRemovePartitions(childpart, type);
			}
		}	
	}
	
	public static Object adaptValueToBoolean(Object value){
		if(value instanceof Boolean){
			return value;
		}else if(value instanceof Byte){
			if((Byte)value != 0)
				return true;
		} else if(value instanceof Short){
			if((Short)value != 0)
				return true;
		} else if(value instanceof Integer){
			if((Integer)value != 0)
				return true;
		} else if(value instanceof Long){
			if((Long)value != 0)
				return true;
		} else if(value instanceof String){
			return Boolean.parseBoolean((String)value);
		}
		return null;
	}
	
	public static Object adaptValueToByte(Object value){
		try{
			if(value instanceof Byte){
				return value;
			}else if(value instanceof Boolean){
				if((Boolean)value)
					return 1;
				else
					return 0;
			} else if(value instanceof Short){
				return (Byte)value;
			} else if(value instanceof Integer){
				return (Byte)value;
			} else if(value instanceof Long){
				return (Byte)value;
			} else if(value instanceof Character){
				return (byte)((char)value);
			} else if(value instanceof String){
				return Byte.parseByte((String)value);
			} else if(value instanceof Float){
				return (Byte)value;
			} else if(value instanceof Double){
				return (Byte)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}
	
	public static Object adaptValueToCharacter(Object value){
		if(value instanceof Character){
			return value;
		} else if(value instanceof Byte){
			return (char)((byte)value);
		} else if(value instanceof Short){
			return (char)((short)value);
		} else if(value instanceof String){
			if(((String)value).length() == 1)
				return ((String)value).charAt(0);
			if(((String)value).length() == 0)
				return '\0';
		}
		return null;
	}

	public static Object adaptValueToDouble(Object value){
		try{
			if(value instanceof Double){
				return value;
			}else if(value instanceof Byte){
				return (Double)value;
			} else if(value instanceof Short){
				return (Double)value;
			} else if(value instanceof Integer){
				return (Double)value;
			} else if(value instanceof Long){
				return (Double)value;
			} else if(value instanceof String){
				return Double.parseDouble((String)value);
			} else if(value instanceof Float){
				return (Double)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}
	
	public static Object adaptValueToFloat(Object value){
		try{
			if(value instanceof Float){
				return value;
			}else if(value instanceof Byte){
				return (Float)value;
			} else if(value instanceof Short){
				return (Float)value;
			} else if(value instanceof Long){
				return (Float)value;
			}  else if(value instanceof Integer){
				return (Double)value;
			} else if(value instanceof String){
				return Float.parseFloat((String)value);
			} else if(value instanceof Double){
				return (Float)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}
	
	public static Object adaptValueToShort(Object value){
		try{
			if(value instanceof Short){
				return value;
			}else if(value instanceof Boolean){
				if((Boolean)value)
					return 1;
				else
					return 0;
			} else if(value instanceof Byte){
				return (Short)value;
			} else if(value instanceof Integer){
				return (Short)value;
			} else if(value instanceof Long){
				return (Short)value;
			} else if(value instanceof Character){
				return (short)((char)value);
			} else if(value instanceof String){
				return Short.parseShort((String)value);
			} else if(value instanceof Float){
				return (Short)value;
			} else if(value instanceof Double){
				return (Short)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}

	public static Object adaptValueToInteger(Object value){
		try{
			if(value instanceof Integer){
				return value;
			}else if(value instanceof Boolean){
				if((Boolean)value)
					return 1;
				else
					return 0;
			} else if(value instanceof Byte){
				return (Integer)value;
			} else if(value instanceof Short){
				return (Integer)value;
			} else if(value instanceof Long){
				return (Integer)value;
			} else if(value instanceof Character){
			} else if(value instanceof String){
				return Integer.parseInt((String)value);
			} else if(value instanceof Float){
				return (Integer)value;
			} else if(value instanceof Double){
				return (Integer)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}

	public static Object adaptValueToLong(Object value){
		try{
			if(value instanceof Long){
				return value;
			}else if(value instanceof Boolean){
				if((Boolean)value)
					return 1;
				else
					return 0;
			} else if(value instanceof Byte){
				return (Long)value;
			} else if(value instanceof Short){
				return (Long)value;
			} else if(value instanceof Long){
				return (Long)value;
			} else if(value instanceof Character){
			} else if(value instanceof String){
				return Long.parseLong((String)value);
			} else if(value instanceof Float){
				return (Long)value;
			} else if(value instanceof Double){
				return (Long)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}
	
	public static Object adaptValueToString(Object value){
		try{
			if(value instanceof Boolean){
				return Boolean.toString((boolean)value);
			} else if(value instanceof Byte){
				return ((Byte)value).toString();
			} else if(value instanceof Short){
				return ((Short)value).toString();
			}  else if(value instanceof Integer){
				return ((Integer)value).toString();
			} else if(value instanceof Long){
				return ((Long)value).toString();
			} else if(value instanceof Character){
				return ((Character)value).toString();
			} else if(value instanceof String){
				return value;
			} else if(value instanceof Float){
				return ((Float)value).toString();
			} else if(value instanceof Double){
				return ((Double)value).toString();
			} else if(value != null && value instanceof Enum<?>){
				Enum<?> e = (Enum<?>)value;
				return e.name();
			}
		} catch(NumberFormatException e){
		}
		return null;
	}
	
	public static Object adaptValueToType(Object value, String type){
		switch(type){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			return adaptValueToBoolean(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			return adaptValueToByte(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			return adaptValueToCharacter(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			return adaptValueToDouble(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			return adaptValueToFloat(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			return adaptValueToInteger(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			return adaptValueToLong(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			return adaptValueToShort(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return adaptValueToString(value);
		default:
		}
		return null;
	}
	
	/*	Returns 0 if types are equal, 1 if types can be converted and 2 if types cannot be converted.
	*	(probably should remove equal cases from switch; left them in case enums would require some special treatment,
	*	i.e. we assume they differ in any case;
	*/
	public static int areTypesCompatible(String oldtype, String newtype){
		if(oldtype.equals(newtype)) return 0;
		
		switch(oldtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 0;
			default:
				return 2;
			}
			// User-defined convert to string and to another user defined directly (
		default:
			switch(newtype){
				case  com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
					return 1;
				default:
					return 2;
			}
		}
	}
}
