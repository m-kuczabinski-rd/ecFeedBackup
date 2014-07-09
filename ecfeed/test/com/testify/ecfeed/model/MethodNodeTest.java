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

package com.testify.ecfeed.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class MethodNodeTest {
	@Test
	public void testAddCategory(){
		MethodNode method = new MethodNode("name");
		CategoryNode category = new CategoryNode("category", "type1", false);
		CategoryNode expCat = new CategoryNode("expCat", "type2", true);
		assertEquals(0, method.getCategories().size());
		method.addCategory(category);
		assertEquals(1, method.getCategories().size());
		assertTrue(method.getCategories().contains(category));
		method.addCategory(expCat);
		assertEquals(2, method.getCategories().size());
		assertTrue(method.getCategories().contains(expCat));
		
		assertEquals(1, method.getCategoriesNames(false).size());
		assertTrue(method.getCategoriesNames(false).contains("category"));

		assertEquals(1, method.getCategoriesNames(true).size());
		assertTrue(method.getCategoriesNames(true).contains("expCat"));
		
		assertEquals(category, method.getCategory("category"));
		assertEquals(expCat, method.getCategory("expCat"));
		
		assertEquals(2, method.getCategoriesTypes().size());
		assertTrue(method.getCategoriesTypes().contains("type1"));
		assertTrue(method.getCategoriesTypes().contains("type2"));
		
	}
	
	@Test
	public void testAddConstraint(){
		MethodNode method = new MethodNode("name");
		Constraint constraint1 = new Constraint(new StaticStatement(true), new StaticStatement(false));
		Constraint constraint2 = new Constraint(new StaticStatement(true), new StaticStatement(false));
		ConstraintNode constraintNode1 = new ConstraintNode("name1", constraint1);
		ConstraintNode constraintNode2 = new ConstraintNode("name2", constraint2);
		assertEquals(0, method.getConstraintNodes().size());
		assertEquals(0, method.getConstraints("name1").size());
		assertEquals(0, method.getConstraints("name2").size());
		
		method.addConstraint(constraintNode1);
		method.addConstraint(constraintNode2);
		
		assertEquals(method, constraintNode1.getParent());
		assertEquals(method, constraintNode2.getParent());
		
		assertEquals(2, method.getAllConstraints().size());
		assertTrue(method.getAllConstraints().contains(constraint1));
		assertTrue(method.getAllConstraints().contains(constraint2));

		assertEquals(2, method.getConstraintNodes().size());
		assertEquals(1, method.getConstraints("name1").size());
		assertTrue(method.getConstraints("name1").contains(constraint1));
		assertFalse(method.getConstraints("name1").contains(constraint2));
		assertEquals(1, method.getConstraints("name2").size());
		assertFalse(method.getConstraints("name2").contains(constraint1));
		assertTrue(method.getConstraints("name2").contains(constraint2));
		
		assertEquals(2, method.getConstraintsNames().size());
		assertTrue(method.getConstraintsNames().contains("name1"));
		assertTrue(method.getConstraintsNames().contains("name2"));
		
		method.removeConstraint(constraintNode1);
		assertFalse(method.getConstraintsNames().contains("name1"));
		assertTrue(method.getConstraintsNames().contains("name2"));
		
	}

	@Test
	public void testAddTestCase(){
		MethodNode method = new MethodNode("name");
		TestCaseNode testCase1 = new TestCaseNode("suite 1", new ArrayList<PartitionNode>());
		TestCaseNode testCase2 = new TestCaseNode("suite 2", new ArrayList<PartitionNode>());
		assertEquals(0, method.getTestCases().size());
		assertEquals(0, method.getTestCases("suite 1").size());
		assertEquals(0, method.getTestCases("suite 2").size());
		
		method.addTestCase(testCase1);
		method.addTestCase(testCase2);
		
		assertEquals(method, testCase1.getParent());
		assertEquals(method, testCase2.getParent());
		
		assertEquals(2, method.getTestCases().size());
		assertTrue(method.getTestCases().contains(testCase1));
		assertTrue(method.getTestCases().contains(testCase2));
		
		assertEquals(1, method.getTestCases("suite 1").size());
		assertTrue(method.getTestCases("suite 1").contains(testCase1));
		assertFalse(method.getTestCases("suite 1").contains(testCase2));

		assertEquals(1, method.getTestCases("suite 2").size());
		assertTrue(method.getTestCases("suite 2").contains(testCase2));
		assertFalse(method.getTestCases("suite 2").contains(testCase1));
		
		assertEquals(2, method.getTestSuites().size());
		assertTrue(method.getTestSuites().contains("suite 1"));
		assertTrue(method.getTestSuites().contains("suite 2"));
	}

	@Test
	public void testGetChildren(){
		MethodNode method = new MethodNode("name");
		TestCaseNode testCase = new TestCaseNode("test case", new ArrayList<PartitionNode>());
		ConstraintNode constraint = new ConstraintNode("constraint", 
				new Constraint(new StaticStatement(false), new StaticStatement(false)));
		CategoryNode category = new CategoryNode("category", "type", false);
		CategoryNode expCat = new CategoryNode("expCat", "type", true);
		
		assertEquals(0, method.getChildren().size());
		assertFalse(method.hasChildren());
		method.addCategory(category);
		method.addCategory(expCat);
		method.addConstraint(constraint);
		method.addTestCase(testCase);
		
		assertEquals(4, method.getChildren().size());
		assertTrue(method.hasChildren());
		assertTrue(method.getChildren().contains(category));
		assertTrue(method.getChildren().contains(expCat));
		assertTrue(method.getChildren().contains(constraint));
		assertTrue(method.getChildren().contains(testCase));
		assertEquals(category, method.getChild("category"));
		assertEquals(expCat, method.getChild("expCat"));
		assertEquals(testCase, method.getChild("test case"));
		assertEquals(constraint, method.getChild("constraint"));
	}
	
	@Test
	public void moveChildTest(){
		MethodNode method = new MethodNode("name");
		
		CategoryNode category1 = new CategoryNode("name", "type", false);
		CategoryNode category2 = new CategoryNode("name", "type", false);
		CategoryNode category3 = new CategoryNode("name", "type", false);
		
		TestCaseNode testCase1 = new TestCaseNode("test case", new ArrayList<PartitionNode>());
		TestCaseNode testCase2 = new TestCaseNode("test case", new ArrayList<PartitionNode>());
		TestCaseNode testCase3 = new TestCaseNode("test case", new ArrayList<PartitionNode>());

		ConstraintNode constraint1 = new ConstraintNode("constraint", 
				new Constraint(new StaticStatement(false), new StaticStatement(false)));
		ConstraintNode constraint2 = new ConstraintNode("constraint", 
				new Constraint(new StaticStatement(false), new StaticStatement(false)));
		ConstraintNode constraint3 = new ConstraintNode("constraint", 
				new Constraint(new StaticStatement(false), new StaticStatement(false)));
		
		method.addCategory(category1);
		method.addCategory(category2);
		method.addCategory(category3);
		
		method.addTestCase(testCase1);
		method.addTestCase(testCase2);
		method.addTestCase(testCase3);
		
		method.addConstraint(constraint1);
		method.addConstraint(constraint2);
		method.addConstraint(constraint3);
		
		int category2Index = method.getChildren().indexOf(category2);
		int testCase2Index = method.getChildren().indexOf(testCase2);
		int constraint2Index = method.getChildren().indexOf(constraint2);
		
		method.moveChild(category2, true);
		assertEquals(category2Index - 1, method.getChildren().indexOf(category2));
		method.moveChild(category2, true); //category should not be moved further
		assertEquals(category2Index - 1, method.getChildren().indexOf(category2));
		method.moveChild(category2, false);
		assertEquals(category2Index, method.getChildren().indexOf(category2));
		method.moveChild(category2, false);
		assertEquals(category2Index + 1, method.getChildren().indexOf(category2));
		method.moveChild(category2, false); //category should not be moved further
		assertEquals(category2Index + 1, method.getChildren().indexOf(category2));

		method.moveChild(testCase2, true);
		assertEquals(testCase2Index - 1, method.getChildren().indexOf(testCase2));
		method.moveChild(testCase2, true); //test case should not be moved further
		assertEquals(testCase2Index - 1, method.getChildren().indexOf(testCase2));
		method.moveChild(testCase2, false);
		assertEquals(testCase2Index, method.getChildren().indexOf(testCase2));
		method.moveChild(testCase2, false);
		assertEquals(testCase2Index + 1, method.getChildren().indexOf(testCase2));
		method.moveChild(testCase2, false); //test case should not be moved further
		assertEquals(testCase2Index + 1, method.getChildren().indexOf(testCase2));

		method.moveChild(constraint2, true);
		assertEquals(constraint2Index - 1, method.getChildren().indexOf(constraint2));
		method.moveChild(constraint2, true); //test case should not be moved further
		assertEquals(constraint2Index - 1, method.getChildren().indexOf(constraint2));
		method.moveChild(constraint2, false);
		assertEquals(constraint2Index, method.getChildren().indexOf(constraint2));
		method.moveChild(constraint2, false);
		assertEquals(constraint2Index + 1, method.getChildren().indexOf(constraint2));
		method.moveChild(constraint2, false); //test case should not be moved further
		assertEquals(constraint2Index + 1, method.getChildren().indexOf(constraint2));

	}
	
	@Test
	public void testRemoveTestCase(){
		MethodNode method = new MethodNode("name");
		TestCaseNode testCase1 = new TestCaseNode("name1", new ArrayList<PartitionNode>());
		TestCaseNode testCase2 = new TestCaseNode("name1", new ArrayList<PartitionNode>());
		TestCaseNode testCase3 = new TestCaseNode("name2", new ArrayList<PartitionNode>());
		TestCaseNode testCase4 = new TestCaseNode("name2", new ArrayList<PartitionNode>());
		
		method.addTestCase(testCase1);
		method.addTestCase(testCase2);
		method.addTestCase(testCase3);
		method.addTestCase(testCase4);
		
		assertEquals(4, method.getTestCases().size());
		
		method.removeTestCase(testCase1);

		assertEquals(3, method.getTestCases().size());
		assertFalse(method.getTestCases().contains(testCase1));
		
		method.removeTestSuite("name2");
		assertEquals(1, method.getTestCases().size());
		assertFalse(method.getTestCases().contains(testCase3));
		assertFalse(method.getTestCases().contains(testCase4));
		assertTrue(method.getTestCases().contains(testCase2));
	}
	
	@Test
	public void removePartitionedCategoryTest(){
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("category", "type", false);
		PartitionNode partition = new PartitionNode("partition", "0");
		Constraint mentioningConstraint = new Constraint(new PartitionedCategoryStatement(category, Relation.EQUAL, partition), new StaticStatement(false));
		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		TestCaseNode testCaseNode = new TestCaseNode("name", new ArrayList<PartitionNode>());
		
		category.addPartition(partition);
		method.addCategory(category);
		method.addConstraint(notMentioningConstraintNode);
		method.addConstraint(mentioningConstraintNode);
		method.addTestCase(testCaseNode);
		
		assertTrue(method.getCategories().contains(category));
		assertTrue(method.getCategories(false).contains(category));
		assertTrue(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		assertTrue(method.getTestCases().contains(testCaseNode));

		assertTrue(method.removeCategory(category));
		assertFalse(method.getCategories().contains(category));
		assertFalse(method.getCategories(false).contains(category));
		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
	}
	
	@Test
	public void removeExpectedCategoryTest(){
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("category", "type", true);
		category.setDefaultValueString("value");
		PartitionNode partition = new PartitionNode("partition", "value2");
		Constraint mentioningConstraint = new Constraint(new ExpectedValueStatement(category, partition), new StaticStatement(false));
		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		TestCaseNode testCaseNode = new TestCaseNode("name", new ArrayList<PartitionNode>());
		
		category.addPartition(partition);
		method.addCategory(category);
		method.addConstraint(notMentioningConstraintNode);
		method.addConstraint(mentioningConstraintNode);
		method.addTestCase(testCaseNode);
		
		assertTrue(method.getCategories().contains(category));
		assertTrue(method.getCategories(true).contains(category));
		assertTrue(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		assertTrue(method.getTestCases().contains(testCaseNode));

		assertTrue(method.removeCategory(category));
		assertFalse(method.getCategories().contains(category));
		assertFalse(method.getCategories(true).contains(category));
		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
	}

	@Test
	public void testGetExpectedCategoriesNames(){
		MethodNode method = new MethodNode("name");
		CategoryNode category = new CategoryNode("category", "type", false);
		CategoryNode expCat1 = new CategoryNode("expCat1", "type", true);
		CategoryNode expCat2 = new CategoryNode("expCat2", "type", true);
		
		method.addCategory(category);
		method.addCategory(expCat1);
		method.addCategory(expCat2);
		
		assertEquals(3, method.getCategories().size());
		assertTrue(method.getCategories().contains(category));
		assertTrue(method.getCategories().contains(expCat1));
		assertTrue(method.getCategories().contains(expCat2));
		
		assertEquals(2,  method.getCategoriesNames(true).size());
		assertTrue(method.getCategoriesNames(true).contains("expCat1"));
		assertTrue(method.getCategoriesNames(true).contains("expCat2"));
		
		method.removeCategory(expCat1);
		assertEquals(1,  method.getCategoriesNames(true).size());
		assertFalse(method.getCategoriesNames(true).contains("expCat1"));
		assertTrue(method.getCategoriesNames(true).contains("expCat2"));
	}

	@Test
	public void testReplaceCategoryWithExpected(){
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("category", "type", false);
		PartitionNode partition = new PartitionNode("partition", "value");
		Constraint mentioningConstraint = new Constraint(new PartitionedCategoryStatement(category, Relation.EQUAL, partition), new StaticStatement(false));
		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		List<PartitionNode> testData = new ArrayList<PartitionNode>();
		testData.add(partition);
		TestCaseNode testCaseNode = new TestCaseNode("name", testData);

		CategoryNode newExpCat = new CategoryNode("expCat", "type", true);
		newExpCat.setDefaultValueString("expected value");
		category.addPartition(partition);
		method.addCategory(category);
		method.addConstraint(notMentioningConstraintNode);
		method.addConstraint(mentioningConstraintNode);
		method.addTestCase(testCaseNode);
		
		method.replaceCategory(0, newExpCat);
		
		assertFalse(method.getCategories().contains(category));
		assertFalse(method.getCategories(false).contains(category));
		assertTrue(method.getCategories().contains(newExpCat));
		assertTrue(method.getCategories(true).contains(newExpCat));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertTrue(method.getTestCases().contains(testCaseNode));
		assertEquals("expected value", testCaseNode.getTestData().get(0).getValueString());
	}
	
	@Test
	public void testReplaceCategoryWithPartitioned(){
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("category", "type", true);
		PartitionNode partition = new PartitionNode("partition", "value");
		Constraint mentioningConstraint = new Constraint(new ExpectedValueStatement(category, partition), new StaticStatement(false));
		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		List<PartitionNode> testData = new ArrayList<PartitionNode>();
		testData.add(partition);
		TestCaseNode testCaseNode = new TestCaseNode("name", testData);

		CategoryNode newCat = new CategoryNode("newCat", "type", false);
		category.addPartition(partition);
		method.addCategory(category);
		method.addConstraint(notMentioningConstraintNode);
		method.addConstraint(mentioningConstraintNode);
		method.addTestCase(testCaseNode);
		
		method.replaceCategory(0, newCat);
		
		assertFalse(method.getCategories().contains(category));
		assertFalse(method.getCategories(true).contains(category));
		assertTrue(method.getCategories().contains(newCat));
		assertTrue(method.getCategories(false).contains(newCat));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertEquals(0, method.getTestCases().size());
	}
	
	@Test
	public void testChangeCategoryTypeToExpected(){
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("category", "type", false);
		PartitionNode partition = new PartitionNode("partition", "value");
		Constraint mentioningConstraint = new Constraint(new PartitionedCategoryStatement(category, Relation.EQUAL, partition), new StaticStatement(false));
		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		List<PartitionNode> testData = new ArrayList<PartitionNode>();
		testData.add(partition);
		TestCaseNode testCaseNode = new TestCaseNode("name", testData);

		category.addPartition(partition);
		method.addCategory(category);
		method.addConstraint(notMentioningConstraintNode);
		method.addConstraint(mentioningConstraintNode);
		method.addTestCase(testCaseNode);
		
		category.getMethod().changeCategoryExpectedStatus(category,true);
		
		assertTrue(method.getCategories().contains(category));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertTrue(method.getTestCases().contains(testCaseNode));
		assertEquals("value", testCaseNode.getTestData().get(0).getValueString());
	}
	
	@Test
	public void testChangeCategoryTypeToPartitioned(){
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("category", "type", true);
		PartitionNode partition = new PartitionNode("partition", "value");
		Constraint mentioningConstraint = new Constraint(new ExpectedValueStatement(category, partition), new StaticStatement(false));
		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		List<PartitionNode> testData = new ArrayList<PartitionNode>();
		testData.add(partition);
		TestCaseNode testCaseNode = new TestCaseNode("name", testData);
		
		category.addPartition(partition);
		method.addCategory(category);
		method.addConstraint(notMentioningConstraintNode);
		method.addConstraint(mentioningConstraintNode);
		method.addTestCase(testCaseNode);

		category.getMethod().changeCategoryExpectedStatus(category,false);
		
		assertTrue(method.getCategories().contains(category));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertEquals(0, method.getTestCases().size());
	}
	
	@Test 
	public void testPartitionRemoved(){
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("category", "type", false);
		PartitionNode partition = new PartitionNode("partition", "value");
		Constraint mentioningConstraint = new Constraint(new PartitionedCategoryStatement(category, Relation.EQUAL, partition), new StaticStatement(false));
		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		List<PartitionNode> mentioningTestData = new ArrayList<PartitionNode>();
		mentioningTestData.add(partition);
		TestCaseNode mentioningTestCaseNode = new TestCaseNode("name", mentioningTestData);
		List<PartitionNode> notMentioningTestData = new ArrayList<PartitionNode>();
		mentioningTestData.add(new PartitionNode("dummy", "0"));
		TestCaseNode notMentioningTestCaseNode = new TestCaseNode("name", notMentioningTestData);

		category.addPartition(partition);
		method.addCategory(category);
		method.addConstraint(notMentioningConstraintNode);
		method.addConstraint(mentioningConstraintNode);
		method.addTestCase(mentioningTestCaseNode);
		method.addTestCase(notMentioningTestCaseNode);
		
		assertTrue(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		assertTrue(method.getTestCases().contains(notMentioningTestCaseNode));
		assertTrue(method.getTestCases().contains(mentioningTestCaseNode));
		
		category.removePartition(partition);
		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		assertTrue(method.getTestCases().contains(notMentioningTestCaseNode));
		assertFalse(method.getTestCases().contains(mentioningTestCaseNode));
		
	}
	
	/************compare()*************************/
	@Test
	public void compareNameTest(){
		MethodNode m1 = new MethodNode("m");
		MethodNode m2 = new MethodNode("m");
		
		assertTrue(m1.compare(m2));
		m1.setName("m1");
		assertFalse(m1.compare(m2));
		m2.setName("m1");
		assertTrue(m1.compare(m2));
	}

	@Test
	public void compareCategoriesTest(){
		MethodNode m1 = new MethodNode("m");
		MethodNode m2 = new MethodNode("m");
		
		assertTrue(m1.compare(m2));
		m1.setName("m1");
		assertFalse(m1.compare(m2));
		m2.setName("m1");
		assertTrue(m1.compare(m2));
		
		CategoryNode c1 = new CategoryNode("c", "type", true);
		CategoryNode c2 = new CategoryNode("c", "type", true);
		
		m1.addCategory(c1);
		assertFalse(m1.compare(m2));
		m2.addCategory(c2);
		assertTrue(m1.compare(m2));
		
		c1.setName("c1");
		assertFalse(m1.compare(m2));
		c2.setName("c1");
		assertTrue(m1.compare(m2));
	}
	
	@Test
	public void compareConstraintsTest(){
		MethodNode m1 = new MethodNode("m");
		MethodNode m2 = new MethodNode("m");

		ConstraintNode c1 = new ConstraintNode("c", new Constraint(new StaticStatement(true), new StaticStatement(true)));
		ConstraintNode c2 = new ConstraintNode("c", new Constraint(new StaticStatement(true), new StaticStatement(true)));
		
		m1.addConstraint(c1);
		assertFalse(m1.compare(m2));
		m2.addConstraint(c2);
		assertTrue(m1.compare(m2));
		
		c1.setName("c1");
		assertFalse(m1.compare(m2));
		c2.setName("c1");
		assertTrue(m1.compare(m2));
	}
	
	@Test
	public void compareTestCasesTest(){
		MethodNode m1 = new MethodNode("m");
		MethodNode m2 = new MethodNode("m");

		TestCaseNode tc1 = new TestCaseNode("tc", new ArrayList<PartitionNode>());
		TestCaseNode tc2 = new TestCaseNode("tc", new ArrayList<PartitionNode>());

		m1.addTestCase(tc1);
		assertFalse(m1.compare(m2));
		m2.addTestCase(tc2);
		assertTrue(m1.compare(m2));
		
		tc1.setName("tc1");
		assertFalse(m1.compare(m2));
		tc2.setName("tc1");
		assertTrue(m1.compare(m2));
}
	
	@Test
	public void compareSmokeTest(){
		for(int i = 0; i < 5; i++){
			RandomModelGenerator gen = new RandomModelGenerator();
			MethodNode m = gen.generateMethod(3, 3, 10);
			assertTrue(m.compare(m));
		}
	}
	
}
