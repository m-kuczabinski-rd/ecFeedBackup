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
import java.util.Vector;

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
import com.testify.ecfeed.model.GenericNode;
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
		if(type.equals(Constants.STRING_TYPE_NAME)) return true;
		return (getPartitionValueFromString(valueString, type) != null);
	}

	public static Object getPartitionValueFromString(String valueString, String type){
		try{
			switch(type){
			case Constants.BOOLEAN_TYPE_NAME:
				return Boolean.valueOf(valueString).booleanValue();
			case Constants.BYTE_TYPE_NAME:
				return Byte.valueOf(valueString).byteValue();
			case Constants.CHAR_TYPE_NAME:
				if(valueString.charAt(0) != '\\' || valueString.length() == 1) return(valueString.charAt(0));
				return Character.toChars(Integer.parseInt(valueString.substring(1)));
			case Constants.DOUBLE_TYPE_NAME:
				return Double.valueOf(valueString).doubleValue();
			case Constants.FLOAT_TYPE_NAME:
				return Float.valueOf(valueString).floatValue();
			case Constants.INT_TYPE_NAME:
				return Integer.valueOf(valueString).intValue();
			case Constants.LONG_TYPE_NAME:
				return Long.valueOf(valueString).longValue();
			case Constants.SHORT_TYPE_NAME:
				return Short.valueOf(valueString).shortValue();
			case Constants.STRING_TYPE_NAME:
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
		ArrayList<TestCaseNode> toRemove = new ArrayList<TestCaseNode>();
		for(TestCaseNode testCase : testCases){
			for(PartitionNode testValue : testCase.getTestData()){
				if(testValue == partition){
					toRemove.add(testCase);
					break;
				}
			}
		}
		for(TestCaseNode testCase : toRemove){
			method.removeChild(testCase);
		}
	}

	public static MethodNode getMethodAncestor(GenericNode node){
		if(node == null) return null;
		GenericNode parent = node.getParent();
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
						classNode.addMethod(generateMethodModel(method));
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
	public static Vector<MethodNode> getObsoleteMethods(ClassNode classNode, String qualifiedTypeName){
		Vector<MethodNode> empty = new Vector<MethodNode>();
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
	public static Vector<MethodNode> getNotContainedMethods(ClassNode classNode, String qualifiedTypeName){
		Vector<MethodNode> empty = new Vector<MethodNode>();
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
	public static Vector<MethodNode> getCompatibleMethods(MethodNode method) {
		ClassNode parent = (ClassNode)method.getParent();
		Vector<MethodNode> potentialMatches = getNotContainedMethods(parent, parent.getQualifiedName());
		Vector<MethodNode> matches = new Vector<MethodNode>();
		for(MethodNode potentialMatch : potentialMatches){
			if (potentialMatch.getParameterTypes().equals(method.getParameterTypes())){
				matches.add(potentialMatch);
			}
		}
		return matches;
	}

	/**
	 * Returns elements in v1 that are not mentioned in v2 by checking toString() value; 
	 */
	private static Vector<MethodNode> diff(Vector<MethodNode> v1, Vector<MethodNode> v2){
		Vector<MethodNode> diff = new Vector<MethodNode>();
		
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
			methodNode.addCategory(generateCategoryModel(parameter));
		}
		return methodNode;
	}

	private static CategoryNode generateCategoryModel(ILocalVariable parameter) {
		String type = getTypeName(parameter.getTypeSignature());
		CategoryNode category = new CategoryNode(parameter.getElementName(), type);
		Vector<PartitionNode> defaultPartitions = generateDefaultPartitions(type);
		for(PartitionNode partition : defaultPartitions){
			category.addPartition(partition);
		}
		return category;
	}

	private static String getTypeName(String typeSignature) {
		switch(typeSignature){
		case Signature.SIG_BOOLEAN:
			return "boolean";
		case Signature.SIG_BYTE:
			return "byte";
		case Signature.SIG_CHAR:
			return "char";
		case Signature.SIG_DOUBLE:
			return "double";
		case Signature.SIG_FLOAT:
			return "float";
		case Signature.SIG_INT:
			return "int";
		case Signature.SIG_LONG:
			return "long";
		case Signature.SIG_SHORT:
			return "short";
		case "QString;":
			return "String";
		default:
			return "unsupported";
		}
	}

	private static Vector<PartitionNode> generateDefaultPartitions(String typeSignature) {
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
			return new Vector<PartitionNode>();
		}
	}

	private static Vector<PartitionNode> defaultBooleanPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("true", true));
		partitions.add(new PartitionNode("false", false));	
		return partitions;
	}

	private static Vector<PartitionNode> defaultBytePartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Byte.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (byte)-1));	
		partitions.add(new PartitionNode("zero", (byte)0));
		partitions.add(new PartitionNode("positive", (byte)1));	
		partitions.add(new PartitionNode("max", Byte.MAX_VALUE));
		return partitions;
	}

	private static Vector<PartitionNode> defaultCharacterPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("zero", '\u0000'));
		partitions.add(new PartitionNode("a", 'a'));
		partitions.add(new PartitionNode("z", 'z'));
		partitions.add(new PartitionNode("A", 'A'));
		partitions.add(new PartitionNode("Z", 'Z'));
		partitions.add(new PartitionNode("non ASCII", '\u00A7'));
		partitions.add(new PartitionNode("max", '\uffff'));
		return partitions;
	}

	private static Vector<PartitionNode> defaultDoublePartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Double.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (double)-1));	
		partitions.add(new PartitionNode("zero", (double)0));
		partitions.add(new PartitionNode("positive", (double)1));	
		partitions.add(new PartitionNode("max", Double.MAX_VALUE));
		return partitions;
	}

	private static Vector<PartitionNode> defaultFloatPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Float.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (float)-1));	
		partitions.add(new PartitionNode("zero", (float)0));
		partitions.add(new PartitionNode("positive", (float)1));	
		partitions.add(new PartitionNode("max", Float.MAX_VALUE));
		return partitions;
	}

	private static Vector<PartitionNode> defaultIntegerPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Integer.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (int)-1));	
		partitions.add(new PartitionNode("zero", (int)0));
		partitions.add(new PartitionNode("positive", (int)1));	
		partitions.add(new PartitionNode("max", Integer.MAX_VALUE));
		return partitions;
	}

	private static Vector<PartitionNode> defaultLongPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Long.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (long)-1));	
		partitions.add(new PartitionNode("zero", (long)0));
		partitions.add(new PartitionNode("positive", (long)1));	
		partitions.add(new PartitionNode("max", Long.MAX_VALUE));
		return partitions;
	}

	private static Vector<PartitionNode> defaultShortPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Short.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (short)-1));	
		partitions.add(new PartitionNode("zero", (short)0));
		partitions.add(new PartitionNode("positive", (short)1));	
		partitions.add(new PartitionNode("max", Short.MAX_VALUE));
		return partitions;
	}

	private static Vector<PartitionNode> defaultStringPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("null", null));
		partitions.add(new PartitionNode("empty", ""));
		partitions.add(new PartitionNode("lower case", "a"));
		partitions.add(new PartitionNode("upper case", "A"));
		partitions.add(new PartitionNode("mixed cases", "aA"));
		partitions.add(new PartitionNode("all latin", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		return partitions;
	}
}
