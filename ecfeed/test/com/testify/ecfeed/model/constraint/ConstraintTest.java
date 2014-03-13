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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class ConstraintTest {
	@Test
	public void testEvaluate() {
		BasicStatement trueStatement = new StaticStatement(true); 
		BasicStatement falseStatement = new StaticStatement(false); 
		List<PartitionNode> values = new ArrayList<PartitionNode>();

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
		PartitionNode partition = new PartitionNode("partition", null);
		CategoryNode category = new CategoryNode("category", "type");
		category.addPartition(partition);

		BasicStatement mentioningStatement = new ConditionStatement(category, Relation.EQUAL, partition);
		BasicStatement notMentioningStatement = new StaticStatement(false);
		
		assertTrue(new Constraint(mentioningStatement, notMentioningStatement).mentions(category));
		assertTrue(new Constraint(mentioningStatement, notMentioningStatement).mentions(partition));
		
		assertTrue(new Constraint(notMentioningStatement, mentioningStatement).mentions(category));
		assertTrue(new Constraint(notMentioningStatement, mentioningStatement).mentions(partition));
		
		assertTrue(new Constraint(mentioningStatement, mentioningStatement).mentions(category));
		assertTrue(new Constraint(mentioningStatement, mentioningStatement).mentions(partition));
		
		assertFalse(new Constraint(notMentioningStatement, notMentioningStatement).mentions(category));
		assertFalse(new Constraint(notMentioningStatement, notMentioningStatement).mentions(partition));
		
	}

}
