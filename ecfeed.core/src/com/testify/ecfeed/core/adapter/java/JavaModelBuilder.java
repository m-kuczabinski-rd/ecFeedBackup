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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.testify.ecfeed.core.adapter.ModelOperationException;
import com.testify.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;

public class JavaModelBuilder {
	
	private ModelClassLoader fLoader;

	public JavaModelBuilder(ILoaderProvider loaderProvider){
		fLoader = loaderProvider.getLoader(true, null);
	}
	
	public ClassNode buildClassModel(String qualifiedName, boolean testOnly) throws ModelOperationException{
		ClassNode classNode = new ClassNode(qualifiedName);
		try{
			Class<?> testClass = fLoader.loadClass(qualifiedName);
			if(testClass != null){
				for(Method method : testClass.getMethods()){
					if((testOnly && isAnnotated(method, "Test") && isPublicVoid(method)) || (!testOnly && isPublicVoid(method))){
						if(hasSupportedParameterTypes(method) && !isInherited(method)){
							MethodNode methodModel = buildMethodModel(method);
							if(methodModel != null){
								classNode.addMethod(methodModel);
							}
						}
					}
				}
			}
		}
		catch(Throwable e){
			SystemLogger.logCatch(e.getMessage());
		}
		return classNode;
	}
	
	public MethodNode buildMethodModel(Method method){
		MethodNode methodNode = new MethodNode(method.getName());
		Class<?> parameterTypes[] = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		for(int i = 0; i < parameterTypes.length; i++){
			String name = "arg" + i;
			methodNode.addParameter(buildParameterModel(name, parameterTypes[i], isExpected(annotations[i])));
		}
		return methodNode;
	}
	
	public MethodParameterNode buildParameterModel(String name, Class<?>type, boolean expected){
		String typeName = JavaUtils.getTypeName(type.getName());
		String defaultValue = getDefaultExpectedValueString(typeName);
		MethodParameterNode parameter = new MethodParameterNode(name, typeName, defaultValue, expected);
		parameter.setDefaultValueString(getDefaultExpectedValueString(typeName));
		if(!expected){
			List<ChoiceNode> defaultChoices = getDefaultChoices(typeName);
			for(ChoiceNode choice : defaultChoices){
				parameter.addChoice(choice);
			}
		}
		return parameter;
	}

	public List<ChoiceNode> getDefaultChoices(String typeSignature) {
		switch(typeSignature){
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_BOOLEAN:
			return defaultBooleanChoices();
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_BYTE:
			return defaultIntegerChoices();
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_CHAR:
			return defaultIntegerChoices();
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_DOUBLE:
			return defaultFloatChoices();
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_FLOAT:
			return defaultFloatChoices();
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_INT:
			return defaultIntegerChoices();
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_LONG:
			return defaultIntegerChoices();
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_SHORT:
			return defaultIntegerChoices();
		case com.testify.ecfeed.core.adapter.java.Constants.TYPE_NAME_STRING:
			return defaultStringChoices();
		default:
			return defaultEnumChoices(typeSignature);
		}
	}

	public ArrayList<ChoiceNode> defaultEnumChoices(String typeName) {
		ArrayList<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		Class<?> typeClass = fLoader.loadClass(typeName);
		if (typeClass != null) {
			for (Object object: typeClass.getEnumConstants()) {
				choices.add(new ChoiceNode(object.toString(), ((Enum<?>)object).name()));
			}
		}
		return choices;
	}

	protected String getDefaultExpectedValueString(String type) {
		return "0";
//		switch(type){
//		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BYTE:
//			return Constants.DEFAULT_EXPECTED_BYTE_VALUE;
//		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BOOLEAN:
//			return Constants.DEFAULT_EXPECTED_BOOLEAN_VALUE;
//		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_CHAR:
//			return Constants.DEFAULT_EXPECTED_CHAR_VALUE;
//		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_DOUBLE:
//			return Constants.DEFAULT_EXPECTED_DOUBLE_VALUE;
//		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_FLOAT:
//			return Constants.DEFAULT_EXPECTED_FLOAT_VALUE;
//		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_INT:
//			return Constants.DEFAULT_EXPECTED_INT_VALUE;
//		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_LONG:
//			return Constants.DEFAULT_EXPECTED_LONG_VALUE;
//		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_SHORT:
//			return Constants.DEFAULT_EXPECTED_SHORT_VALUE;
//		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_STRING:
//			return Constants.DEFAULT_EXPECTED_STRING_VALUE;
//		default:
//			return ClassUtils.defaultEnumExpectedValueString(type);
//		}
	}

