/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.testutils;

import static com.testify.ecfeed.modelif.java.Constants.REGEX_CATEGORY_NODE_NAME;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_CATEGORY_TYPE_NAME;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_CHAR_TYPE_VALUE;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_CLASS_NODE_NAME;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_CONSTRAINT_NODE_NAME;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_METHOD_NODE_NAME;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_PARTITION_LABEL;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_PARTITION_NODE_NAME;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_ROOT_NODE_NAME;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_STRING_TYPE_VALUE;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_TEST_CASE_NODE_NAME;
import static com.testify.ecfeed.modelif.java.Constants.REGEX_USER_TYPE_VALUE;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BOOLEAN;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BYTE;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_CHAR;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_DOUBLE;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_FLOAT;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_INT;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_LONG;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_SHORT;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_STRING;
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
	
	public int MAX_CLASSES = 3;
	public int MAX_METHODS = 3;
	public int MAX_CATEGORIES = 3;
	public int MAX_CONSTRAINTS = 3;
	public int MAX_TEST_CASES = 10;
	public int MAX_PARTITIONS = 5;
	public int MAX_PARTITION_LEVELS = 3;
	public int MAX_PARTITION_LABELS = 3;
	public int MAX_STATEMENTS = 5;
	public int MAX_STATEMENTS_DEPTH = 3;
	
	public RootNode generateModel(int classes){
		String name = generateString(REGEX_ROOT_NODE_NAME);
		
		RootNode root = new RootNode(name);
		
		for(int i = 0; i < classes; i++){
			root.addClass(generateClass(rand.nextInt(MAX_METHODS)));
		}
		
		return root;
	}

	
	
	public ClassNode generateClass(int methods) {
		String name = generateString(REGEX_CLASS_NODE_NAME);

		ClassNode _class = new ClassNode(name);
		
		for(int i = 0; i < methods; i++){
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
		
		for(int i = 0; i < testCases; i++){
			method.addTestCase(generateTestCase(method));
		}
		
		return method;
	}
	
	public CategoryNode generateCategory(String type, boolean expected, int partitionLevels, int partitions, int labels){
		String name = generateString(REGEX_CATEGORY_NODE_NAME);
		
		CategoryNode category = new CategoryNode(name, type, randomPartitionValue(type), expected);
		
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
		return generateStatement(method, MAX_STATEMENTS_DEPTH);
	}

	public BasicStatement generateStatement(MethodNode method, int maxDepth) {
		switch(rand.nextInt(5)){
		case 0:
			return generateStaticStatement();
		case 1:
		case 2:
			return generatePartitionedStatement(method);
		case 3:
		case 4:
			if(maxDepth > 0){
				return generateStatementArray(method, maxDepth); 
			}
		}
		return generateStaticStatement();
	}
	
	public StaticStatement generateStaticStatement(){
		return new StaticStatement(rand.nextBoolean());
	}

	public PartitionedCategoryStatement generatePartitionedStatement(MethodNode method) {
		List<CategoryNode> categories = new ArrayList<CategoryNode>();
		
		for(CategoryNode category : method.getCategories()){
			if(category.isExpected() == false && category.getPartitions().size() > 0){
				categories.add(category);
			}
		}
		
		if(categories.size() == 0){
			CategoryNode category = generateCategory(TYPE_NAME_INT, false, 0, 1, 1);
			method.addCategory(category);
			categories.add(category);
		}
		
		CategoryNode category = categories.get(rand.nextInt(categories.size()));
		Relation relation = rand.nextBoolean() ? Relation.EQUAL : Relation.NOT;
		if(category.getPartitions().size() == 0){
			PartitionNode partition = generatePartition(0, 0, 1, category.getType());
			category.addPartition(partition);
		}
		
		if(rand.nextBoolean()){
			List<String> partitionNames = category.getAllPartitionNames();
			String luckyPartitionName = partitionNames.get(rand.nextInt(partitionNames.size()));
			PartitionNode condition = category.getPartition(luckyPartitionName);
			return new PartitionedCategoryStatement(category, relation, condition);
		}
		else{
			if(category.getAllPartitionLabels().size() == 0){
				category.getPartitions().get(0).addLabel(generateString(REGEX_PARTITION_LABEL));
			}
			
			Set<String>labels = category.getAllPartitionLabels();
			
			String label = labels.toArray(new String[]{})[rand.nextInt(labels.size())];
			return new PartitionedCategoryStatement(category, relation, label);
		}
	}

	public ExpectedValueStatement generateExpectedValueStatement(MethodNode method) {
		List<CategoryNode> categories = new ArrayList<CategoryNode>();
		
		for(CategoryNode category : method.getCategories()){
			if(category.isExpected() == true){
				categories.add(category);
			}
		}
		
		if(categories.size() == 0){
			CategoryNode category = generateCategory(SUPPORTED_TYPES[rand.nextInt(SUPPORTED_TYPES.length)], true, 0, 1, 1);
			method.addCategory(category);
			categories.add(category);
		}
		
		CategoryNode category = categories.get(rand.nextInt(categories.size()));
		
		
		String value = randomPartitionValue(category.getType());
		String name = generateString(REGEX_PARTITION_NODE_NAME);
		return new ExpectedValueStatement(category, new PartitionNode(name, value));
	}

	public StatementArray generateStatementArray(MethodNode method, int depth) {
		StatementArray statement = new StatementArray(rand.nextBoolean()?Operator.AND:Operator.OR);
		for(int i = 0; i < MAX_STATEMENTS; i++){
			statement.addStatement(generateStatement(method, depth - 1));
		}
		return statement;
	}

	public BasicStatement generateConsequence(MethodNode method) {
		if(method.getCategories().size() == 0){
			method.addCategory(generateCategory(TYPE_NAME_INT, false, 0, 1, 1));
		}
		
		List<CategoryNode> categories = method.getCategories();
		CategoryNode category = categories.get(rand.nextInt(categories.size()));
		if(category.isExpected()){
			return generateExpectedValueStatement(method);
		}
		return generateStatement(method, MAX_STATEMENTS_DEPTH);
	}

	public PartitionNode generatePartition(int levels, int partitions, int labels, String type) {
		String name = generateString(REGEX_PARTITION_NODE_NAME);
		name = name.replaceAll(":", "_");
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
//		return "name" + id++;
		
		Xeger generator = new Xeger(regex);
		return generator.generate();
	}
	
	//DEBUG
	
	@Test
	public void testGenerateClass(){
		ClassNode _class = generateClass(5);
		System.out.println(fStringifier.stringify(_class, 0));
	}
	
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
		MethodNode m = generateMethod(5, 5, 5);
		System.out.println(fStringifier.stringify(m, 0));
	}
	
