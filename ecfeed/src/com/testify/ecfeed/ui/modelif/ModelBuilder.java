package com.testify.ecfeed.ui.modelif;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.utils.ClassUtils;
import com.testify.ecfeed.ui.modelif.Constants;

public class ModelBuilder {
	public ClassNode generateClassModel(IType type, boolean testOnly) throws ModelIfException{
		ClassNode classNode = new ClassNode(type.getFullyQualifiedName());
		try{
			Class<?> testClass = ClassUtils.loadClass(ClassUtils.getClassLoader(true, null), type.getFullyQualifiedName());
			for(IMethod method : type.getMethods()){
				if((testOnly && isAnnotated(method, "Test")) || (!testOnly && isPublicVoid(method))){
					try{
						MethodNode methodModel = generateMethodModel(method, testClass);
						if(methodModel != null){
							classNode.addMethod(methodModel);
						}
					} catch(Throwable e){
						//Let the show go on
						// throw new ModelIfException(Messages.METHOD_IMPORT_EXCEPTION(method.getElementName()));
					}
				}
			}
		}
		catch(Throwable e){
			throw new ModelIfException(Messages.CLASS_IMPORT_EXCEPTION(type.getElementName()));
		}
		return classNode;
	}
	
	public MethodNode generateMethodModel(IMethod method, Class<?> testClass) throws JavaModelException {
		MethodNode methodNode = new MethodNode(method.getElementName());
		for(ILocalVariable parameter : method.getParameters()){
			methodNode.addCategory(generateCategoryModel(parameter, getTypeName(parameter, method, testClass), isExpected(parameter)));
		}
		return methodNode;
	}
	
	public CategoryNode generateCategoryModel(ILocalVariable parameter, String type, boolean expected){
		CategoryNode category = new CategoryNode(parameter.getElementName(), type, expected);
		if(expected){
			category.setDefaultValueString(getDefaultExpectedValueString(type));
			return category;
		} else{
			ArrayList<PartitionNode> defaultPartitions = generateDefaultPartitions(type);
			for(PartitionNode partition : defaultPartitions){
				category.addPartition(partition);
			}
			return category;
		}
	}

	public ArrayList<PartitionNode> generateDefaultPartitions(String typeSignature) {
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
		Class<?> typeClass = ClassUtils.loadClass(ClassUtils.getClassLoader(true, null), typeName);
		if (typeClass != null) {
			for (Object object: typeClass.getEnumConstants()) {
				partitions.add(new PartitionNode(object.toString(), ((Enum<?>)object).name()));
			}
		}
		return partitions;
	}

	private boolean isExpected(ILocalVariable parameter) throws JavaModelException {
		IAnnotation[] annotations = parameter.getAnnotations();
		for(IAnnotation annotation : annotations){
			if(annotation.getElementName().equals("expected")){
				return true;
			}
		}
		return false;
	}

	private boolean isAnnotated(IMethod method, String name) throws JavaModelException{
		IAnnotation[] annotations = method.getAnnotations();
		for(IAnnotation annotation : annotations){
			if(annotation.getElementName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isPublicVoid(IMethod method) throws JavaModelException{
		return (method.getReturnType().equals(Signature.SIG_VOID) && Flags.isPublic(method.getFlags()));
	}

	private String getDefaultExpectedValueString(String type) {
		switch(type){
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BYTE:
			return Constants.DEFAULT_EXPECTED_BYTE_VALUE;
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BOOLEAN:
			return Constants.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_CHAR:
			return Constants.DEFAULT_EXPECTED_CHAR_VALUE;
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_DOUBLE:
			return Constants.DEFAULT_EXPECTED_DOUBLE_VALUE;
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_FLOAT:
			return Constants.DEFAULT_EXPECTED_FLOAT_VALUE;
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_INT:
			return Constants.DEFAULT_EXPECTED_INT_VALUE;
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_LONG:
			return Constants.DEFAULT_EXPECTED_LONG_VALUE;
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_SHORT:
			return Constants.DEFAULT_EXPECTED_SHORT_VALUE;
		case com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_STRING:
			return Constants.DEFAULT_EXPECTED_STRING_VALUE;
		default:
			return ClassUtils.defaultEnumExpectedValueString(type);
		}
	}

	private String getTypeName(ILocalVariable parameter, IMethod method, Class<?> testClass) {
		String typeSignature = parameter.getTypeSignature();
		switch(typeSignature){
		case Signature.SIG_BOOLEAN:
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BOOLEAN;
		case Signature.SIG_BYTE:
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BYTE;
		case Signature.SIG_CHAR:
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_CHAR;
		case Signature.SIG_DOUBLE:
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_DOUBLE;
		case Signature.SIG_FLOAT:
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_FLOAT;
		case Signature.SIG_INT:
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_INT;
		case Signature.SIG_LONG:
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_LONG;
		case Signature.SIG_SHORT:
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_SHORT;
		case "QString;":
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_STRING;
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
			return com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_UNSUPPORTED;
		}
	}

	private ArrayList<PartitionNode> defaultBooleanPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedBooleanValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	private HashMap<String, String> predefinedBooleanValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("true", Constants.BOOLEAN_TRUE_STRING_REPRESENTATION);
		values.put("false", Constants.BOOLEAN_FALSE_STRING_REPRESENTATION);
		return values;
	}

	private ArrayList<PartitionNode> defaultIntegerPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedIntegerValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	private HashMap<String, String> predefinedIntegerValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		return values;
	}

	private ArrayList<PartitionNode> defaultFloatPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedFloatValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	private HashMap<String, String> predefinedFloatValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		values.put("positive infinity", Constants.POSITIVE_INFINITY_STRING_REPRESENTATION);
		values.put("negative infinity", Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION);
		return values;
	}

	private ArrayList<PartitionNode> defaultStringPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedStringValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	private HashMap<String, String> predefinedStringValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("null", Constants.NULL_VALUE_STRING_REPRESENTATION);
		return values;
	}
	
}