	protected String defaultEnumExpectedValueString(String typeName) {
		String value = "VALUE";
		List<String> values = JavaUtils.enumValuesNames(fLoader, typeName);
		if(values.size() > 0){
			value = values.get(0);
		}
		return value;
	}

	protected List<ChoiceNode> defaultBooleanChoices() {
		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		HashMap<String, String> values = predefinedBooleanValues();
		for (String key : values.keySet()) {
			choices.add(new ChoiceNode(key, values.get(key)));
		}
		return choices;
	}

	protected HashMap<String, String> predefinedBooleanValues() {
		HashMap<String, String> values = new HashMap<String, String>();
//		values.put("true", Constants.BOOLEAN_TRUE_STRING_REPRESENTATION);
//		values.put("false", Constants.BOOLEAN_FALSE_STRING_REPRESENTATION);
		return values;
	}

	protected List<ChoiceNode> defaultIntegerChoices() {
		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		HashMap<String, String> values = predefinedIntegerValues();
		for (String key : values.keySet()) {
			choices.add(new ChoiceNode(key, values.get(key)));
		}
		return choices;
	}

	protected HashMap<String, String> predefinedIntegerValues() {
		HashMap<String, String> values = new HashMap<String, String>();
//		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
//		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		return values;
	}

	protected List<ChoiceNode> defaultFloatChoices() {
		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		HashMap<String, String> values = predefinedFloatValues();
		for (String key : values.keySet()) {
			choices.add(new ChoiceNode(key, values.get(key)));
		}
		return choices;
	}

	protected HashMap<String, String> predefinedFloatValues() {
		HashMap<String, String> values = new HashMap<String, String>();
//		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
//		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
//		values.put("positive infinity", Constants.POSITIVE_INFINITY_STRING_REPRESENTATION);
//		values.put("negative infinity", Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION);
		return values;
	}

	protected List<ChoiceNode> defaultStringChoices() {
		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		HashMap<String, String> values = predefinedStringValues();
		for (String key : values.keySet()) {
			choices.add(new ChoiceNode(key, values.get(key)));
		}
		return choices;
	}

	protected HashMap<String, String> predefinedStringValues() {
		HashMap<String, String> values = new HashMap<String, String>();
//		values.put("null", Constants.NULL_VALUE_STRING_REPRESENTATION);
		return values;
	}

	private boolean isExpected(Annotation[] annotations){
		for(Annotation annotation : annotations){
			if(annotation.annotationType().getName().equals("expected")){
				return true;
			}
		}
		return false;
	}

	private boolean isAnnotated(Method method, String name) throws Exception{
		Annotation[] annotations = method.getAnnotations();
		for(Annotation annotation : annotations){
			if(annotation.annotationType().getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	private boolean isPublicVoid(Method method) throws Exception{
		return (method.getReturnType().equals(Void.TYPE) && Modifier.isPublic(method.getModifiers()));
	}

	private boolean isInherited(Method method) {
		Class<?> declaringClass = method.getDeclaringClass();
		if(declaringClass.equals(Object.class)){
			return true;
		}
		Class<?> superClass = declaringClass.getSuperclass();
		try {
			Method overridenMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
			return method.equals(overridenMethod);
		} catch (NoSuchMethodException e) {
			return false;
		} catch (SecurityException e) {
			return false;
		}
	}

	private boolean hasSupportedParameterTypes(Method method) {
		for(Class<?> type : method.getParameterTypes()){
			String typeName = JavaUtils.getTypeName(type.getCanonicalName()); 
			if(JavaUtils.isPrimitive(typeName)){
				return false;
			}
			else if(type.isEnum() == false){
				return false;
			}
		}
		return true;
	}

}
