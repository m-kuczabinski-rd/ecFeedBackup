/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.testify.ecfeed.generators.RandomGenerator;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.model.BasicStatement;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.EStatementOperator;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.DecomposedParameterStatement;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.serialization.ect.Constants;
import com.testify.ecfeed.serialization.ect.EctParser;
import com.testify.ecfeed.serialization.ect.EctSerializer;

public class XmlParserSerializerTest {
	private final int TEST_RUNS = 10;

//	private final int MAX_CLASSES = 1;
//	private final int MAX_METHODS = 1;
//	private final int MAX_PARAMETERS = 3;
//	private final int MAX_EXPECTED_PARAMETERS = 3;
//	private final int MAX_PARTITIONS = 1;
//	private final int MAX_PARTITION_LEVELS = 1;
//	private final int MAX_PARTITION_LABELS = 1;
//	private final int MAX_CONSTRAINTS = 5;
//	private final int MAX_TEST_CASES = 1;

	private final int MAX_CLASSES = 5;
	private final int MAX_METHODS = 5;
	private final int MAX_PARAMETERS = 5;
//	private final int MAX_EXPECTED_PARAMETERS = 3;
	private final int MAX_PARTITIONS = 10;
	private final int MAX_PARTITION_LEVELS = 5;
	private final int MAX_PARTITION_LABELS = 5;
	private final int MAX_CONSTRAINTS = 5;
	private final int MAX_TEST_CASES = 50;

	Random rand = new Random();
	static int nextInt = 0;

	private final String[] CATEGORY_TYPES = new String[]{
			Constants.TYPE_NAME_BOOLEAN, Constants.TYPE_NAME_BYTE, Constants.TYPE_NAME_CHAR,
			Constants.TYPE_NAME_DOUBLE, Constants.TYPE_NAME_FLOAT, Constants.TYPE_NAME_INT,
			Constants.TYPE_NAME_LONG, Constants.TYPE_NAME_SHORT, Constants.TYPE_NAME_STRING
	};

