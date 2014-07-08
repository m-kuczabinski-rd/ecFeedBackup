package com.testify.ecfeed.testutils;

import static com.testify.ecfeed.model.Constants.REGEX_CATEGORY_NODE_NAME;
import static com.testify.ecfeed.model.Constants.REGEX_CATEGORY_TYPE_NAME;
import static com.testify.ecfeed.model.Constants.REGEX_CHAR_TYPE_VALUE;
import static com.testify.ecfeed.model.Constants.REGEX_CLASS_NODE_NAME;
import static com.testify.ecfeed.model.Constants.REGEX_CONSTRAINT_NODE_NAME;
import static com.testify.ecfeed.model.Constants.REGEX_METHOD_NODE_NAME;
import static com.testify.ecfeed.model.Constants.REGEX_PARTITION_LABEL;
import static com.testify.ecfeed.model.Constants.REGEX_PARTITION_NODE_NAME;
import static com.testify.ecfeed.model.Constants.REGEX_ROOT_NODE_NAME;
import static com.testify.ecfeed.model.Constants.REGEX_STRING_TYPE_VALUE;
import static com.testify.ecfeed.model.Constants.REGEX_TEST_CASE_NODE_NAME;
import static com.testify.ecfeed.model.Constants.REGEX_USER_TYPE_VALUE;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_INT;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_LONG;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_STRING;
import static com.testify.ecfeed.testutils.Constants.SUPPORTED_TYPES;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import nl.flotsam.xeger.Xeger;

import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;

public class RandomModelGenerator {
	
	private Random rand = new Random();
	private ModelStringifier fStringifier = new ModelStringifier();
	private static int id = 0;
	
