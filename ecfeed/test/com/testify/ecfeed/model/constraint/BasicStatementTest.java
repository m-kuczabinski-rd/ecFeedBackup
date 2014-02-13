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

import java.util.List;

import org.junit.Test;

public class BasicStatementTest {

	@Test
	public void testParent() {
		BasicStatement statement1 = new BasicStatement();
		BasicStatement statement2 = new BasicStatement();
		
		statement2.setParent(statement1);
		assertEquals(statement1, statement2.getParent());
	}

	@Test
	public void testGetChildren() {
		StatementArray array = new StatementArray(Operator.AND);
		BasicStatement statement2 = new BasicStatement();
		BasicStatement statement3 = new BasicStatement();

		array.addStatement(statement2);
		array.addStatement(statement3);
		
		List<BasicStatement> children = array.getChildren();
		assertEquals(2, children.size());
		assertTrue(children.contains(statement2));
		assertTrue(children.contains(statement3));
	}

	@Test
	public void testReplaceChild() {
		StatementArray array = new StatementArray(Operator.AND);
		BasicStatement statement2 = new BasicStatement();
		BasicStatement statement3 = new BasicStatement();

		array.addStatement(statement2);
		List<BasicStatement> children = array.getChildren();
		assertEquals(1, children.size());
		assertTrue(children.contains(statement2));
		
		array.replaceChild(statement2, statement3);
		children = array.getChildren();
		assertEquals(1, children.size());
		assertTrue(children.contains(statement3));
	}

	@Test
	public void testRemoveChild() {
		StatementArray array = new StatementArray(Operator.AND);
		BasicStatement statement2 = new BasicStatement();
		BasicStatement statement3 = new BasicStatement();

		array.addStatement(statement2);
		array.addStatement(statement3);
		List<BasicStatement> children = array.getChildren();
		assertEquals(2, children.size());
		assertTrue(children.contains(statement2));
		assertTrue(children.contains(statement3));
		
		array.removeChild(statement2);
		children = array.getChildren();
		assertEquals(1, children.size());
		assertFalse(children.contains(statement2));
		assertTrue(children.contains(statement3));
		
	}
}
