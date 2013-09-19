/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class EcModelUtils {
	public static boolean validateModelName(String name){
		if (name == null) return false;
		if(name.length() == 0) return false;
		if(name.length() >= Constants.MAX_MODEL_NAME_LENGTH) return false;
		if(name.matches("[ ]+.*")) return false;
		
		return true;
	}

	
	/**
	 * Checks if certain name is valid for given partition in given category
	 * @param name Name to validate
	 * @param parent Parent for which the name is validated
	 * @param partition Partition for which the name is validated. May be null
	 * @return
	 */ 
	public static boolean validatePartitionName(String name, CategoryNode parent, PartitionNode partition){
		if (name == null) return false;
		if(name.length() == 0) return false;
		if(name.length() >= Constants.MAX_PARTITION_NAME_LENGTH) return false;
		if(name.matches("[ ]+.*")) return false;

		PartitionNode sibling = parent.getPartition(name);
		if(sibling != null && sibling != partition) return false;
		
		return true;
	}
	
	public static boolean validatePartitionStringValue(String valueString, CategoryNode parent){
		String type = parent.getType(); 
		if(type.equals(Constants.TYPE_NAME_STRING)) return true;
		return (getPartitionValueFromString(valueString, type) != null);
	}

	public static Object getPartitionValueFromString(String valueString, String type){
		try{
			switch(type){
			case Constants.TYPE_NAME_BOOLEAN:
				return Boolean.valueOf(valueString).booleanValue();
			case Constants.TYPE_NAME_BYTE:
				return Byte.valueOf(valueString).byteValue();
			case Constants.TYPE_NAME_CHAR:
				if(valueString.charAt(0) != '\\' || valueString.length() == 1) return(valueString.charAt(0));
				return Character.toChars(Integer.parseInt(valueString.substring(1)));
			case Constants.TYPE_NAME_DOUBLE:
				return Double.valueOf(valueString).doubleValue();
			case Constants.TYPE_NAME_FLOAT:
				return Float.valueOf(valueString).floatValue();
			case Constants.TYPE_NAME_INT:
				return Integer.valueOf(valueString).intValue();
			case Constants.TYPE_NAME_LONG:
				return Long.valueOf(valueString).longValue();
			case Constants.TYPE_NAME_SHORT:
				return Short.valueOf(valueString).shortValue();
			case Constants.TYPE_NAME_STRING:
				return valueString;
			default:
				return null;
			}
		}catch(NumberFormatException|IndexOutOfBoundsException e){
			return null;
		}
	}

	/**
	 * Removes all test cases from the tree that have references to the provided partition
	 * @param partition Partition to remove
	 */
	public static void removeReferences(PartitionNode partition){
		MethodNode method = getMethodAncestor(partition);
		Collection<TestCaseNode> testCases = method.getTestCases();
		ArrayList<TestCaseNode> testCasesToRemove = new ArrayList<TestCaseNode>();
		for(TestCaseNode testCase : testCases){
			if(testCase.mentions(partition)){
				testCasesToRemove.add(testCase);
			}
		}
		for(TestCaseNode node : testCasesToRemove){
			method.removeChild(node);
		}

		ArrayList<ConstraintNode> constraintsToRemove = new ArrayList<ConstraintNode>();
		Collection<ConstraintNode> constraints = method.getConstraints();
		for(ConstraintNode constraint : constraints){
			if(constraint.mentions(partition)){
				constraintsToRemove.add(constraint);
			}
		}
		for(ConstraintNode node : constraintsToRemove){
			method.removeChild(node);
		}
	}

	public static MethodNode getMethodAncestor(IGenericNode node){
		if(node == null) return null;
		IGenericNode parent = node.getParent();
		if(parent == null) return null;
		if(parent instanceof MethodNode) return (MethodNode)parent;
		return getMethodAncestor(parent);
	}

	public static boolean validateTestSuiteName(String newName) {
		if(newName.length() < 1 || newName.length() > 64) return false;
		if(newName.matches("[ ]+.*")) return false;
		return true;
	}

	public static boolean classExists(RootNode model, String qualifiedName) {
		for(ClassNode node : model.getClasses()){
			if (node.getQualifiedName().equals(qualifiedName)){
				return true;
			}
		}
		return false;
	}

	public static ClassNode generateClassModel(IType type){
		ClassNode classNode = new ClassNode(type.getFullyQualifiedName());
		try{
			for(IMethod method : type.getMethods()){
				IAnnotation[] annotations = method.getAnnotations();
				for(IAnnotation annotation : annotations){
					if(annotation.getElementName().equals("Test")){
						MethodNode methodModel = generateMethodModel(method);
						if(methodModel != null){
							classNode.addMethod(methodModel);
						}
						break;
					}
				}
			}
		}
		catch(JavaModelException e){
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
	public static ArrayList<MethodNode> getObsoleteMethods(ClassNode classNode, String qualifiedTypeName){
		ArrayList<MethodNode> empty = new ArrayList<MethodNode>();
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
	public static ArrayList<MethodNode> getNotContainedMethods(ClassNode classNode, String qualifiedTypeName){
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
	public static ArrayList<MethodNode> getCompatibleMethods(MethodNode method) {
		ClassNode parent = (ClassNode)method.getParent();
		ArrayList<MethodNode> potentialMatches = getNotContainedMethods(parent, parent.getQualifiedName());
		ArrayList<MethodNode> matches = new ArrayList<MethodNode>();
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
	private static ArrayList<MethodNode> diff(ArrayList<MethodNode> v1, ArrayList<MethodNode> v2){
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
	
	private static MethodNode generateMethodModel(IMethod method) throws JavaModelException {
		MethodNode methodNode = new MethodNode(method.getElementName());
		for(ILocalVariable parameter : method.getParameters()){
			CategoryNode parameterModel = generateCategoryModel(parameter);
			if(parameterModel == null){
				return null;
			}
			methodNode.addCategory(parameterModel);
		}
		return methodNode;
	}

	private static CategoryNode generateCategoryModel(ILocalVariable parameter) {
		String type = getTypeName(parameter.getTypeSignature());
		boolean expected = false;
		if(type.equals(Constants.TYPE_NAME_UNSUPPORTED)){
			return null;
		}
		IAnnotation[] annotations;
		try {
			annotations = parameter.getAnnotations();
			for(IAnnotation annotation : annotations){
				if(annotation.getElementName().equals("expected")){
					expected = true;
				}
			}
			
			CategoryNode category;
			if(!expected){
				category = new CategoryNode(parameter.getElementName(), type);
				ArrayList<PartitionNode> defaultPartitions = generateDefaultPartitions(type);
				for(PartitionNode partition : defaultPartitions){
					category.addPartition(partition);
				}
			}
			else{
				category = new ExpectedValueCategoryNode(parameter.getElementName(), type, getDefaultExpectedValue(type));
			}
			return category;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getDefaultExpectedValue(String type) {
		switch(type){
		case Constants.TYPE_NAME_BYTE:
			return Constants.DEFAULT_EXPECTED_BYTE_VALUE;
		case Constants.TYPE_NAME_BOOLEAN:
			return Constants.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		case Constants.TYPE_NAME_CHAR:
			return Constants.DEFAULT_EXPECTED_CHAR_VALUE;
		case Constants.TYPE_NAME_DOUBLE:
			return Constants.DEFAULT_EXPECTED_DOUBLE_VALUE;
		case Constants.TYPE_NAME_FLOAT:
			return Constants.DEFAULT_EXPECTED_FLOAT_VALUE;
		case Constants.TYPE_NAME_INT:
			return Constants.DEFAULT_EXPECTED_INT_VALUE;
		case Constants.TYPE_NAME_LONG:
			return Constants.DEFAULT_EXPECTED_LONG_VALUE;
		case Constants.TYPE_NAME_SHORT:
			return Constants.DEFAULT_EXPECTED_SHORT_VALUE;
		case Constants.TYPE_NAME_STRING:
			return Constants.DEFAULT_EXPECTED_STRING_VALUE;
		}
		return null;
	}


	private static String getTypeName(String typeSignature) {
		switch(typeSignature){
		case Signature.SIG_BOOLEAN:
			return Constants.TYPE_NAME_BOOLEAN;
		case Signature.SIG_BYTE:
			return Constants.TYPE_NAME_BYTE;
		case Signature.SIG_CHAR:
			return Constants.TYPE_NAME_CHAR;
		case Signature.SIG_DOUBLE:
			return Constants.TYPE_NAME_DOUBLE;
		case Signature.SIG_FLOAT:
			return Constants.TYPE_NAME_FLOAT;
		case Signature.SIG_INT:
			return Constants.TYPE_NAME_INT;
		case Signature.SIG_LONG:
			return Constants.TYPE_NAME_LONG;
		case Signature.SIG_SHORT:
			return Constants.TYPE_NAME_SHORT;
		case "QString;":
			return Constants.TYPE_NAME_STRING;
		default:
			return Constants.TYPE_NAME_UNSUPPORTED;
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
			return new ArrayList<PartitionNode>();
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
}