	public int MAX_CLASSES = 3;
	public int MAX_METHODS = 3;
	public int MAX_CATEGORIES = 3;
	public int MAX_CONSTRAINTS = 3;
	public int MAX_TEST_CASES = 10;
	public int MAX_PARTITIONS = 5;
	public int MAX_PARTITION_LEVELS = 3;
	public int MAX_PARTITION_LABELS = 3;
	public int MAX_STATEMENTS = 5;
	
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
			int categories = rand.nextInt(MAX_CATEGORIES);
			int constraints = rand.nextInt(MAX_CONSTRAINTS);
			int testCases = rand.nextInt(MAX_TEST_CASES);
			_class.addMethod(generateMethod(categories, constraints, testCases));
		}

		return _class;
	}

	public MethodNode generateMethod(int categories, int constraints, int testCases){
		String name = generateString(REGEX_METHOD_NODE_NAME);
		
		MethodNode method = new MethodNode(name);
		
		for(int i = 0; i < categories; i++){
			boolean expected = rand.nextInt(4) < 3 ? false : true;
			String type = randomType();
			
			method.addCategory(generateCategory(type, expected, 
					rand.nextInt(MAX_PARTITION_LEVELS), rand.nextInt(MAX_PARTITIONS) + 1, 
					rand.nextInt(MAX_PARTITION_LABELS)));
		}
		
		for(int i = 0; i < constraints; i++){
			method.addConstraint(generateConstraint(method));
		}
		
		return method;
	}
	
	public CategoryNode generateCategory(String type, boolean expected, int partitionLevels, int partitions, int labels){
		String name = generateString(REGEX_CATEGORY_NODE_NAME);
		
		CategoryNode category = new CategoryNode(name, type, expected);
		category.setDefaultValueString(randomPartitionValue(type));
		
		for(int i = 0; i < rand.nextInt(MAX_PARTITIONS) + 1; i++){
			category.addPartition(generatePartition(partitionLevels, partitions, labels, type));
		}

		return category;
	}

	public TestCaseNode generateTestCase(MethodNode method){
		String name = generateString(REGEX_TEST_CASE_NODE_NAME);
		List<PartitionNode> testData = new ArrayList<PartitionNode>();
		
		for(CategoryNode c : method.getCategories()){
			if(c.isExpected()){
				PartitionNode expectedValue = new PartitionNode("@expected", randomPartitionValue(c.getType()));
				expectedValue.setParent(c);
				testData.add(expectedValue);
			}
			else{
				List<PartitionNode> partitions = c.getPartitions();
				if(partitions.size() == 0){
					System.out.println("Empty category!");
				}
				PartitionNode p = c.getPartitions().get(rand.nextInt(partitions.size()));
				while(p.getPartitions().size() > 0){
					List<PartitionNode> ppartitions = p.getPartitions();
					p = ppartitions.get(rand.nextInt(ppartitions.size()));
				}
				testData.add(p);
			}
		}
		
		return new TestCaseNode(name, testData);
	}

	public ConstraintNode generateConstraint(MethodNode method){
		String name = generateString(REGEX_CONSTRAINT_NODE_NAME);
		
		Constraint constraint = new Constraint(generatePremise(method), generateConsequence(method));
		
		return new ConstraintNode(name, constraint);
	}
	
	public BasicStatement generatePremise(MethodNode method) {
		return generateStatement(method);
	}

	public BasicStatement generateStatement(MethodNode method) {
		switch(rand.nextInt(3)){
		case 0: return new StaticStatement(rand.nextBoolean());
		case 1: return generatePartitionedStatement(method);
		default: return generateStatementArray(method); 
		}
	}
	
	public StaticStatement generateStaticStatement(){
		return new StaticStatement(rand.nextBoolean());
	}

	public BasicStatement generatePartitionedStatement(MethodNode method) {
		List<CategoryNode> categories = new ArrayList<CategoryNode>();
		
		for(CategoryNode category : method.getCategories()){
			if(category.isExpected() == false && category.getPartitions().size() > 0){
				categories.add(category);
			}
		}
		
		if(categories.size() == 0){
			return new StaticStatement(true);
		}
		
		CategoryNode category = categories.get(categories.size());
		Relation relation = rand.nextBoolean() ? Relation.EQUAL : Relation.NOT;
		
		if(rand.nextBoolean()){
			List<String> partitionNames = category.getAllPartitionNames();
			String luckyPartitionName = partitionNames.get(rand.nextInt(partitionNames.size()));
			PartitionNode condition = category.getPartition(luckyPartitionName);
			return new PartitionedCategoryStatement(category, relation, condition);
		}
		else{
			Set<String>labels = category.getAllPartitionLabels();
			String label = labels.toArray(new String[]{})[rand.nextInt(labels.size())];
			return new PartitionedCategoryStatement(category, relation, label);
		}
	}

	public BasicStatement generateStatementArray(MethodNode method) {
		StatementArray statement = new StatementArray(rand.nextBoolean()?Operator.AND:Operator.OR);
		for(int i = 0; i < MAX_STATEMENTS; i++){
			statement.addStatement(generateStatement(method));
		}
		return statement;
	}

	public BasicStatement generateConsequence(MethodNode method) {
		List<CategoryNode> categories = method.getCategories();
		CategoryNode category = categories.get(rand.nextInt(categories.size()));
		if(category.isExpected()){
			return generateExpectedValueStatement(category);
		}
		return generateStatement(method);
	}

	public BasicStatement generateExpectedValueStatement(CategoryNode category) {
		String value = randomPartitionValue(category.getType());
		String name = generateString(REGEX_PARTITION_NODE_NAME);
		return new ExpectedValueStatement(category, new PartitionNode(name, value));
	}

	public PartitionNode generatePartition(int levels, int partitions, int labels, String type) {
		String name = generateString(REGEX_PARTITION_NODE_NAME);
		String value = randomPartitionValue(type);
		
		PartitionNode partition = new PartitionNode(name, value);
		for(int i = 0; i < labels; i++){
			String label = generateString(REGEX_PARTITION_LABEL);
			partition.addLabel(label);
		}
		
		if(levels > 0){
			for(int i = 0; i < partitions; i++){
				partition.addPartition(generatePartition(levels - 1, partitions, labels, type));
			}
		}
		
		return partition;
	}

	private String randomType(){
		
		int typeIdx = rand.nextInt(SUPPORTED_TYPES.length + 1);
		if(typeIdx < SUPPORTED_TYPES.length){
			return SUPPORTED_TYPES[typeIdx];
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
		return generateString(REGEX_CHAR_TYPE_VALUE);
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
		return "name" + id++;
		
//		Xeger generator = new Xeger(regex);
//		return generator.generate();
	}
	
	//DEBUG
//	@Test
	public void testPartitionGeneration(){
		System.out.println("Childless partitions:");
		for(String type : new String[]{"String"}){
			PartitionNode p0 = generatePartition(0, 0, 0, type);
			System.out.println(type + " partition:" + p0);
		}
		
		System.out.println("Hierarchic partitions:");
		for(String type : SUPPORTED_TYPES){
			System.out.println("Type: " + type);
			PartitionNode p1 = generatePartition(MAX_PARTITION_LEVELS, MAX_PARTITIONS, MAX_PARTITION_LABELS, type);
			System.out.println(fStringifier.stringify(p1, 0));
		}
	}
	
//	@Test
	public void testCategoryGenerator(){
		for(String type : SUPPORTED_TYPES){
			for(boolean expected : new Boolean[]{true, false}){
				System.out.println("Type: " + type);
				int partitions = rand.nextInt(MAX_PARTITIONS);
				int labels = rand.nextInt(MAX_PARTITION_LABELS);
				int levels = rand.nextInt(MAX_PARTITION_LEVELS);
				CategoryNode c = generateCategory(type, expected, levels, partitions, labels);
				System.out.println(fStringifier.stringify(c, 0));
			}
		}
	}
	
//	@Test
	public void testMethodGenerator(){
		MethodNode m = generateMethod(5, 0, 0);
		System.out.println(fStringifier.stringify(m, 0));
	}
	
//	@Test
	public void testTestCaseGenerator(){
		MethodNode m = generateMethod(5, 0, 0);
		TestCaseNode tc = generateTestCase(m);
		System.out.println(fStringifier.stringify(m, 0));
		System.out.println(fStringifier.stringify(tc, 0));
	}
	
	@Test
	public void testGenerateStaticStatement(){
		for(int i = 0; i < 10; i++){
			StaticStatement statement = generateStaticStatement();
			System.out.println(fStringifier.stringify(statement, 0));
		}
	}
}
