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

import com.testify.ecfeed.model.PartitionNode;

public class StaticStatementTest {

	@Test
	public void testEvaluate() {
		List<PartitionNode> list = new ArrayList<PartitionNode>();
		
		StaticStatement trueStatement = new StaticStatement(true);
		assertTrue(trueStatement.evaluate(list));
		StaticStatement falseStatement = new StaticStatement(false);
		assertFalse(falseStatement.evaluate(list));
	}

	@Test
	public void testSetValue() {
		List<PartitionNode> list = new ArrayList<PartitionNode>();
		
		StaticStatement statement = new StaticStatement(true);
		assertTrue(statement.evaluate(list));
		
		statement.setValue(false);
		assertFalse(statement.evaluate(list));
	}

}
