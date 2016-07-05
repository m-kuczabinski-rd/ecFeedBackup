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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.StaticStatement;

public class StaticStatementTest {

	@Test
	public void testEvaluate() {
		List<ChoiceNode> list = new ArrayList<ChoiceNode>();
		
		StaticStatement trueStatement = new StaticStatement(true);
		assertTrue(trueStatement.evaluate(list));
		StaticStatement falseStatement = new StaticStatement(false);
		assertFalse(falseStatement.evaluate(list));
	}

	@Test
	public void testSetValue() {
		List<ChoiceNode> list = new ArrayList<ChoiceNode>();
		
		StaticStatement statement = new StaticStatement(true);
		assertTrue(statement.evaluate(list));
		
		statement.setValue(false);
		assertFalse(statement.evaluate(list));
	}

	@Test
	public void compareTest(){
		StaticStatement true1 = new StaticStatement(true);
		StaticStatement true2 = new StaticStatement(true);
		StaticStatement false1 = new StaticStatement(false);
		StaticStatement false2 = new StaticStatement(false);
		
		assertTrue(true1.compare(true2));
		assertTrue(false1.compare(false2));
		assertFalse(true1.compare(false1));
	}
}
