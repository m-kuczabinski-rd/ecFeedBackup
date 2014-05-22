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

import java.util.List;

import org.junit.Test;

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
		CategoryNode category = new CategoryNode("name", "type", false);
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
		CategoryNode category = new CategoryNode("name", "type", false);
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
		
		assertTrue(p.getLeafPartitions().contains(p1));
		assertTrue(p.getLeafPartitions().contains(p21));
		assertTrue(p.getLeafPartitions().contains(p22));
		assertTrue(p.getLeafPartitions().contains(p23));
		assertTrue(p.getLeafPartitions().contains(p31));
		assertTrue(p.getLeafPartitions().contains(p321));
		assertTrue(p.getLeafPartitions().contains(p322));
		assertTrue(p.getLeafPartitions().contains(p323));
		assertTrue(p.getLeafPartitions().contains(p33));
		
		assertFalse(p.getLeafPartitions().contains(p2));
		assertFalse(p.getLeafPartitions().contains(p3));
		assertFalse(p.getLeafPartitions().contains(p32));
	}
	
	@Test
	public void getQualifiedNameTest(){
		PartitionNode p = new PartitionNode("p", 0);
		PartitionNode q = new PartitionNode("q", 0);
		
		p.addPartition(q);
		
		assertEquals(p.getName() + ":" + q.getName(), q.getQualifiedName());
	}
	
	@Test
	public void getAllDescendantsTest(){
		PartitionNode p = new PartitionNode("p", 0);
		PartitionNode p1 = new PartitionNode("p1", 0);
		PartitionNode p11 = new PartitionNode("p11", 0);
		PartitionNode p12 = new PartitionNode("p12", 0);
		PartitionNode p111 = new PartitionNode("p111", 0);
		PartitionNode p112 = new PartitionNode("p112", 0);
		PartitionNode p2 = new PartitionNode("p2", 0);
		
		p.addPartition(p1);
		p.addPartition(p2);
		p1.addPartition(p11);
		p1.addPartition(p12);
		p11.addPartition(p111);
		p11.addPartition(p112);
		
		List<String> names = p.getAllPartitionNames();
		assertTrue(names.contains("p:p1"));
		assertTrue(names.contains("p:p1:p11"));
		assertTrue(names.contains("p:p1:p12"));
		assertTrue(names.contains("p:p1:p11:p111"));
		assertTrue(names.contains("p:p1:p11:p112"));
		assertTrue(names.contains("p:p2"));
		
	}
	
	@Test 
	public void labelTest(){
		PartitionNode p = new PartitionNode("p", 0);
		PartitionNode p1 = new PartitionNode("p1", 0);
		PartitionNode p2 = new PartitionNode("p2", 0);
		PartitionNode p12 = new PartitionNode("p12", 0);
		PartitionNode p11 = new PartitionNode("p11", 0);
		
		p.addPartition(p1);
		p.addPartition(p2);
		p1.addPartition(p11);
		p1.addPartition(p12);
		
		assertTrue(p.addLabel("pLabel.0"));
		assertTrue(p.addLabel("pLabel.1"));
		assertTrue(p1.addLabel("p1Label.0"));
		assertTrue(p1.addLabel("p1Label.1"));
		assertTrue(p2.addLabel("p2Label.0"));
		assertTrue(p2.addLabel("p2Label.1"));
		assertTrue(p11.addLabel("p11Label.0"));
		assertTrue(p11.addLabel("p11Label.1"));
		assertTrue(p12.addLabel("p12Label.0"));
		assertTrue(p12.addLabel("p12Label.1"));
		
		assertFalse(p.addLabel("pLabel.0"));
		assertFalse(p12.addLabel("pLabel.0"));

		
		//getLabels
		assertTrue(p12.getLabels().contains("p12Label.0"));
		assertTrue(p12.getLabels().contains("p12Label.1"));
		assertFalse(p12.getLabels().contains("p1Label.0"));
		assertFalse(p12.getLabels().contains("p1Label.1"));

		//getAllLabels
		assertTrue(p12.getAllLabels().contains("p12Label.0"));
		assertTrue(p12.getAllLabels().contains("p12Label.1"));
		assertTrue(p12.getAllLabels().contains("p1Label.0"));
		assertTrue(p12.getAllLabels().contains("p1Label.1"));
		assertTrue(p12.getAllLabels().contains("pLabel.0"));
		assertTrue(p12.getAllLabels().contains("pLabel.1"));
		
		assertFalse(p12.getAllLabels().contains("p11Label.0"));
		assertFalse(p12.getAllLabels().contains("p11Label.1"));
		assertFalse(p12.getAllLabels().contains("p2Label.0"));
		assertFalse(p12.getAllLabels().contains("p2Label.1"));
		
		//getInheritedLabels test
		assertFalse(p12.getInheritedLabels().contains("p12Label.0"));
		assertFalse(p12.getInheritedLabels().contains("p12Label.1"));
		assertTrue(p12.getInheritedLabels().contains("p1Label.0"));
		assertTrue(p12.getInheritedLabels().contains("p1Label.1"));
		assertTrue(p12.getInheritedLabels().contains("pLabel.0"));
		assertTrue(p12.getInheritedLabels().contains("pLabel.1"));
		
		//getDescendingLabels test
		assertTrue(p.getAllDescendingLabels().contains("pLabel.0"));
		assertTrue(p.getAllDescendingLabels().contains("pLabel.1"));
		assertTrue(p.getAllDescendingLabels().contains("p1Label.0"));
		assertTrue(p.getAllDescendingLabels().contains("p1Label.1"));
		assertTrue(p.getAllDescendingLabels().contains("p2Label.0"));
		assertTrue(p.getAllDescendingLabels().contains("p2Label.1"));
		assertTrue(p.getAllDescendingLabels().contains("p11Label.0"));
		assertTrue(p.getAllDescendingLabels().contains("p11Label.1"));
		assertTrue(p.getAllDescendingLabels().contains("p12Label.0"));
		assertTrue(p.getAllDescendingLabels().contains("p12Label.1"));

		assertFalse(p1.getAllDescendingLabels().contains("pLabel.0"));
		assertFalse(p1.getAllDescendingLabels().contains("pLabel.1"));
		assertTrue(p1.getAllDescendingLabels().contains("p1Label.0"));
		assertTrue(p1.getAllDescendingLabels().contains("p1Label.1"));
		assertFalse(p1.getAllDescendingLabels().contains("p2Label.0"));
		assertFalse(p1.getAllDescendingLabels().contains("p2Label.1"));
		assertTrue(p1.getAllDescendingLabels().contains("p11Label.0"));
		assertTrue(p1.getAllDescendingLabels().contains("p11Label.1"));
		assertTrue(p1.getAllDescendingLabels().contains("p12Label.0"));
		assertTrue(p1.getAllDescendingLabels().contains("p12Label.1"));
		
	}
	
}
