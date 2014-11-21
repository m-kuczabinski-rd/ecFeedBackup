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

import org.junit.BeforeClass;
import org.junit.Test;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.EStatementOperator;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.ChoicesParentStatement;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;

public class StatementArrayTest {

	private static MethodNode fMethod;
	private static MethodParameterNode fParameter1;
	private static ChoiceNode fChoice11;
	private static ChoiceNode fChoice12;
	private static ChoiceNode fChoice13;
	private static MethodParameterNode fParameter2;
	private static ChoiceNode fChoice21;
	private static ChoiceNode fChoice22;
	private static ChoiceNode fChoice23;

	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method");
		fParameter1 = new MethodParameterNode("parameter", "type", "0", false);
		fChoice11 = new ChoiceNode("choice11", null);
		fChoice12 = new ChoiceNode("choice12", null);
		fChoice13 = new ChoiceNode("choice13", null);
		fParameter1.addChoice(fChoice11);
		fParameter1.addChoice(fChoice12);
		fParameter1.addChoice(fChoice13);
		fParameter2 = new MethodParameterNode("parameter", "type", "0", false);
		fChoice21 = new ChoiceNode("choice21", null);
		fChoice22 = new ChoiceNode("choice22", null);
		fChoice23 = new ChoiceNode("choice23", null);
		fParameter2.addChoice(fChoice21);
		fParameter2.addChoice(fChoice22);
		fParameter2.addChoice(fChoice23);
		fMethod.addParameter(fParameter1);
		fMethod.addParameter(fParameter2);
	}
	

	@Test
	public void testEvaluate() {
		StatementArray arrayOr = new StatementArray(EStatementOperator.OR);
		StatementArray arrayAnd = new StatementArray(EStatementOperator.AND);
		ChoicesParentStatement statement1 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice11);
		ChoicesParentStatement statement2 = new ChoicesParentStatement(fParameter2, EStatementRelation.EQUAL, fChoice21);
		arrayOr.addStatement(statement1);
		arrayOr.addStatement(statement2);
		arrayAnd.addStatement(statement1);
		arrayAnd.addStatement(statement2);
		
		List<ChoiceNode> bothFulfill = new ArrayList<ChoiceNode>();
		bothFulfill.add(fChoice11);
		bothFulfill.add(fChoice21);
		assertTrue(arrayOr.evaluate(bothFulfill));
		assertTrue(arrayAnd.evaluate(bothFulfill));

		List<ChoiceNode> oneFulfills = new ArrayList<ChoiceNode>();
		oneFulfills.add(fChoice12);
		oneFulfills.add(fChoice21);
		assertTrue(arrayOr.evaluate(oneFulfills));
		assertFalse(arrayAnd.evaluate(oneFulfills));

		List<ChoiceNode> noneFulfills = new ArrayList<ChoiceNode>();
		noneFulfills.add(fChoice12);
		noneFulfills.add(fChoice22);
		assertFalse(arrayOr.evaluate(noneFulfills));
		assertFalse(arrayAnd.evaluate(noneFulfills));
	}

	@Test
	public void testGetChildren() {
		StatementArray array = new StatementArray(EStatementOperator.OR);
		ChoicesParentStatement statement1 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice11);
		ChoicesParentStatement statement2 = new ChoicesParentStatement(fParameter2, EStatementRelation.EQUAL, fChoice21);
		ChoicesParentStatement statement3 = new ChoicesParentStatement(fParameter2, EStatementRelation.EQUAL, fChoice21);
		array.addStatement(statement1);
		array.addStatement(statement2);
		
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
		assertFalse(array.getChildren().contains(statement3));
	}

	@Test
	public void testMentionsChoiceNode() {
		StatementArray array = new StatementArray(EStatementOperator.OR);
		ChoicesParentStatement statement1 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice11);
		ChoicesParentStatement statement2 = new ChoicesParentStatement(fParameter2, EStatementRelation.EQUAL, fChoice21);
		array.addStatement(statement1);
		array.addStatement(statement2);
		assertTrue(array.mentions(fChoice11));
		assertFalse(array.mentions(fChoice13));
	}

	@Test
	public void testMentionsParameterNode() {
		StatementArray array = new StatementArray(EStatementOperator.OR);
		ChoicesParentStatement statement1 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice11);
		array.addStatement(statement1);
		assertTrue(array.mentions(fChoice11.getParameter()));
		assertFalse(array.mentions(fChoice21.getParameter()));
	}

	@Test
	public void testSetOperator() {
		StatementArray array = new StatementArray(EStatementOperator.OR);
		ChoicesParentStatement statement1 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice11);
		ChoicesParentStatement statement2 = new ChoicesParentStatement(fParameter2, EStatementRelation.EQUAL, fChoice21);
		array.addStatement(statement1);
		array.addStatement(statement2);
		assertEquals(EStatementOperator.OR, array.getOperator());
		array.setOperator(EStatementOperator.AND);
		assertEquals(EStatementOperator.AND, array.getOperator());
		//check that children statements were not changed
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
	}

	@Test
	public void testReplaceChild() {
		StatementArray array = new StatementArray(EStatementOperator.OR);
		ChoicesParentStatement statement1 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice11);
		ChoicesParentStatement statement2 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice12);
		ChoicesParentStatement statement3 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice13);
		array.addStatement(statement1);
		array.addStatement(statement2);
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
		assertFalse(array.getChildren().contains(statement3));
		
		array.replaceChild(statement2, statement3);
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertFalse(array.getChildren().contains(statement2));
		assertTrue(array.getChildren().contains(statement3));
	}

	@Test
	public void testRemoveChild() {
		StatementArray array = new StatementArray(EStatementOperator.OR);
		ChoicesParentStatement statement1 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice11);
		ChoicesParentStatement statement2 = new ChoicesParentStatement(fParameter1, EStatementRelation.EQUAL, fChoice12);
		array.addStatement(statement1);
		array.addStatement(statement2);
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
		
		array.removeChild(statement2);
		assertEquals(1, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertFalse(array.getChildren().contains(statement2));
	}

	/*****************compare()**********************/
	@Test
	public void compareOperatorTest(){
		StatementArray or1 = new StatementArray(EStatementOperator.OR);
		StatementArray or2 = new StatementArray(EStatementOperator.OR);
		StatementArray and1 = new StatementArray(EStatementOperator.AND);
		StatementArray and2 = new StatementArray(EStatementOperator.AND);
		
		assertTrue(or1.compare(or2));
		assertTrue(and1.compare(and2));
		assertFalse(or1.compare(and1));
		assertFalse(and1.compare(or1));
	}

	@Test
	public void compareChildrenTest(){
		StatementArray s1 = new StatementArray(EStatementOperator.OR);
		StatementArray s2 = new StatementArray(EStatementOperator.OR);
		
		StaticStatement ss1 = new StaticStatement(true);
		StaticStatement ss2 = new StaticStatement(true);
		assertTrue(s1.compare(s2));
		
		s1.addStatement(ss1);
		assertFalse(s1.compare(s2));
		s2.addStatement(ss2);
		assertTrue(s1.compare(s2));
		
		ss1.setValue(false);;
		assertFalse(s1.compare(s2));
		ss2.setValue(false);
		assertTrue(s1.compare(s2));
	}
}
