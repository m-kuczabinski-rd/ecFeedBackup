package com.testify.ecfeed.testutils;

import static com.testify.ecfeed.model.Constants.*;

import java.util.Random;

import nl.flotsam.xeger.Xeger;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;

public class RandomModelGenerator {
	
	private Random rand = new Random();
	
	public int MAX_CLASSES = 10;
	public int MAX_METHODS = 10;
	public int MAX_CATEGORIES = 10;
	public int MAX_CONSTRAINTS = 10;
	public int MAX_PARTITIONS = 10;
	public int MAX_PARTITION_LEVELS = 5;
	public int MAX_PARTITION_LABELS = 10;
	
	public RootNode generateModel(){
		String name = generateString(REGEX_ROOT_NODE_NAME);
		
		RootNode root = new RootNode(name);
		
		for(int i = 0; i < rand.nextInt(MAX_CLASSES) + 1; i++){
			root.addClass(generateClass());
		}
		
		return root;
	}

	
	
	public ClassNode generateClass() {
		String name = generateString(REGEX_CLASS_NODE_NAME);

		ClassNode _class = new ClassNode(name);
		
		for(int i = 0; i < rand.nextInt(MAX_METHODS) + 1; i++){
			_class.addMethod(generateMethod());
		}

		return _class;
	}

	public MethodNode generateMethod(){
		String name = generateString(REGEX_METHOD_NODE_NAME);
		
		MethodNode method = new MethodNode(name);
		
		for(int i = 0; i < MAX_CATEGORIES; i++){
			method.addCategory(generateCategory());
		}
		
		for(int i = 0; i < MAX_CONSTRAINTS; i++){
			method.addConstraint(generateConstraint(method));
		}
		
		return method;
	}
	
	public CategoryNode generateCategory(){
		String name = generateString(REGEX_CATEGORY_NODE_NAME);
		String type = randomType();
		boolean expected = rand.nextInt(4) < 3 ? false : true;
		
		CategoryNode category = new CategoryNode(name, type, expected);
		category.setDefaultValueString(randomPartitionValue(type));
		
		for(int i = 0; i < rand.nextInt(MAX_PARTITIONS); i++){
			int levels = rand.nextInt(MAX_PARTITION_LEVELS);
			category.addPartition(generatePartition(levels, type));
		}
		
		return category;
	}

	public ConstraintNode generateConstraint(MethodNode method){
		String name = generateString(REGEX_CONSTRAINT_NODE_NAME);
		
		Constraint constraint = new Constraint(generatePremise(method), generateConsequence(method));
	}
	
	private BasicStatement generatePremise(MethodNode method) {
		return generateStatement(method);
	}

	private BasicStatement generateConsequence(MethodNode method) {
		return generateStatement(method);
	}



	public PartitionNode generatePartition(int levels, String type) {
		String name = generateString(REGEX_PARTITION_NODE_NAME);
		String value = randomPartitionValue(type);
		
		PartitionNode partition = new PartitionNode(name, value);
		for(int i = 0; i < MAX_PARTITION_LABELS; i++){
			partition.addLabel(generateString(REGEX_PARTITION_LABEL));
		}
		
		for(int i = 0; i < rand.nextInt(MAX_PARTITIONS); i++){
			partition.addPartition(generatePartition(levels - 1, type));
		}
		
		return partition;
	}



	private String randomType(){
		String[] types = {
				TYPE_NAME_BOOLEAN,
				TYPE_NAME_BYTE,
				TYPE_NAME_CHAR,
				TYPE_NAME_DOUBLE,
				TYPE_NAME_FLOAT,
				TYPE_NAME_INT,
				TYPE_NAME_LONG,
				TYPE_NAME_SHORT,
				TYPE_NAME_STRING
		};
		
		int typeIdx = rand.nextInt(types.length + 1);
		if(typeIdx < types.length){
			return types[typeIdx];
		}
		
		return generateString(REGEX_CATEGORY_TYPE_NAME);
	}
	
	private String randomPartitionValue(String type){
		switch(type){
		case TYPE_NAME_BOOLEAN:
			return randomBooleanValue();
		case TYPE_NAME_BYTE:
			return randomByteValue();
		case TYPE_NAME_CHAR:
			return randomCharValue();
		case TYPE_NAME_DOUBLE:
			return randomDoubleValue();
		case TYPE_NAME_FLOAT:
			return randomFloatValue();
		case TYPE_NAME_INT:
			return randomIntValue();
		case TYPE_NAME_LONG:
			return randomLongValue();
		case TYPE_NAME_SHORT:
			return randomShortValue();
		case TYPE_NAME_STRING:
			return randomStringValue();
		default:
			return randomUserTypeValue();
		}
	}
	
	private String randomBooleanValue() {
		return String.valueOf(rand.nextBoolean());
	}

	private String randomByteValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE"};
		
		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf((byte)rand.nextInt());
	}

	private String randomCharValue() {
		return String.valueOf((char)rand.nextInt());
	}

	private String randomDoubleValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE", "POSITIVE_INFINITY", "NEGATIVE_INFINITY"};
		
		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf(rand.nextDouble());
	}



	private String randomFloatValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE", "POSITIVE_INFINITY", "NEGATIVE_INFINITY"};
		
		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf(rand.nextLong());
	}

	private String randomIntValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE"};
		
		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf(rand.nextInt());
	}

	private String randomLongValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE"};
		
		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf(rand.nextLong());
	}

	private String randomShortValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE"};
		
		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}
		return String.valueOf((short)rand.nextInt());
	}

	private String randomStringValue() {
		return generateString(REGEX_STRING_TYPE_VALUE);
	}

	private String randomUserTypeValue() {
		return generateString(REGEX_USER_TYPE_VALUE);
	}



	private String generateString(String regex){
		Xeger generator = new Xeger(regex);
		return generator.generate();
	}
	
}
