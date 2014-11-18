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
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedParameterStatement;
import com.testify.ecfeed.model.EStatementRelation;

public class PartitionStatementTest {

	private static MethodNode fMethod;
	private static ParameterNode fParameter;
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

		fParameter.addPartition(fP1);
		fParameter.addPartition(fP2);
		fParameter.addPartition(fP3);
		
		fMethod.addParameter(fParameter);
	}

	@Test
	public void equalsTest(){
		PartitionedParameterStatement statement = new PartitionedParameterStatement(fParameter, EStatementRelation.EQUAL, fP22);
		
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
		PartitionedParameterStatement statement = new PartitionedParameterStatement(fParameter, EStatementRelation.NOT, fP22);
		
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
		fParameter = new ParameterNode("parameter", "type", "0", false);
		fPartition1 = new PartitionNode("partition1", null);
		fPartition2 = new PartitionNode("partition2", null);
		fPartition3 = new PartitionNode("partition3", null);
		fParameter.addPartition(fPartition1);
		fParameter.addPartition(fPartition2);
		fParameter.addPartition(fPartition3);
		fMethod.addParameter(fParameter);
		
		fList1 = new ArrayList<PartitionNode>();
		fList1.add(fPartition1);
		fList2 = new ArrayList<PartitionNode>();
		fList2.add(fPartition2);
		fList3 = new ArrayList<PartitionNode>();
		fList3.add(fPartition3);
	}
	
	
	@Test
	public void testEvaluate() {

		PartitionedParameterStatement statement1 = new PartitionedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		assertFalse(statement1.evaluate(fList1));
		assertTrue(statement1.evaluate(fList2));
		assertFalse(statement1.evaluate(fList3));

		PartitionedParameterStatement statement4 = new PartitionedParameterStatement(fParameter, EStatementRelation.NOT, fPartition2);
		assertTrue(statement4.evaluate(fList1));
		assertFalse(statement4.evaluate(fList2));
		assertTrue(statement4.evaluate(fList3));
}

	@Test
	public void testMentionsPartitionNode() {
		PartitionedParameterStatement statement = new PartitionedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		assertTrue(statement.mentions(fPartition2));
		assertFalse(statement.mentions(fPartition1));
	}

	@Test
	public void testMentionsParameterNode() {
		PartitionedParameterStatement statement = new PartitionedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		ParameterNode parameter = new ParameterNode("name", "type", "0", false);
		assertTrue(statement.mentions(fParameter));
		assertFalse(statement.mentions(parameter));
	}

	@Test
	public void testGetCondition() {
		PartitionedParameterStatement statement = new PartitionedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		assertEquals(fPartition2, statement.getConditionValue());
	}

	@Test
	public void testGetRelation() {
		PartitionedParameterStatement statement = new PartitionedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		assertEquals(EStatementRelation.EQUAL, statement.getRelation());
	}

	@Test
	public void compareTest(){
		ParameterNode c1 = new ParameterNode("name", "type", "0", true);
		ParameterNode c2 = new ParameterNode("name", "type", "0", true);
		
		PartitionNode p1 = new PartitionNode("name", "value");
		PartitionNode p2 = new PartitionNode("name", "value");
		
		PartitionedParameterStatement s1 = new PartitionedParameterStatement(c1, EStatementRelation.NOT, p1);
		PartitionedParameterStatement s2 = new PartitionedParameterStatement(c2, EStatementRelation.NOT, p2);
		
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
		s2.setCondition("label1");
		assertFalse(s1.compare(s2));
		s2.setCondition("label");
		assertTrue(s1.compare(s2));
		
		s1.setRelation(EStatementRelation.EQUAL);
		assertFalse(s1.compare(s2));
		s2.setRelation(EStatementRelation.EQUAL);
		assertTrue(s1.compare(s2));
	}
}
