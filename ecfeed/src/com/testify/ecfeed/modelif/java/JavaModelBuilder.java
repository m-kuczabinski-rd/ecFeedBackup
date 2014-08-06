package com.testify.ecfeed.modelif.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.ui.common.LoaderProvider;

public class JavaModelBuilder {
	
	public ClassNode buildClassModel(String qualifiedName, boolean testOnly) throws ModelIfException{
		ClassNode classNode = new ClassNode(qualifiedName);
		try{
			ModelClassLoader loader = LoaderProvider.getLoader(false, null);
			Class<?> testClass = loader.loadClass(qualifiedName);
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
//			throw new ModelIfException(Messages.EXCEPTION_CLASS_IMPORT(qualifiedName));
		}
		return classNode;
	}
	
	public MethodNode buildMethodModel(Method method){
		MethodNode methodNode = new MethodNode(method.getName());
		Class<?> parameterTypes[] = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		for(int i = 0; i < parameterTypes.length; i++){
			String name = "arg" + i;
			methodNode.addCategory(buildCategoryModel(name, parameterTypes[i], isExpected(annotations[i])));
		}
		return methodNode;
	}
	
	public CategoryNode buildCategoryModel(String name, Class<?>type, boolean expected){
		String typeName = JavaUtils.getTypeName(type.getName());
		CategoryNode category = new CategoryNode(name, typeName, expected);
		category.setDefaultValueString(getDefaultExpectedValueString(typeName));
		if(!expected){
			List<PartitionNode> defaultPartitions = getDefaultPartitions(typeName);
			for(PartitionNode partition : defaultPartitions){
				category.addPartition(partition);
			}
		}
		return category;
	}

	public List<PartitionNode> getDefaultPartitions(String typeSignature) {
		switch(typeSignature){
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BOOLEAN:
			return defaultBooleanPartitions();
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BYTE:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_CHAR:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_DOUBLE:
			return defaultFloatPartitions();
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_FLOAT:
			return defaultFloatPartitions();
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_INT:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_LONG:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_SHORT:
			return defaultIntegerPartitions();
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_STRING:
			return defaultStringPartitions();
		default:
			return defaultEnumPartitions(typeSignature);
		}
	}

	public static ArrayList<PartitionNode> defaultEnumPartitions(String typeName) {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		ModelClassLoader loader = LoaderProvider.getLoader(false, null);
		Class<?> typeClass = loader.loadClass(typeName);
		if (typeClass != null) {
			for (Object object: typeClass.getEnumConstants()) {
				partitions.add(new PartitionNode(object.toString(), ((Enum<?>)object).name()));
			}
		}
		return partitions;
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

	protected static String defaultEnumExpectedValueString(String typeName) {
		String value = "VALUE";
		List<String> values = JavaUtils.enumValuesNames(LoaderProvider.getLoader(false, null), typeName);
		if(values.size() > 0){
			value = values.get(0);
		}
		return value;
	}

	protected List<PartitionNode> defaultBooleanPartitions() {
		List<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedBooleanValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	protected HashMap<String, String> predefinedBooleanValues() {
		HashMap<String, String> values = new HashMap<String, String>();
//		values.put("true", Constants.BOOLEAN_TRUE_STRING_REPRESENTATION);
//		values.put("false", Constants.BOOLEAN_FALSE_STRING_REPRESENTATION);
		return values;
	}

	protected List<PartitionNode> defaultIntegerPartitions() {
		List<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedIntegerValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	protected HashMap<String, String> predefinedIntegerValues() {
		HashMap<String, String> values = new HashMap<String, String>();
//		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
//		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		return values;
	}

	protected List<PartitionNode> defaultFloatPartitions() {
		List<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedFloatValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	protected HashMap<String, String> predefinedFloatValues() {
		HashMap<String, String> values = new HashMap<String, String>();
//		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
//		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
//		values.put("positive infinity", Constants.POSITIVE_INFINITY_STRING_REPRESENTATION);
//		values.put("negative infinity", Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION);
		return values;
	}

	protected List<PartitionNode> defaultStringPartitions() {
		List<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedStringValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
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

	private boolean isAnnotated(Method method, String name) throws JavaModelException{
		Annotation[] annotations = method.getAnnotations();
		for(Annotation annotation : annotations){
			if(annotation.annotationType().getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	private boolean isPublicVoid(Method method) throws JavaModelException{
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
