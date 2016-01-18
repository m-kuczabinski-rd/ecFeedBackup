/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.adapter.java;

import com.testify.ecfeed.core.model.ChoiceNode;

public class ChoiceValueParser {
	private ModelClassLoader fLoader;

	public ChoiceValueParser(ModelClassLoader loader){
		fLoader = loader;
	}

	public Object parseValue(ChoiceNode choice){
		if(choice.getParameter() != null){
			return parseValue(choice.getValueString(), choice.getParameter().getType());
		}
		return null;
	}

	public Object parseValue(String valueString, String typeName){
		if(typeName == null || valueString == null){
			return null;
		}
		switch(typeName){
		case Constants.TYPE_NAME_BOOLEAN:
			return parseBooleanValue(valueString);
		case Constants.TYPE_NAME_BYTE:
			return parseByteValue(valueString);
		case Constants.TYPE_NAME_CHAR:
			return parseCharValue(valueString);
		case Constants.TYPE_NAME_DOUBLE:
			return parseDoubleValue(valueString);
		case Constants.TYPE_NAME_FLOAT:
			return parseFloatValue(valueString);
		case Constants.TYPE_NAME_INT:
			return parseIntValue(valueString);
		case Constants.TYPE_NAME_LONG:
			return parseLongValue(valueString);
		case Constants.TYPE_NAME_SHORT:
			return parseShortValue(valueString);
		case Constants.TYPE_NAME_STRING:
			return parseStringValue(valueString);
		default:
			return parseUserTypeValue(valueString, typeName);
		}
	}

	private Object parseBooleanValue(String valueString) {
		if(valueString.toLowerCase().equals(Constants.VALUE_REPRESENTATION_TRUE.toLowerCase())){
			return true;
		}
		if(valueString.toLowerCase().equals(Constants.VALUE_REPRESENTATION_FALSE.toLowerCase())){
			return false;
		}
		return null;
	}

	private Object parseByteValue(String valueString) {
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MAX)){
			return Byte.MAX_VALUE;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MIN)){
			return Byte.MIN_VALUE;
		}
		try{
			return Byte.parseByte(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private Object parseCharValue(String valueString) {
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MAX)){
			return Character.MAX_VALUE;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MIN)){
			return Character.MIN_VALUE;
		}
		if (valueString.charAt(0) == '\\') {
			return new Character((char)Integer.parseInt(valueString.substring(1)));
		} else if (valueString.length() == 1) {
			return valueString.charAt(0);
		}
		return null;

	}

	private Object parseDoubleValue(String valueString) {
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MAX)){
			return Double.MAX_VALUE;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MIN)){
			return Double.MIN_VALUE;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_POSITIVE_INF)){
			return Double.POSITIVE_INFINITY;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_NEGATIVE_INF)){
			return Double.NEGATIVE_INFINITY;
		}
		try{
			return Double.parseDouble(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private Object parseFloatValue(String valueString) {
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MAX)){
			return Float.MAX_VALUE;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MIN)){
			return Float.MIN_VALUE;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_POSITIVE_INF)){
			return Float.POSITIVE_INFINITY;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_NEGATIVE_INF)){
			return Float.NEGATIVE_INFINITY;
		}
		try{
			return Float.parseFloat(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private Object parseIntValue(String valueString) {
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MAX)){
			return Integer.MAX_VALUE;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MIN)){
			return Integer.MIN_VALUE;
		}
		try{
			return Integer.parseInt(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private Object parseLongValue(String valueString) {
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MAX)){
			return Long.MAX_VALUE;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MIN)){
			return Long.MIN_VALUE;
		}
		try{
			return Long.parseLong(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private Object parseShortValue(String valueString) {
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MAX)){
			return Short.MAX_VALUE;
		}
		if(valueString.equals(Constants.VALUE_REPRESENTATION_MIN)){
			return Short.MIN_VALUE;
		}
		try{
			return Short.parseShort(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private Object parseStringValue(String valueString) {
		if(valueString.equals(Constants.VALUE_REPRESENTATION_NULL)){
			return null;
		}
		return valueString;
	}

	private Object parseUserTypeValue(String valueString, String typeName) {
		Object value = null;
		Class<?> typeClass = fLoader.loadClass(typeName);
		if (typeClass != null) {
			for (Object object: typeClass.getEnumConstants()) {
				if ((((Enum<?>)object).name()).equals(valueString)) {
					value = object;
					break;
				}
			}
		}
		return value;
	}


}
