package com.testify.ecfeed.modelif.java;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class JavaUtils {
	
	public static boolean isValidTypeName(String name){
		if(isPrimitive(name)) return true;
		if(name.matches(Constants.REGEX_CLASS_NODE_NAME) == false) return false;
		for(String keyword : javaKeywords()){
			if(name.contains(keyword)) return false;
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

	public static boolean isValidTestCaseName(String name) {
		return name.matches(Constants.REGEX_TEST_CASE_NODE_NAME);
	}

	public static boolean isValidConstraintName(String name) {
		return name.matches(Constants.REGEX_CONSTRAINT_NODE_NAME);
	}
	
	public static boolean hasLimitedValuesSet(String type){
		return isPrimitive(type) || type.equals(getBooleanTypeName());
	}
}
