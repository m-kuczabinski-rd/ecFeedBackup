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

package com.testify.ecfeed.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;

public class ChoiceStatementTest {

	private static MethodNode fMethod;
	private static MethodParameterNode fParameter;
	private static ChoiceNode fChoice1;
	private static ChoiceNode fChoice2;
	private static ChoiceNode fChoice3;
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
		fP1.addChoice(fP11);
		fP1.addChoice(fP12);
		fP1.addChoice(fP13);

		fP2.addChoice(fP21);
		fP2.addChoice(fP22);
		fP2.addChoice(fP23);

		fP22.addChoice(fP221);
		fP22.addChoice(fP222);
		fP22.addChoice(fP223);

		fP3.addChoice(fP31);
		fP3.addChoice(fP32);
		fP3.addChoice(fP33);

		fParameter.addChoice(fP1);
		fParameter.addChoice(fP2);
		fParameter.addChoice(fP3);
		
		fMethod.addParameter(fParameter);
	}

	@Test
	public void equalsTest(){
		ChoicesParentStatement statement = new ChoicesParentStatement(fParameter, EStatementRelation.EQUAL, fP22);
		
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
		ChoicesParentStatement statement = new ChoicesParentStatement(fParameter, EStatementRelation.NOT, fP22);
		
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
		fParameter = new MethodParameterNode("parameter", "type", "0", false);
		fChoice1 = new ChoiceNode("choice1", null);
		fChoice2 = new ChoiceNode("choice2", null);
		fChoice3 = new ChoiceNode("choice3", null);
		fParameter.addChoice(fChoice1);
		fParameter.addChoice(fChoice2);
		fParameter.addChoice(fChoice3);
		fMethod.addParameter(fParameter);
		
		fList1 = new ArrayList<ChoiceNode>();
		fList1.add(fChoice1);
		fList2 = new ArrayList<ChoiceNode>();
		fList2.add(fChoice2);
		fList3 = new ArrayList<ChoiceNode>();
		fList3.add(fChoice3);
	}
	
	
	@Test
	public void testEvaluate() {

		ChoicesParentStatement statement1 = new ChoicesParentStatement(fParameter, EStatementRelation.EQUAL, fChoice2);
		assertFalse(statement1.evaluate(fList1));
		assertTrue(statement1.evaluate(fList2));
		assertFalse(statement1.evaluate(fList3));

		ChoicesParentStatement statement4 = new ChoicesParentStatement(fParameter, EStatementRelation.NOT, fChoice2);
		assertTrue(statement4.evaluate(fList1));
		assertFalse(statement4.evaluate(fList2));
		assertTrue(statement4.evaluate(fList3));
}

	@Test
	public void testMentionsChoiceNode() {
		ChoicesParentStatement statement = new ChoicesParentStatement(fParameter, EStatementRelation.EQUAL, fChoice2);
		assertTrue(statement.mentions(fChoice2));
		assertFalse(statement.mentions(fChoice1));
	}

	@Test
	public void testMentionsParameterNode() {
		ChoicesParentStatement statement = new ChoicesParentStatement(fParameter, EStatementRelation.EQUAL, fChoice2);
		MethodParameterNode parameter = new MethodParameterNode("name", "type", "0", false);
		assertTrue(statement.mentions(fParameter));
		assertFalse(statement.mentions(parameter));
	}

	@Test
	public void testGetCondition() {
		ChoicesParentStatement statement = new ChoicesParentStatement(fParameter, EStatementRelation.EQUAL, fChoice2);
		assertEquals(fChoice2, statement.getConditionValue());
	}

	@Test
	public void testGetRelation() {
		ChoicesParentStatement statement = new ChoicesParentStatement(fParameter, EStatementRelation.EQUAL, fChoice2);
		assertEquals(EStatementRelation.EQUAL, statement.getRelation());
	}

	@Test
	public void compareTest(){
		MethodParameterNode c1 = new MethodParameterNode("name", "type", "0", true);
		MethodParameterNode c2 = new MethodParameterNode("name", "type", "0", true);
		
		ChoiceNode p1 = new ChoiceNode("name", "value");
		ChoiceNode p2 = new ChoiceNode("name", "value");
		
		ChoicesParentStatement s1 = new ChoicesParentStatement(c1, EStatementRelation.NOT, p1);
		ChoicesParentStatement s2 = new ChoicesParentStatement(c2, EStatementRelation.NOT, p2);
		
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
