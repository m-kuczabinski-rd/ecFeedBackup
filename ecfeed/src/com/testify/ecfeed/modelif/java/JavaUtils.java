package com.testify.ecfeed.modelif.java;

import java.util.Arrays;

public class JavaUtils {

	public static boolean isJavaKeyword(String word){
		return Arrays.asList(Constants.JAVA_IDENTIFIERS).contains(word);
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
}
