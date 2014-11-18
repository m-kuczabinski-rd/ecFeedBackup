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
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.DecomposedParameterStatement;
import com.testify.ecfeed.model.EStatementRelation;

public class PartitionStatementTest {

	private static MethodNode fMethod;
	private static ParameterNode fParameter;
	private static ChoiceNode fPartition1;
	private static ChoiceNode fPartition2;
	private static ChoiceNode fPartition3;
	private static List<ChoiceNode> fList1;
	private static List<ChoiceNode> fList2;
	private static List<ChoiceNode> fList3;

	private ChoiceNode fP1 = new ChoiceNode("p1", "0");
	private ChoiceNode fP2 = new ChoiceNode("p2", "0");
	private ChoiceNode fP3 = new ChoiceNode("p3", "0");

	private ChoiceNode fP11 = new ChoiceNode("p11", "0");
	private ChoiceNode fP12 = new ChoiceNode("p12", "0");
	private ChoiceNode fP13 = new ChoiceNode("p13", "0");

	private ChoiceNode fP21 = new ChoiceNode("p21", "0");
	private ChoiceNode fP22 = new ChoiceNode("p22", "0");
	private ChoiceNode fP23 = new ChoiceNode("p23", "0");

	private ChoiceNode fP221 = new ChoiceNode("p21", "0");
	private ChoiceNode fP222 = new ChoiceNode("p22", "0");
	private ChoiceNode fP223 = new ChoiceNode("p23", "0");

	private ChoiceNode fP31 = new ChoiceNode("p31", "0");
	private ChoiceNode fP32 = new ChoiceNode("p32", "0");
	private ChoiceNode fP33 = new ChoiceNode("p33", "0");

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
		DecomposedParameterStatement statement = new DecomposedParameterStatement(fParameter, EStatementRelation.EQUAL, fP22);
		
		assertTrue(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP221})));
		assertTrue(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP222})));
		assertTrue(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP22})));

		assertFalse(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP2})));
		assertFalse(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP1})));
		assertFalse(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP3})));
		assertFalse(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP13})));
		assertFalse(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP33})));
	}

	@Test 
	public void notEqualsTest(){
		DecomposedParameterStatement statement = new DecomposedParameterStatement(fParameter, EStatementRelation.NOT, fP22);
		
		assertFalse(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP221})));
		assertFalse(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP222})));
		assertFalse(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP22})));

		assertTrue(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP2})));
		assertTrue(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP1})));
		assertTrue(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP3})));
		assertTrue(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP13})));
		assertTrue(statement.evaluate(Arrays.asList(new ChoiceNode[]{fP33})));
	}

	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method");
		fParameter = new ParameterNode("parameter", "type", "0", false);
		fPartition1 = new ChoiceNode("partition1", null);
		fPartition2 = new ChoiceNode("partition2", null);
		fPartition3 = new ChoiceNode("partition3", null);
		fParameter.addPartition(fPartition1);
		fParameter.addPartition(fPartition2);
		fParameter.addPartition(fPartition3);
		fMethod.addParameter(fParameter);
		
		fList1 = new ArrayList<ChoiceNode>();
		fList1.add(fPartition1);
		fList2 = new ArrayList<ChoiceNode>();
		fList2.add(fPartition2);
		fList3 = new ArrayList<ChoiceNode>();
		fList3.add(fPartition3);
	}
	
	
	@Test
	public void testEvaluate() {

		DecomposedParameterStatement statement1 = new DecomposedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		assertFalse(statement1.evaluate(fList1));
		assertTrue(statement1.evaluate(fList2));
		assertFalse(statement1.evaluate(fList3));

		DecomposedParameterStatement statement4 = new DecomposedParameterStatement(fParameter, EStatementRelation.NOT, fPartition2);
		assertTrue(statement4.evaluate(fList1));
		assertFalse(statement4.evaluate(fList2));
		assertTrue(statement4.evaluate(fList3));
}

	@Test
	public void testMentionsPartitionNode() {
		DecomposedParameterStatement statement = new DecomposedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		assertTrue(statement.mentions(fPartition2));
		assertFalse(statement.mentions(fPartition1));
	}

	@Test
	public void testMentionsParameterNode() {
		DecomposedParameterStatement statement = new DecomposedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		ParameterNode parameter = new ParameterNode("name", "type", "0", false);
		assertTrue(statement.mentions(fParameter));
		assertFalse(statement.mentions(parameter));
	}

	@Test
	public void testGetCondition() {
		DecomposedParameterStatement statement = new DecomposedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		assertEquals(fPartition2, statement.getConditionValue());
	}

	@Test
	public void testGetRelation() {
		DecomposedParameterStatement statement = new DecomposedParameterStatement(fParameter, EStatementRelation.EQUAL, fPartition2);
		assertEquals(EStatementRelation.EQUAL, statement.getRelation());
	}

	@Test
	public void compareTest(){
		ParameterNode c1 = new ParameterNode("name", "type", "0", true);
		ParameterNode c2 = new ParameterNode("name", "type", "0", true);
		
		ChoiceNode p1 = new ChoiceNode("name", "value");
		ChoiceNode p2 = new ChoiceNode("name", "value");
		
		DecomposedParameterStatement s1 = new DecomposedParameterStatement(c1, EStatementRelation.NOT, p1);
		DecomposedParameterStatement s2 = new DecomposedParameterStatement(c2, EStatementRelation.NOT, p2);
		
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
