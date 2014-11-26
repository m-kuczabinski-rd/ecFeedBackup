package com.testify.ecfeed.adapter.java;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import com.testify.ecfeed.adapter.operations.Messages;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;

public class JavaUtils {

	public static boolean isValidTypeName(String name){
		if(name == null) return false;
		if(isPrimitive(name)) return true;
		if(name.matches(Constants.REGEX_CLASS_NODE_NAME) == false) return false;
		StringTokenizer tokenizer = new StringTokenizer(name, ".");
		while(tokenizer.hasMoreTokens()){
			String segment = tokenizer.nextToken();
			if(isValidJavaIdentifier(segment) == false){
				return false;
			}
		}
		return true;
	}

	public static boolean isJavaKeyword(String word){
		return Arrays.asList(Constants.JAVA_KEYWORDS).contains(word);
	}

	public static String[] javaKeywords(){
		return Constants.JAVA_KEYWORDS;
	}

	public static boolean isPrimitive(String typeName){
		return Arrays.asList(Constants.SUPPORTED_PRIMITIVE_TYPES).contains(typeName);
	}

	public static boolean isUserType(String typeName){
		return isPrimitive(typeName) == false;
	}

	public static boolean isString(String typeName){
		return typeName.equals(Constants.TYPE_NAME_STRING);
	}

	public static boolean isBoolean(String typeName){
		return typeName.equals(Constants.TYPE_NAME_BOOLEAN);
	}

	public static String getTypeName(String cannonicalName) {
		if(cannonicalName.equals(boolean.class.getName())){
			return Constants.TYPE_NAME_BOOLEAN;
		}
		if(cannonicalName.equals(byte.class.getName())){
			return Constants.TYPE_NAME_BYTE;
		}
		if(cannonicalName.equals(char.class.getName())){
			return Constants.TYPE_NAME_CHAR;
		}
		if(cannonicalName.equals(double.class.getName())){
			return Constants.TYPE_NAME_DOUBLE;
		}
		if(cannonicalName.equals(float.class.getName())){
			return Constants.TYPE_NAME_FLOAT;
		}
		if(cannonicalName.equals(int.class.getName())){
			return Constants.TYPE_NAME_INT;
		}
		if(cannonicalName.equals(long.class.getName())){
			return Constants.TYPE_NAME_LONG;
		}
		if(cannonicalName.equals(short.class.getName())){
			return Constants.TYPE_NAME_SHORT;
		}
		if(cannonicalName.equals(String.class.getName())){
			return Constants.TYPE_NAME_STRING;
		}

		return cannonicalName;
	}

	public static String consolidate(Collection<String> strings){
		String consolidated = "";
		for(String string : strings){
			consolidated += string + "\n";
		}
		return consolidated;
	}

	public static List<String> enumValuesNames(URLClassLoader loader, String enumTypeName){
		List<String> values = new ArrayList<String>();
		try {
			Class<?> enumType = loader.loadClass(enumTypeName);
			if(enumType != null && enumType.isEnum()){
				for (Object object: enumType.getEnumConstants()) {
					values.add(((Enum<?>)object).name());
				}
			}
		} catch (ClassNotFoundException e) {
		}
		return values;
	}

	public static String[] supportedPrimitiveTypes(){
		return Constants.SUPPORTED_PRIMITIVE_TYPES;
	}

	public static boolean isValidJavaIdentifier(String value) {
		return (value.matches(Constants.REGEX_JAVA_IDENTIFIER) && isJavaKeyword(value) == false);
	}

	public static String getBooleanTypeName(){
		return Constants.TYPE_NAME_BOOLEAN;
	}

	public static String getStringTypeName(){
		return Constants.TYPE_NAME_STRING;
	}

	public static boolean isValidTestCaseName(String name) {
		return name.matches(Constants.REGEX_TEST_CASE_NODE_NAME);
	}

	public static boolean isValidConstraintName(String name) {
		return name.matches(Constants.REGEX_CONSTRAINT_NODE_NAME);
	}

	public static boolean hasLimitedValuesSet(String type){
		return isPrimitive(type) == false || type.equals(getBooleanTypeName());
	}

	public static boolean validateTestCaseName(String name){
		return name.matches(Constants.REGEX_TEST_CASE_NODE_NAME);
	}

	public static List<String> getArgNames(MethodNode method) {
		List<String> result = new ArrayList<String>();
		for(AbstractParameterNode parameter : method.getParameters()){
			result.add(parameter.getName());
		}
		return result;
	}

	public static List<String> getArgTypes(MethodNode method) {
		List<String> result = new ArrayList<String>();
		for(AbstractParameterNode parameter : method.getParameters()){
			result.add(parameter.getType());
		}
		return result;
	}

	public static boolean validateMethodName(String name) {
		return validateMethodName(name, null);
	}

	public static boolean validateMethodName(String name, List<String> problems) {
		boolean valid = name.matches(Constants.REGEX_METHOD_NODE_NAME);
		valid &= Arrays.asList(Constants.JAVA_KEYWORDS).contains(name) == false;
		if(valid == false){
			if(problems != null){
				problems.add(Messages.METHOD_NAME_REGEX_PROBLEM);
			}
		}
		return valid;
	}

	public static String getLocalName(String qualifiedName){
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)?qualifiedName: qualifiedName.substring(lastDotIndex + 1);
	}

	public static String getLocalName(ClassNode node){
		return getLocalName(node.getName());
	}

	public static String getPackageName(ClassNode classNode){
		return getPackageName(classNode.getName());
	}

	public static String getPackageName(String qualifiedName){
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)? "" : qualifiedName.substring(0, lastDotIndex);
	}

	public static String simplifiedToString(AbstractParameterNode parameter){
		String result = parameter.toString();
		String type = parameter.getType();
		result.replace(type, JavaUtils.getLocalName(type));
		return result;
	}

	public static String simplifiedToString(MethodNode method){
		String result = method.toString();
		for(AbstractParameterNode parameter : method.getParameters()){
			String type = parameter.getType();
			String newType = JavaUtils.getLocalName(type);
			result = result.replaceAll(type, newType);
		}
		return result;
	}

	public static String getQualifiedName(ClassNode classNode){
		return classNode.getName();
	}

	public static String getQualifiedName(String packageName, String localName){
		return packageName + "." + localName;
	}

	public static boolean validateNewMethodSignature(ClassNode parent, String methodName, List<String> argTypes){
		return validateNewMethodSignature(parent, methodName, argTypes, null);
	}

	public static boolean validateNewMethodSignature(ClassNode parent, String methodName,
			List<String> argTypes, List<String> problems){
		boolean valid = JavaUtils.validateMethodName(methodName, problems);
		if(parent.getMethod(methodName, argTypes) != null){
			valid = false;
			if(problems != null){
				problems.add(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
			}
		}
		return valid;
	}
}
