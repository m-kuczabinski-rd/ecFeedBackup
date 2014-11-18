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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.BasicStatement;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.DecomposedParameterStatement;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.StaticStatement;

public class ConstraintTest {
	@Test
	public void testEvaluate() {
		BasicStatement trueStatement = new StaticStatement(true); 
		BasicStatement falseStatement = new StaticStatement(false); 
		List<ChoiceNode> values = new ArrayList<ChoiceNode>();

		assertTrue(new Constraint(falseStatement, falseStatement).evaluate(values));
		assertTrue(new Constraint(falseStatement, trueStatement).evaluate(values));
		assertTrue(new Constraint(trueStatement, trueStatement).evaluate(values));
		assertFalse(new Constraint(trueStatement, falseStatement).evaluate(values));
	}

	@Test
	public void testSetPremise() {
		BasicStatement statement1 = new StaticStatement(true); 
		BasicStatement statement2 = new StaticStatement(false); 
		BasicStatement statement3 = new StaticStatement(false);
		
		Constraint constraint = new Constraint(statement1, statement2);
		assertTrue(constraint.getPremise().equals(statement1));
		constraint.setPremise(statement3);
		assertTrue(constraint.getPremise().equals(statement3));
	}

	@Test
	public void testSetConsequence() {
		BasicStatement statement1 = new StaticStatement(true); 
		BasicStatement statement2 = new StaticStatement(false); 
		BasicStatement statement3 = new StaticStatement(false);
		
		Constraint constraint = new Constraint(statement1, statement2);
		assertTrue(constraint.getConsequence().equals(statement2));
		constraint.setConsequence(statement3);
		assertTrue(constraint.getConsequence().equals(statement3));
	}

	@Test
	public void testMentions() {
		ChoiceNode partition = new ChoiceNode("partition", null);
		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		parameter.addPartition(partition);

		BasicStatement mentioningStatement = new DecomposedParameterStatement(parameter, EStatementRelation.EQUAL, partition);
		BasicStatement notMentioningStatement = new StaticStatement(false);
		
		assertTrue(new Constraint(mentioningStatement, notMentioningStatement).mentions(parameter));
		assertTrue(new Constraint(mentioningStatement, notMentioningStatement).mentions(partition));
		
		assertTrue(new Constraint(notMentioningStatement, mentioningStatement).mentions(parameter));
		assertTrue(new Constraint(notMentioningStatement, mentioningStatement).mentions(partition));
		
		assertTrue(new Constraint(mentioningStatement, mentioningStatement).mentions(parameter));
		assertTrue(new Constraint(mentioningStatement, mentioningStatement).mentions(partition));
		
		assertFalse(new Constraint(notMentioningStatement, notMentioningStatement).mentions(parameter));
		assertFalse(new Constraint(notMentioningStatement, notMentioningStatement).mentions(partition));
		
	}

}