	@Test
	public void test() {
		try {
		for(int i = 0; i < TEST_RUNS; ++i){
			RootNode model = createRootNode(rand.nextInt(MAX_CLASSES) + 1);
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			IModelSerializer serializer = new EctSerializer(ostream);
			IModelParser parser = new EctParser();
			serializer.serialize(model);
			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			RootNode parsedModel = parser.parseModel(istream);
			compareModels(model, parsedModel);

		}
		} catch (IOException e) {
			fail("Unexpected exception");
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parsePartitionTest(){
		try{
			RootNode root = new RootNode("root");
			ClassNode classNode = new ClassNode("classNode");
			MethodNode method = new MethodNode("method");
			ParameterNode parameter = new ParameterNode("parameter", com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_STRING, "0", false);
			ChoiceNode partition = new ChoiceNode("partition", "A                 B");
			List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
			testData.add(partition);
			TestCaseNode testCase = new TestCaseNode("test", testData);

			root.addClass(classNode);
			classNode.addMethod(method);
			method.addParameter(parameter);
			parameter.addPartition(partition);
			method.addTestCase(testCase);

			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			IModelSerializer serializer = new EctSerializer(ostream);
			IModelParser parser = new EctParser();
			serializer.serialize(root);
			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			RootNode parsedModel = parser.parseModel(istream);
			compareModels(root, parsedModel);
		}
		catch (IOException e) {
			fail("Unexpected exception");
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parseConditionStatementTest(){
		try{
			RootNode root = new RootNode("root");
			ClassNode classNode = new ClassNode("classNode");
			MethodNode method = new MethodNode("method");
			ParameterNode partitionedParameter =
					new ParameterNode("partitionedParameter", com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_STRING, "0", false);
			ParameterNode expectedParameter =
					new ParameterNode("expectedParameter", com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_CHAR, "0", true);
			expectedParameter.setDefaultValueString("d");
			ChoiceNode partition1 = new ChoiceNode("partition", "p");
			partition1.setParent(partitionedParameter);
			ChoiceNode partition2 = new ChoiceNode("expected", "s");
			partition2.setParent(expectedParameter);

			List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
			testData.add(partition1);
			testData.add(partition2);
			TestCaseNode testCase = new TestCaseNode("test", testData);
			Constraint partitionConstraint = new Constraint(new StaticStatement(true),
					new DecomposedParameterStatement(partitionedParameter, EStatementRelation.EQUAL, partition1));
			Constraint labelConstraint = new Constraint(new StaticStatement(true),
					new DecomposedParameterStatement(partitionedParameter, EStatementRelation.EQUAL, "label"));
			Constraint expectedConstraint = new Constraint(new StaticStatement(true),
					new ExpectedValueStatement(expectedParameter, new ChoiceNode("expected", "n")));
			ConstraintNode partitionConstraintNode = new ConstraintNode("partition constraint", partitionConstraint);
			ConstraintNode labelConstraintNode = new ConstraintNode("label constraint", labelConstraint);
			ConstraintNode expectedConstraintNode = new ConstraintNode("expected constraint", expectedConstraint);

			root.addClass(classNode);
			classNode.addMethod(method);
			method.addParameter(partitionedParameter);
			method.addParameter(expectedParameter);
			partitionedParameter.addPartition(partition1);
			method.addTestCase(testCase);
			method.addConstraint(labelConstraintNode);
			method.addConstraint(partitionConstraintNode);
			method.addConstraint(expectedConstraintNode);

			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			IModelSerializer serializer = new EctSerializer(ostream);
			IModelParser parser = new EctParser();
			serializer.serialize(root);
//			System.out.println(ostream.toString());
			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			RootNode parsedModel = parser.parseModel(istream);
			compareModels(root, parsedModel);
		}
		catch (IOException e) {
			fail("Unexpected exception");
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	protected RootNode createRootNode(int classes) {
		RootNode root = new RootNode(randomName());
		for(int i = 0; i < classes; ++i){
			root.addClass(createClassNode(rand.nextInt(MAX_METHODS) + 1));
		}
		return root;
	}

	protected ClassNode createClassNode(int methods) {
		ClassNode classNode = new ClassNode("com.example." + randomName());
		for(int i = 0; i < methods; ++i){
			int numOfParameters = rand.nextInt(MAX_PARAMETERS) + 1;
//			int numOfExpParameters = rand.nextInt(MAX_EXPECTED_PARAMETERS);
//			if(numOfParameters + numOfExpParameters == 0){
//				numOfParameters = 1;
//			}
			int numOfConstraints = rand.nextInt(MAX_CONSTRAINTS) + 1;
			int numOfTestCases = rand.nextInt(MAX_TEST_CASES);
			classNode.addMethod(createMethodNode(numOfParameters, 0, numOfConstraints, numOfTestCases));
		}
		return classNode;
	}

	protected MethodNode createMethodNode(int numOfParameters,
			int numOfExpParameters, int numOfConstraints, int numOfTestCases) {
		MethodNode method = new MethodNode(randomName());
		List<ParameterNode> partitionedParameters = createPartitionedParameters(numOfParameters);
		List<ParameterNode> expectedParameters = createExpectedParameters(numOfExpParameters);

		for(int i = 0, j = 0; i < partitionedParameters.size() || j < expectedParameters.size();){
			if(rand.nextBoolean() && i < partitionedParameters.size()){
				method.addParameter(partitionedParameters.get(i));
				++i;
			}
			else if (j < expectedParameters.size()){
				method.addParameter(expectedParameters.get(j));
				++j;
			}
		}

		List<ConstraintNode> constraints = createConstraints(partitionedParameters, expectedParameters, numOfConstraints);
		List<TestCaseNode> testCases = createTestCases(method.getParameters(), numOfTestCases);

		for(ConstraintNode constraint : constraints){
			method.addConstraint(constraint);
		}
		for(TestCaseNode testCase : testCases){
			method.addTestCase(testCase);
		}

		return method;
	}

	private List<ParameterNode> createPartitionedParameters(int numOfParameters) {
		List<ParameterNode> parameters = new ArrayList<ParameterNode>();
		for(int i = 0; i < numOfParameters; i++){
			parameters.add(createPartitionedParameter(CATEGORY_TYPES[rand.nextInt(CATEGORY_TYPES.length)], rand.nextInt(MAX_PARTITIONS) + 1));
		}
		return parameters;
	}

	private ParameterNode createPartitionedParameter(String type, int numOfPartitions) {
		ParameterNode parameter = new ParameterNode(randomName(), type, "0", false);
		for(int i = 0; i < numOfPartitions; i++){
			parameter.addPartition(createPartition(type, 1));
		}
		return parameter;
	}

	private List<ParameterNode> createExpectedParameters(int numOfExpParameters) {
		List<ParameterNode> parameters = new ArrayList<ParameterNode>();
		for(int i = 0; i < numOfExpParameters; i++){
			parameters.add(createExpectedValueParameter(CATEGORY_TYPES[rand.nextInt(CATEGORY_TYPES.length)]));
		}
		return parameters;
	}

	private ParameterNode createExpectedValueParameter(String type) {
		String defaultValue = createRandomValue(type);
		ParameterNode parameter = new ParameterNode(randomName(), type, "0", true);
		parameter.setDefaultValueString(defaultValue);
		return parameter;
	}

	private String createRandomValue(String type) {
		switch(type){
		case Constants.TYPE_NAME_BOOLEAN:
			return Boolean.toString(rand.nextBoolean());
		case Constants.TYPE_NAME_BYTE:
			return Byte.toString((byte)rand.nextInt());
		case Constants.TYPE_NAME_CHAR:
			int random = rand.nextInt(255);
			if (random >= 32) {
				return new String ("\\" + String.valueOf(random));
			}
			return new String ("\\");
		case Constants.TYPE_NAME_DOUBLE:
			return Double.toString(rand.nextDouble());
		case Constants.TYPE_NAME_FLOAT:
			return Float.toString(rand.nextFloat());
		case Constants.TYPE_NAME_INT:
			return Integer.toString(rand.nextInt());
		case Constants.TYPE_NAME_LONG:
			return Long.toString(rand.nextLong());
		case Constants.TYPE_NAME_SHORT:
			return Short.toString((short)rand.nextInt());
		case Constants.TYPE_NAME_STRING:
			if(rand.nextInt(5) == 0){
				return com.testify.ecfeed.ui.common.Constants.NULL_VALUE_STRING_REPRESENTATION;
			}
			else{
				return generateRandomString(rand.nextInt(10));
			}
		default:
			fail("Unexpected parameter type");
			return null;
		}
	}

	String generateRandomString(int length) {
		String allowedChars = " 1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		String result = new String();
		for(int i = 0; i < length; i++){
			int index = rand.nextInt(allowedChars.length());
			result += allowedChars.substring(index, index + 1);
		}
		return result;
	}

	private ChoiceNode createPartition(String type, int level) {
		String value = createRandomValue(type);
		ChoiceNode partition = new ChoiceNode(randomName(), value);
		for(int i = 0; i < rand.nextInt(MAX_PARTITION_LABELS); i++){
			partition.addLabel(generateRandomString(10));
		}
		boolean createChildren = rand.nextBoolean();
		int numOfChildren = rand.nextInt(MAX_PARTITIONS);
		if(createChildren && level <= MAX_PARTITION_LEVELS){
			for(int i = 0; i < numOfChildren; i++){
				partition.addPartition(createPartition(type, level + 1));
			}
		}
		return partition;
	}


	private List<ConstraintNode> createConstraints(List<ParameterNode> partitionedParameters,
			List<ParameterNode> expectedParameters, int numOfConstraints) {
		List<ConstraintNode> constraints = new ArrayList<ConstraintNode>();
		for(int i = 0; i < numOfConstraints; ++i){
			constraints.add(new ConstraintNode(randomName(), createConstraint(partitionedParameters, expectedParameters)));
		}
		return constraints;
	}

	private Constraint createConstraint(List<ParameterNode> partitionedParameters,
			List<ParameterNode> expectedParameters) {
		BasicStatement premise = createPartitionedStatement(partitionedParameters);
		BasicStatement consequence = null;
		while(consequence == null){
			if(rand.nextBoolean()){
				consequence = createPartitionedStatement(partitionedParameters);
			}
			else{
				consequence = createExpectedStatement(expectedParameters);
			}
		}
		return new Constraint(premise, consequence);
	}

	private BasicStatement createPartitionedStatement(List<ParameterNode> parameters) {
		BasicStatement statement = null;
		while(statement == null){
			switch(rand.nextInt(3)){
			case 0: statement = new StaticStatement(rand.nextBoolean());
			case 1: if(getPartitionedParameters(parameters).size() > 0){
				switch(rand.nextInt(2)){
				case 0:
					statement = createPartitionStatement(parameters);
				case 1:
					statement = createLabelStatement(parameters);
				}
			}
			case 2: statement = createStatementArray(rand.nextInt(3), parameters);
			}
		}
		return statement;
	}

	private BasicStatement createLabelStatement(List<ParameterNode> parameters) {
		ParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		Set<String> labels = parameter.getLeafLabels();
		String label;
		if(labels.size() > 0){
			label = new ArrayList<String>(labels).get(rand.nextInt(labels.size()));
		}
		else{
			label = "SomeLabel";
			parameter.getPartitions().get(0).addLabel(label);
		}
		EStatementRelation relation = pickRelation();
		return new DecomposedParameterStatement(parameter, relation, label);
	}

	private BasicStatement createPartitionStatement(List<ParameterNode> parameters) {
		ParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		ChoiceNode partition = new ArrayList<ChoiceNode>(parameter.getLeafPartitions()).get(rand.nextInt(parameter.getPartitions().size()));
		EStatementRelation relation = pickRelation();
		return new DecomposedParameterStatement(parameter, relation, partition);
	}

	private EStatementRelation pickRelation() {
		EStatementRelation relation;
		switch(rand.nextInt(2)){
		case 0: relation = EStatementRelation.EQUAL;
		case 1: relation = EStatementRelation.NOT;
		default: relation = EStatementRelation.EQUAL;
		}
		return relation;
	}

	private BasicStatement createExpectedStatement(List<ParameterNode> parameters) {
		if(parameters.size() == 0) return null;
		ParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		return new ExpectedValueStatement(parameter, new ChoiceNode("default", createRandomValue(parameter.getType())));
	}

	private List<ParameterNode> getPartitionedParameters(List<? extends ParameterNode> parameters) {
		List<ParameterNode> result = new ArrayList<ParameterNode>();
		for(ParameterNode parameter : parameters){
			if(parameter instanceof ParameterNode == false){
				result.add(parameter);
			}
		}
		return result;
	}

	private BasicStatement createStatementArray(int levels, List<ParameterNode> parameters) {
		StatementArray array = new StatementArray(rand.nextBoolean()?EStatementOperator.AND:EStatementOperator.OR);
		for(int i = 0; i < rand.nextInt(3) + 1; ++i){
			if(levels > 0){
				array.addStatement(createStatementArray(levels - 1, parameters));
			}
			else{
				if(rand.nextBoolean() && getPartitionedParameters(parameters).size() > 0){
					array.addStatement(createPartitionStatement(parameters));
				}
				else{
					array.addStatement(new StaticStatement(rand.nextBoolean()));
				}
			}
		}
		return array;
	}

	private List<TestCaseNode> createTestCases(
			List<ParameterNode> parameters, int numOfTestCases) {
		List<TestCaseNode> result = new ArrayList<TestCaseNode>();
		try {
			List<IConstraint<ChoiceNode>> constraints = new ArrayList<IConstraint<ChoiceNode>>();
			RandomGenerator<ChoiceNode> generator = new RandomGenerator<ChoiceNode>();
			List<List<ChoiceNode>> input = getGeneratorInput(parameters);
			Map<String, Object> genParameters = new HashMap<String, Object>();
			genParameters.put("length", numOfTestCases);
			genParameters.put("duplicates", true);

			generator.initialize(input, constraints, genParameters);
			List<ChoiceNode> next;
			while((next = generator.next()) != null){
				result.add(new TestCaseNode(randomName(), next));
			}
		} catch (GeneratorException e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
		return result;
	}

	private List<List<ChoiceNode>> getGeneratorInput(
			List<ParameterNode> parameters) {
		List<List<ChoiceNode>> result = new ArrayList<List<ChoiceNode>>();
		for(ParameterNode parameter : parameters){
			result.add(parameter.getLeafPartitions());
		}
		return result;
	}

	protected String randomName(){
		return "name" + nextInt++;
	}

	private void compareModels(RootNode model1, RootNode model2) {
		compareNames(model1.getName(), model2.getName());
		compareSizes(model1.getClasses(), model2.getClasses());
		for(int i = 0; i < model1.getClasses().size(); ++i){
			compareClasses(model1.getClasses().get(i), model2.getClasses().get(i));
		}
	}

	private void compareClasses(ClassNode classNode1, ClassNode classNode2) {
		compareNames(classNode1.getName(), classNode2.getName());
		compareSizes(classNode1.getMethods(), classNode2.getMethods());

		for(int i = 0; i < classNode1.getMethods().size(); ++i){
			compareMethods(classNode1.getMethods().get(i), classNode2.getMethods().get(i));
		}
	}

	private void compareMethods(MethodNode method1, MethodNode method2) {
		compareNames(method1.getName(), method2.getName());
		compareSizes(method1.getParameters(), method2.getParameters());
		compareSizes(method1.getConstraintNodes(), method2.getConstraintNodes());
		compareSizes(method1.getTestCases(), method2.getTestCases());

		for(int i =0; i < method1.getParameters().size(); ++i){
			compareParameters(method1.getParameters().get(i), method2.getParameters().get(i));
		}
		for(int i =0; i < method1.getConstraintNodes().size(); ++i){
			compareConstraintNodes(method1.getConstraintNodes().get(i), method2.getConstraintNodes().get(i));
		}
		for(int i =0; i < method1.getTestCases().size(); ++i){
			compareTestCases(method1.getTestCases().get(i), method2.getTestCases().get(i));
		}
	}

	private void compareParameters(ParameterNode parameter1, ParameterNode parameter2) {
		compareNames(parameter1.getName(), parameter2.getName());
		compareNames(parameter1.getType(), parameter2.getType());
		compareSizes(parameter1.getPartitions(), parameter2.getPartitions());
		if(parameter1 instanceof ParameterNode || parameter2 instanceof ParameterNode){
			if((parameter1 instanceof ParameterNode && parameter2 instanceof ParameterNode) == false){
				fail("Either both parameters must be expected value or none");
			}
		}
		for(int i = 0; i < parameter1.getPartitions().size(); ++i){
			comparePartitions(parameter1.getPartitions().get(i), parameter2.getPartitions().get(i));
		}
	}

	private void comparePartitions(ChoiceNode partition1, ChoiceNode partition2) {
		compareNames(partition1.getName(), partition2.getName());
		compareValues(partition1.getValueString(),partition2.getValueString());
		compareLabels(partition1.getLabels(), partition2.getLabels());
		assertEquals(partition1.getPartitions().size(), partition2.getPartitions().size());
		for(int i = 0; i < partition1.getPartitions().size(); i++){
			comparePartitions(partition1.getPartitions().get(i), partition2.getPartitions().get(i));
		}
	}

	private void compareLabels(Set<String> labels, Set<String> labels2) {
		assertTrue(labels.size() == labels2.size());
		for(String label : labels){
			assertTrue(labels2.contains(label));
		}
	}

	private void compareValues(Object value1, Object value2) {
		boolean result = true;
		if(value1 == null){
			result = (value2 == null);
		}
		else{
			result = value1.equals(value2);
		}
		if(!result){
			fail("Value " + value1 + " differ from " + value2);
		}
	}

	private void compareConstraintNodes(ConstraintNode constraint1, ConstraintNode constraint2) {
		compareNames(constraint1.getName(), constraint2.getName());
		compareConstraints(constraint1.getConstraint(), constraint2.getConstraint());
	}


	private void compareConstraints(Constraint constraint1, Constraint constraint2) {
		compareBasicStatements(constraint1.getPremise(), constraint2.getPremise());
		compareBasicStatements(constraint1.getConsequence(), constraint2.getConsequence());
	}

	private void compareBasicStatements(BasicStatement statement1, BasicStatement statement2) {
		if(statement1 instanceof StaticStatement && statement2 instanceof StaticStatement){
			compareStaticStatements((StaticStatement)statement1, (StaticStatement)statement2);
		}
		else if(statement1 instanceof DecomposedParameterStatement && statement2 instanceof DecomposedParameterStatement){
			compareRelationStatements((DecomposedParameterStatement)statement1, (DecomposedParameterStatement)statement2);
		}
		else if(statement1 instanceof StatementArray && statement2 instanceof StatementArray){
			compareStatementArrays((StatementArray)statement1, (StatementArray)statement2);
		}
		else if(statement1 instanceof ExpectedValueStatement && statement2 instanceof ExpectedValueStatement){
			compareExpectedValueStatements((ExpectedValueStatement)statement1, (ExpectedValueStatement)statement2);
		}
		else{
			fail("Unknown type of statement or compared statements are of didderent types");
		}
	}

	private void compareExpectedValueStatements(
			ExpectedValueStatement statement1, ExpectedValueStatement statement2) {
		compareParameters(statement1.getParameter(), statement2.getParameter());
		assertEquals(statement1.getCondition().getValueString(), statement2.getCondition().getValueString());
	}

	private void compareRelationStatements(DecomposedParameterStatement statement1, DecomposedParameterStatement statement2) {
		compareParameters(statement1.getParameter(), statement2.getParameter());
		if((statement1.getRelation() != statement2.getRelation())){
			fail("Compared statements have different relations: " +
					statement1.getRelation() + " and " + statement2.getRelation());
		}
		compareConditions(statement1.getConditionValue(), statement2.getConditionValue());
	}

	private void compareConditions(Object condition, Object condition2) {
		if(condition instanceof String && condition2 instanceof String){
			if(condition.equals(condition2) == false){
				fail("Compared labels are different: " + condition + "!=" + condition2);
			}
		}
		else if(condition instanceof ChoiceNode && condition2 instanceof ChoiceNode){
			comparePartitions((ChoiceNode)condition, (ChoiceNode)condition2);
		}
		else{
			fail("Unknown or not same types of compared conditions");
		}
	}

	private void compareStatementArrays(StatementArray array1, StatementArray array2) {
		if(array1.getOperator() != array2.getOperator()){
			fail("Operator of compared statement arrays differ");
		}
		compareSizes(array1.getChildren(), array2.getChildren());
		for(int i = 0; i < array1.getChildren().size(); ++i){
			compareBasicStatements(array1.getChildren().get(i), array2.getChildren().get(i));
		}
	}

	private void compareStaticStatements(StaticStatement statement1, StaticStatement statement2) {
		if(statement1.getValue() != statement2.getValue()){
			fail("Static statements different");
		}
	}

	private void compareTestCases(TestCaseNode testCase1, TestCaseNode testCase2) {
		compareNames(testCase1.getName(), testCase2.getName());
		compareSizes(testCase1.getTestData(), testCase2.getTestData());
		for(int i = 0; i < testCase1.getTestData().size(); i++){
			ChoiceNode testValue1 = testCase1.getTestData().get(i);
			ChoiceNode testValue2 = testCase2.getTestData().get(i);

			if(testValue1.getParameter() instanceof ParameterNode){
				compareValues(testValue1.getValueString(), testValue2.getValueString());
			}
			else{
				comparePartitions(testCase1.getTestData().get(i),testCase2.getTestData().get(i));
			}
		}
	}

	private void compareSizes(Collection<? extends Object> collection1, Collection<? extends Object> collection2) {
		if(collection1.size() != collection2.size()){
			fail("Different sizes of collections");
		}
	}

	private void compareNames(String name, String name2) {
		if(name.equals(name2) == false){
			fail("Different names: " + name + ", " + name2);
		}
	}
}
