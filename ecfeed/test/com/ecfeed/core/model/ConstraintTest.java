/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.StaticStatement;

public class ConstraintTest {
	@Test
	public void testEvaluate() {
		AbstractStatement trueStatement = new StaticStatement(true); 
		AbstractStatement falseStatement = new StaticStatement(false); 
		List<ChoiceNode> values = new ArrayList<ChoiceNode>();

		assertTrue(new Constraint(falseStatement, falseStatement).evaluate(values));
		assertTrue(new Constraint(falseStatement, trueStatement).evaluate(values));
		assertTrue(new Constraint(trueStatement, trueStatement).evaluate(values));
		assertFalse(new Constraint(trueStatement, falseStatement).evaluate(values));
	}

	@Test
	public void testSetPremise() {
		AbstractStatement statement1 = new StaticStatement(true); 
		AbstractStatement statement2 = new StaticStatement(false); 
		AbstractStatement statement3 = new StaticStatement(false);
		
		Constraint constraint = new Constraint(statement1, statement2);
		assertTrue(constraint.getPremise().equals(statement1));
		constraint.setPremise(statement3);
		assertTrue(constraint.getPremise().equals(statement3));
	}

	@Test
	public void testSetConsequence() {
		AbstractStatement statement1 = new StaticStatement(true); 
		AbstractStatement statement2 = new StaticStatement(false); 
		AbstractStatement statement3 = new StaticStatement(false);
		
		Constraint constraint = new Constraint(statement1, statement2);
		assertTrue(constraint.getConsequence().equals(statement2));
		constraint.setConsequence(statement3);
		assertTrue(constraint.getConsequence().equals(statement3));
	}

	@Test
	public void testMentions() {
		ChoiceNode choice = new ChoiceNode("choice", null);
		MethodParameterNode parameter = new MethodParameterNode("parameter", "type", "0", false);
		parameter.addChoice(choice);

		AbstractStatement mentioningStatement = new ChoicesParentStatement(parameter, EStatementRelation.EQUAL, choice);
		AbstractStatement notMentioningStatement = new StaticStatement(false);
		
		assertTrue(new Constraint(mentioningStatement, notMentioningStatement).mentions(parameter));
		assertTrue(new Constraint(mentioningStatement, notMentioningStatement).mentions(choice));
		
		assertTrue(new Constraint(notMentioningStatement, mentioningStatement).mentions(parameter));
		assertTrue(new Constraint(notMentioningStatement, mentioningStatement).mentions(choice));
		
		assertTrue(new Constraint(mentioningStatement, mentioningStatement).mentions(parameter));
		assertTrue(new Constraint(mentioningStatement, mentioningStatement).mentions(choice));
		
		assertFalse(new Constraint(notMentioningStatement, notMentioningStatement).mentions(parameter));
		assertFalse(new Constraint(notMentioningStatement, notMentioningStatement).mentions(choice));
		
	}

}
