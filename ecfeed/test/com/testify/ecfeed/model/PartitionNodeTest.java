/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class PartitionNodeTest{
	@Test
	public void testValue() {
		PartitionNode partition = new PartitionNode("name", "value");
		assertEquals("value", partition.getValue());
		partition.setValue("new value");
		assertEquals("new value", partition.getValue());
	}

	
	@Test
	public void testGetCategory(){
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("name", "type");
		PartitionNode p1 = new PartitionNode("p1", 0);
		PartitionNode p11 = new PartitionNode("p11", 0);
		PartitionNode p111 = new PartitionNode("p111", 0);
		
		method.addCategory(category);
		category.addPartition(p1);
		p1.addPartition(p11);
		p11.addPartition(p111);
		
		assertEquals(p11, p111.getParent());
		assertEquals(p1, p11.getParent());
		assertEquals(category, p1.getParent());
		assertEquals(method, category.getParent());
		
		assertEquals(category, p1.getCategory());
		assertEquals(category, p11.getCategory());
		assertEquals(category, p111.getCategory());
	}
	
	
	@Test
	public void testLevel(){
		MethodNode method = new MethodNode("method");
		CategoryNode category = new CategoryNode("name", "type");
		PartitionNode p1 = new PartitionNode("p1", 0);
		PartitionNode p11 = new PartitionNode("p11", 0);
		PartitionNode p111 = new PartitionNode("p111", 0);
		
		method.addCategory(category);
		category.addPartition(p1);
		p1.addPartition(p11);
		p11.addPartition(p111);
		
		assertEquals(0, p1.level());
		assertEquals(1, p11.level());
		assertEquals(2, p111.level());
	}
	
	@Test
	public void isAncestorTest(){
		PartitionNode p = new PartitionNode("p", 0);
		PartitionNode p1 = new PartitionNode("p1", 0);
		PartitionNode p2 = new PartitionNode("p2", 0);
		PartitionNode p11 = new PartitionNode("p1", 0);
		PartitionNode p12 = new PartitionNode("p2", 0);
		PartitionNode p21 = new PartitionNode("p1", 0);
		PartitionNode p22 = new PartitionNode("p2", 0);
		
		p.addPartition(p1);
		p.addPartition(p2);
		p1.addPartition(p11);
		p1.addPartition(p12);
		p2.addPartition(p21);
		p2.addPartition(p22);
		
		assertTrue(p.isAncestor(p1));
		assertTrue(p.isAncestor(p2));
		assertTrue(p.isAncestor(p11));
		assertTrue(p.isAncestor(p12));
		assertTrue(p.isAncestor(p21));
		assertTrue(p.isAncestor(p22));
		assertFalse(p.isAncestor(p));
		
		assertTrue(p1.isAncestor(p11));
		assertTrue(p1.isAncestor(p12));
		assertFalse(p1.isAncestor(p21));
		assertFalse(p1.isAncestor(p21));
		assertFalse(p1.isAncestor(p));
		assertFalse(p1.isAncestor(p1));
		assertFalse(p1.isAncestor(p2));

		assertTrue(p2.isAncestor(p21));
		assertTrue(p2.isAncestor(p22));
		assertFalse(p2.isAncestor(p11));
		assertFalse(p2.isAncestor(p11));
		assertFalse(p2.isAncestor(p1));
		assertFalse(p2.isAncestor(p2));
		assertFalse(p2.isAncestor(p));
	}
	
	@Test
	public void isDescendantTest(){
		PartitionNode p = new PartitionNode("p", 0);
		PartitionNode p1 = new PartitionNode("p1", 0);
		PartitionNode p2 = new PartitionNode("p2", 0);
		PartitionNode p11 = new PartitionNode("p1", 0);
		PartitionNode p12 = new PartitionNode("p2", 0);
		PartitionNode p21 = new PartitionNode("p1", 0);
		PartitionNode p22 = new PartitionNode("p2", 0);
		
		p.addPartition(p1);
		p.addPartition(p2);
		p1.addPartition(p11);
		p1.addPartition(p12);
		p2.addPartition(p21);
		p2.addPartition(p22);
		
		assertTrue(p11.isDescendant(p1));
		assertTrue(p11.isDescendant(p));
		assertTrue(p12.isDescendant(p1));
		assertTrue(p12.isDescendant(p));
		assertFalse(p11.isDescendant(p2));
		assertFalse(p11.isDescendant(p11));
	}
	
	@Test
	public void getLeafsTest(){
		PartitionNode p = new PartitionNode("p", 0);
		
		PartitionNode p1 = new PartitionNode("p1", 0);
		PartitionNode p2 = new PartitionNode("p1", 0);
		PartitionNode p3 = new PartitionNode("p1", 0);
		
		PartitionNode p21 = new PartitionNode("p21", 0);
		PartitionNode p22 = new PartitionNode("p22", 0);
		PartitionNode p23 = new PartitionNode("p23", 0);

		PartitionNode p31 = new PartitionNode("p31", 0);
		PartitionNode p32 = new PartitionNode("p32", 0);
		PartitionNode p33 = new PartitionNode("p33", 0);

		PartitionNode p321 = new PartitionNode("p321", 0);
		PartitionNode p322 = new PartitionNode("p322", 0);
		PartitionNode p323 = new PartitionNode("p323", 0);
		
		p.addPartition(p1);
		p.addPartition(p2);
		p.addPartition(p3);
		p2.addPartition(p21);
		p2.addPartition(p22);
		p2.addPartition(p23);
		p3.addPartition(p31);
		p3.addPartition(p32);
		p3.addPartition(p33);
		p32.addPartition(p321);
		p32.addPartition(p322);
		p32.addPartition(p323);
		
		assertTrue(p.getLeafs().contains(p1));
		assertTrue(p.getLeafs().contains(p21));
		assertTrue(p.getLeafs().contains(p22));
		assertTrue(p.getLeafs().contains(p23));
		assertTrue(p.getLeafs().contains(p31));
		assertTrue(p.getLeafs().contains(p321));
		assertTrue(p.getLeafs().contains(p322));
		assertTrue(p.getLeafs().contains(p323));
		assertTrue(p.getLeafs().contains(p33));
		
		assertFalse(p.getLeafs().contains(p2));
		assertFalse(p.getLeafs().contains(p3));
		assertFalse(p.getLeafs().contains(p32));
	}
	
	@Test
	public void getQualifiedNameTest(){
		PartitionNode p = new PartitionNode("p", 0);
		PartitionNode q = new PartitionNode("q", 0);
		
		p.addPartition(q);
		
		assertEquals(p.getName() + ":" + q.getName(), q.getQualifiedName());
	}
	
}
