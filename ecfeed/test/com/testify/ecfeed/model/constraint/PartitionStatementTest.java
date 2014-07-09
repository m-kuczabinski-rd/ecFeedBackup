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

package com.testify.ecfeed.model.constraint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class PartitionStatementTest {

	private static MethodNode fMethod;
	private static CategoryNode fCategory;
	private static PartitionNode fPartition1;
	private static PartitionNode fPartition2;
	private static PartitionNode fPartition3;
	private static List<PartitionNode> fList1;
	private static List<PartitionNode> fList2;
	private static List<PartitionNode> fList3;

	private PartitionNode fP1 = new PartitionNode("p1", "0");
	private PartitionNode fP2 = new PartitionNode("p2", "0");
	private PartitionNode fP3 = new PartitionNode("p3", "0");

	private PartitionNode fP11 = new PartitionNode("p11", "0");
	private PartitionNode fP12 = new PartitionNode("p12", "0");
	private PartitionNode fP13 = new PartitionNode("p13", "0");

	private PartitionNode fP21 = new PartitionNode("p21", "0");
	private PartitionNode fP22 = new PartitionNode("p22", "0");
	private PartitionNode fP23 = new PartitionNode("p23", "0");

	private PartitionNode fP221 = new PartitionNode("p21", "0");
	private PartitionNode fP222 = new PartitionNode("p22", "0");
	private PartitionNode fP223 = new PartitionNode("p23", "0");

	private PartitionNode fP31 = new PartitionNode("p31", "0");
	private PartitionNode fP32 = new PartitionNode("p32", "0");
	private PartitionNode fP33 = new PartitionNode("p33", "0");

	@Before
	public void prepareStructure(){
		fP1.addPartition(fP11);
		fP1.addPartition(fP12);
		fP1.addPartition(fP13);

		fP2.addPartition(fP21);
		fP2.addPartition(fP22);
		fP2.addPartition(fP23);

		fP22.addPartition(fP221);
		fP22.addPartition(fP222);
		fP22.addPartition(fP223);

		fP3.addPartition(fP31);
		fP3.addPartition(fP32);
		fP3.addPartition(fP33);

		fCategory.addPartition(fP1);
		fCategory.addPartition(fP2);
		fCategory.addPartition(fP3);
		
		fMethod.addCategory(fCategory);
	}

	@Test
	public void equalsTest(){
		PartitionedCategoryStatement statement = new PartitionedCategoryStatement(fCategory, Relation.EQUAL, fP22);
		
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP221})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP222})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP22})));

		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP2})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP1})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP3})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP13})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP33})));
	}

	@Test 
	public void notEqualsTest(){
		PartitionedCategoryStatement statement = new PartitionedCategoryStatement(fCategory, Relation.NOT, fP22);
		
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP221})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP222})));
		assertFalse(statement.evaluate(Arrays.asList(new PartitionNode[]{fP22})));

		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP2})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP1})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP3})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP13})));
		assertTrue(statement.evaluate(Arrays.asList(new PartitionNode[]{fP33})));
	}

	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method");
		fCategory = new CategoryNode("category", "type", false);
		fPartition1 = new PartitionNode("partition1", null);
		fPartition2 = new PartitionNode("partition2", null);
		fPartition3 = new PartitionNode("partition3", null);
		fCategory.addPartition(fPartition1);
		fCategory.addPartition(fPartition2);
		fCategory.addPartition(fPartition3);
		fMethod.addCategory(fCategory);
		
		fList1 = new ArrayList<PartitionNode>();
		fList1.add(fPartition1);
		fList2 = new ArrayList<PartitionNode>();
		fList2.add(fPartition2);
		fList3 = new ArrayList<PartitionNode>();
		fList3.add(fPartition3);
	}
	
	
	@Test
	public void testEvaluate() {

		PartitionedCategoryStatement statement1 = new PartitionedCategoryStatement(fCategory, Relation.EQUAL, fPartition2);
		assertFalse(statement1.evaluate(fList1));
		assertTrue(statement1.evaluate(fList2));
		assertFalse(statement1.evaluate(fList3));

		PartitionedCategoryStatement statement4 = new PartitionedCategoryStatement(fCategory, Relation.NOT, fPartition2);
		assertTrue(statement4.evaluate(fList1));
		assertFalse(statement4.evaluate(fList2));
		assertTrue(statement4.evaluate(fList3));
}

	@Test
	public void testMentionsPartitionNode() {
		PartitionedCategoryStatement statement = new PartitionedCategoryStatement(fCategory, Relation.EQUAL, fPartition2);
		assertTrue(statement.mentions(fPartition2));
		assertFalse(statement.mentions(fPartition1));
	}

	@Test
	public void testMentionsCategoryNode() {
		PartitionedCategoryStatement statement = new PartitionedCategoryStatement(fCategory, Relation.EQUAL, fPartition2);
		CategoryNode category = new CategoryNode("name", "type", false);
		assertTrue(statement.mentions(fCategory));
		assertFalse(statement.mentions(category));
	}

	@Test
	public void testGetCondition() {
		PartitionedCategoryStatement statement = new PartitionedCategoryStatement(fCategory, Relation.EQUAL, fPartition2);
		assertEquals(fPartition2, statement.getConditionValue());
	}

	@Test
	public void testGetRelation() {
		PartitionedCategoryStatement statement = new PartitionedCategoryStatement(fCategory, Relation.EQUAL, fPartition2);
		assertEquals(Relation.EQUAL, statement.getRelation());
	}

	@Test
	public void compareTest(){
		CategoryNode c1 = new CategoryNode("name", "type", true);
		CategoryNode c2 = new CategoryNode("name", "type", true);
		
		PartitionNode p1 = new PartitionNode("name", "value");
		PartitionNode p2 = new PartitionNode("name", "value");
		
		PartitionedCategoryStatement s1 = new PartitionedCategoryStatement(c1, Relation.NOT, p1);
		PartitionedCategoryStatement s2 = new PartitionedCategoryStatement(c2, Relation.NOT, p2);
		
		assertTrue(s1.compare(s2));
		c1.setName("c1");
		assertFalse(s1.compare(s2));
		c2.setName("c1");
		assertTrue(s1.compare(s2));
		
		p1.setName("p1");
		assertFalse(s1.compare(s2));
		p2.setName("p1");
		assertTrue(s1.compare(s2));
		
		s1.setCondition("label");
		assertFalse(s1.compare(s2));
		s2.setCondition("label");
		assertTrue(s1.compare(s2));
		
		s1.setRelation(Relation.EQUAL);
		assertFalse(s1.compare(s2));
		s2.setRelation(Relation.EQUAL);
		assertTrue(s1.compare(s2));
	}
}
