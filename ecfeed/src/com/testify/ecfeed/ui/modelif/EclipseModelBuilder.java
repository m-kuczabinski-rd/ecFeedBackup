package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.IField;
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
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.utils.ClassUtils;

public class EclipseModelBuilder {
	
	public ClassNode buildClassModel(String qualifiedName, boolean testOnly) throws ModelIfException{
		IType type = JavaModelUtils.getIType(qualifiedName);
		if(type != null){
			return buildClassModel(type, testOnly);
		}
		throw new ModelIfException(Messages.EXCEPTION_TYPE_DOES_NOT_EXIST_IN_THE_PROJECT);
	}
	
	
	public ClassNode buildClassModel(IType type, boolean testOnly) throws ModelIfException{
		try{
			String qualifiedName = type.getFullyQualifiedName();
			ClassNode classNode = new ClassNode(qualifiedName);
			for(IMethod method : type.getMethods()){
				if((testOnly && JavaModelUtils.isAnnotated(method, "Test")) || (!testOnly)){
					if(hasSupportedParameters(method) && JavaModelUtils.isPublicVoid(method)){
						try{
							MethodNode methodModel = buildMethodModel(method);
							if(methodModel != null){
								classNode.addMethod(methodModel);
							}
						} catch(Throwable e){}
					}
				}
			}
			return classNode;
		}
		catch(Throwable e){
			throw new ModelIfException(Messages.EXCEPTION_CLASS_IMPORT(type.getElementName()));
		}
	}
	
	public MethodNode buildMethodModel(IMethod method) throws JavaModelException {
		MethodNode methodNode = new MethodNode(method.getElementName());
		for(ILocalVariable parameter : method.getParameters()){
			String typeName = getTypeName(method, parameter);
			boolean expected = JavaModelUtils.isExpected(parameter);
			methodNode.addCategory(buildCategoryModel(parameter.getElementName(), typeName, expected));
		}
		return methodNode;
	}
	
	public CategoryNode buildCategoryModel(String name, String type, boolean expected){
		CategoryNode category = new CategoryNode(name, type, expected);
		if(!expected){
			List<PartitionNode> defaultPartitions = defaultPartitions(type);
			for(PartitionNode partition : defaultPartitions){
				category.addPartition(partition);
			}
		}
		category.setDefaultValueString(getDefaultExpectedValueString(type));
		return category;
	}

	public List<PartitionNode> defaultPartitions(String typeSignature) {
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
			return defaultUserTypePartitions(typeSignature);
		}
	}

	
	protected List<PartitionNode> defaultUserTypePartitions(String typeName) {
		IType type = JavaModelUtils.getIType(typeName);
		try {
			if(type.isEnum()){
				return defaultEnumPartitions(type);
			}
		} catch (JavaModelException e) {}
		return new ArrayList<PartitionNode>();
	}

	protected List<PartitionNode> defaultEnumPartitions(IType type) {
		List<PartitionNode> partitions = new ArrayList<PartitionNode>();
		String typeSignature = Signature.createTypeSignature(type.getElementName(), false);
		try {
			if(type.isEnum()){
				for(IField field : type.getFields()){
					if(field.getTypeSignature().equals(typeSignature)){
						String value = field.getElementName();
						String name = value.toLowerCase().replaceAll("_", " ");
						partitions.add(new PartitionNode(name, value));
					}
				}
			}
		} catch (JavaModelException e) {}
		return partitions;
	}

	protected String getDefaultExpectedValueString(String type) {
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

	protected ArrayList<PartitionNode> defaultBooleanPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedBooleanValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	protected HashMap<String, String> predefinedBooleanValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("true", Constants.BOOLEAN_TRUE_STRING_REPRESENTATION);
		values.put("false", Constants.BOOLEAN_FALSE_STRING_REPRESENTATION);
		return values;
	}

	protected ArrayList<PartitionNode> defaultIntegerPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedIntegerValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	protected HashMap<String, String> predefinedIntegerValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		return values;
	}

	protected ArrayList<PartitionNode> defaultFloatPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedFloatValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	protected HashMap<String, String> predefinedFloatValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("min", Constants.MIN_VALUE_STRING_REPRESENTATION);
		values.put("max", Constants.MAX_VALUE_STRING_REPRESENTATION);
		values.put("positive infinity", Constants.POSITIVE_INFINITY_STRING_REPRESENTATION);
		values.put("negative infinity", Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION);
		return values;
	}

	protected ArrayList<PartitionNode> defaultStringPartitions() {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		HashMap<String, String> values = predefinedStringValues();
		for (String key : values.keySet()) {
			partitions.add(new PartitionNode(key, values.get(key)));
		}
		return partitions;
	}

	protected HashMap<String, String> predefinedStringValues() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("null", Constants.NULL_VALUE_STRING_REPRESENTATION);
		return values;
	}


	protected String getTypeName(IMethod method, ILocalVariable parameter){
		String typeSignaure = parameter.getTypeSignature(); 
		switch(typeSignaure){
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
			return JavaModelUtils.getVariableType(method, parameter).getFullyQualifiedName().replaceAll("\\$",	"\\.");
		}
	}


	protected boolean hasSupportedParameters(IMethod method) {
		try {
			for(ILocalVariable var : method.getParameters()){
				if(JavaUtils.isPrimitive(getTypeName(method, var)) == false && JavaModelUtils.isEnumType(method, var) == false){
					return false;
				}
			}
		} catch (JavaModelException e) {
			return false;
		}
		return true;
	}
	
}
