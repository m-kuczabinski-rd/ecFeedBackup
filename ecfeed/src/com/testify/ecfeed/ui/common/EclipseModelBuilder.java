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

package com.testify.ecfeed.ui.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.testify.ecfeed.core.adapter.ModelOperationException;
import com.testify.ecfeed.core.adapter.java.JavaUtils;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.utils.SystemLogger;

public class EclipseModelBuilder extends JavaModelAnalyser{

	public ClassNode buildClassModel(String qualifiedName, boolean testOnly) throws ModelOperationException{
		IType type = getIType(qualifiedName);
		
		if(type == null) {
			ModelOperationException.report(Messages.EXCEPTION_TYPE_DOES_NOT_EXIST_IN_THE_PROJECT);
		}
		return buildClassModel(type, testOnly);
	}


	public ClassNode buildClassModel(IType type, boolean testOnly) throws ModelOperationException{
		try{
			String qualifiedName = type.getFullyQualifiedName();
			ClassNode classNode = new ClassNode(qualifiedName);
			for(IMethod method : type.getMethods()){
				if((testOnly && isAnnotated(method, "Test")) || (!testOnly)){
					if(hasSupportedParameters(method) && isPublicVoid(method)){
						try{
							MethodNode methodModel = buildMethodModel(method);
							if(methodModel != null){
								classNode.addMethod(methodModel);
							}
						} catch(Throwable e){SystemLogger.logCatch(e.getMessage());}
					}
				}
			}
			return classNode;
		}
		catch(Throwable e){
			ModelOperationException.report(Messages.EXCEPTION_CLASS_IMPORT(type.getElementName()));
			return null;
		}
	}

	public MethodNode buildMethodModel(IMethod method) throws JavaModelException {
		MethodNode methodNode = new MethodNode(method.getElementName());
		for(ILocalVariable parameter : method.getParameters()){
			String typeName = getTypeName(method, parameter);
			boolean expected = isAnnotated(parameter, "expected");
			methodNode.addParameter(buildParameterModel(parameter.getElementName(), typeName, expected));
		}
		return methodNode;
	}

	public MethodParameterNode buildParameterModel(String name, String type, boolean expected){
		MethodParameterNode parameter = new MethodParameterNode(name, type, getDefaultExpectedValue(type), expected);
		if(!expected){
			List<ChoiceNode> defaultChoices = defaultChoices(type);
			for(ChoiceNode choice : defaultChoices){
				parameter.addChoice(choice);
			}
		}
		return parameter;
	}

	public List<ChoiceNode> defaultChoices(String typeName) {
		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		for(String value : getSpecialValues(typeName)){
			String name = value.toLowerCase();
			name = name.replace("_", " ");
			choices.add(new ChoiceNode(name, value));
		}
		return choices;
	}

	public List<String> getSpecialValues(String typeName) {
		List<String> result = new ArrayList<String>();
		switch(typeName){
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_BOOLEAN:
			result.addAll(Arrays.asList(Constants.BOOLEAN_SPECIAL_VALUES));
			break;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_CHAR:
			result.addAll(Arrays.asList(Constants.DEFAULT_EXPECTED_CHAR_VALUE));
			break;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_BYTE:
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_INT:
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_LONG:
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_SHORT:
			result.addAll(Arrays.asList(Constants.INTEGER_SPECIAL_VALUES));
			break;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_DOUBLE:
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_FLOAT:
			result.addAll(Arrays.asList(Constants.FLOAT_SPECIAL_VALUES));
			break;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_STRING:
			result.addAll(Arrays.asList(Constants.STRING_SPECIAL_VALUES));
			break;
		default:
			result.addAll(enumValues(typeName));
			break;
		}
		return result;
	}


	public String getDefaultExpectedValue(String type) {
		switch(type){
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_BYTE:
			return Constants.DEFAULT_EXPECTED_BYTE_VALUE;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_BOOLEAN:
			return Constants.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_CHAR:
			return Constants.DEFAULT_EXPECTED_CHAR_VALUE;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_DOUBLE:
			return Constants.DEFAULT_EXPECTED_DOUBLE_VALUE;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_FLOAT:
			return Constants.DEFAULT_EXPECTED_FLOAT_VALUE;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_INT:
			return Constants.DEFAULT_EXPECTED_INT_VALUE;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_LONG:
			return Constants.DEFAULT_EXPECTED_LONG_VALUE;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_SHORT:
			return Constants.DEFAULT_EXPECTED_SHORT_VALUE;
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_STRING:
			return Constants.DEFAULT_EXPECTED_STRING_VALUE;
		default:
			return defaultEnumExpectedValue(type);
		}
	}

	protected List<String> enumValues(String typeName) {
		IType type = getIType(typeName);
		List<String> result = new ArrayList<String>();
		try {
			if(type != null && type.isEnum()){
				String typeSignature = Signature.createTypeSignature(type.getElementName(), false);
				try {
					if(type.isEnum()){
						for(IField field : type.getFields()){
							if(field.getTypeSignature().equals(typeSignature)){
								result.add(field.getElementName());
							}
						}
					}
				} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
				return result;
			}
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return new ArrayList<String>();
	}


	protected String defaultEnumExpectedValue(String type) {
		String value = Constants.DEFAULT_EXPECTED_ENUM_VALUE;

		List<String> enumValues = enumValues(type);
		if(enumValues.size() > 0){
			value = enumValues.get(0);
		}
		return value;
	}


	protected ArrayList<ChoiceNode> defaultBooleanChoices() {
		ArrayList<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		HashMap<String, String> values = predefinedBooleanValues();
		for (String key : values.keySet()) {
			choices.add(new ChoiceNode(key, values.get(key)));
		}
		return choices;
	}

	protected HashMap<String, String> predefinedBooleanValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("true", Constants.BOOLEAN_TRUE_STRING_REPRESENTATION);
		values.put("false", Constants.BOOLEAN_FALSE_STRING_REPRESENTATION);
		return values;
	}

	protected ArrayList<ChoiceNode> defaultIntegerChoices() {
		ArrayList<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		HashMap<String, String> values = predefinedIntegerValues();
		for (String key : values.keySet()) {
			choices.add(new ChoiceNode(key, values.get(key)));
		}
		return choices;
	}

	protected HashMap<String, String> predefinedIntegerValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		return values;
	}

	protected ArrayList<ChoiceNode> defaultFloatChoices() {
		ArrayList<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		HashMap<String, String> values = predefinedFloatValues();
		for (String key : values.keySet()) {
			choices.add(new ChoiceNode(key, values.get(key)));
		}
		return choices;
	}

	protected HashMap<String, String> predefinedFloatValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		values.put("positive infinity", Constants.POSITIVE_INFINITY_STRING_REPRESENTATION);
		values.put("negative infinity", Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION);
		return values;
	}

	protected ArrayList<ChoiceNode> defaultStringChoices() {
		ArrayList<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		HashMap<String, String> values = predefinedStringValues();
		for (String key : values.keySet()) {
			choices.add(new ChoiceNode(key, values.get(key)));
		}
		return choices;
	}

	protected HashMap<String, String> predefinedStringValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("null", Constants.NULL_VALUE_STRING_REPRESENTATION);
		return values;
	}

	protected boolean hasSupportedParameters(IMethod method) {
		try {
			for(ILocalVariable var : method.getParameters()){
				if(JavaUtils.isPrimitive(getTypeName(method, var)) == false && isEnumType(method, var) == false){
					return false;
				}
			}
		} catch (JavaModelException e) {
			return false;
		}
		return true;
	}

}
