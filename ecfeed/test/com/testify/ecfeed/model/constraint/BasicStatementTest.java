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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.MethodNode;

public class BasicStatementTest {

	private class StatementImplementation extends BasicStatement{
		@Override
		public String getLeftOperandName() {
			return null;
		}
		@Override
		public BasicStatement getCopy(){
			return null;
		}
		@Override
		public boolean updateReferences(MethodNode method){
			return true;
		}
		@Override
		public boolean compare(IStatement statement) {
			return false;
		}
		@Override
		public Object accept(IStatementVisitor visitor) {
			return null;
		}
	}
	
	@Test
	public void testParent() {
		BasicStatement statement1 = new StatementImplementation();
		BasicStatement statement2 = new StatementImplementation();
		
		statement2.setParent(statement1);
		assertEquals(statement1, statement2.getParent());
	}

	@Test
	public void testGetChildren() {
		StatementArray array = new StatementArray(Operator.AND);
		BasicStatement statement2 = new StatementImplementation();
		BasicStatement statement3 = new StatementImplementation();

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
		BasicStatement statement2 = new StatementImplementation();
		BasicStatement statement3 = new StatementImplementation();

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
		BasicStatement statement2 = new StatementImplementation();
		BasicStatement statement3 = new StatementImplementation();

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