//	@Test
	public void testTestCaseGenerator(){
		MethodNode m = generateMethod(5, 0, 0);
		TestCaseNode tc = generateTestCase(m);
		System.out.println(fStringifier.stringify(m, 0));
		System.out.println(fStringifier.stringify(tc, 0));
	}
	
//	@Test
	public void testGenerateConstraint(){
		MethodNode m = generateMethod(10, 0, 0);
		ConstraintNode c = generateConstraint(m);
		System.out.println(fStringifier.stringify(c, 2));
	}
	
//	@Test
	public void testGenerateStaticStatement(){
		for(int i = 0; i < 10; i++){
			StaticStatement statement = generateStaticStatement();
			System.out.println(fStringifier.stringify(statement, 0));
		}
	}
	
//	@Test
	public void testGeneratePartitionedStatement(){
		for(int i = 0; i < 10; i++){
			MethodNode m = generateMethod(10, 0, 0);
			PartitionedCategoryStatement statement = generatePartitionedStatement(m);
			System.out.println(fStringifier.stringify(statement, 0));
		}
	}
	
//	@Test
	public void testGenerateExpectedValueStatement(){
		for(int i = 0; i < 10; i++){
			MethodNode m = generateMethod(10, 0, 0);
			ExpectedValueStatement statement = generateExpectedValueStatement(m);
			System.out.println(fStringifier.stringify(statement, 0));
		}
	}
	
//	@Test
	public void testGenerateStatementArray(){
		for(int i = 0; i < 10; i++){
			MethodNode m = generateMethod(10, 0, 0);
			StatementArray statement = generateStatementArray(m, 3);
			System.out.println(fStringifier.stringify(statement, 0));
		}
	}
}
