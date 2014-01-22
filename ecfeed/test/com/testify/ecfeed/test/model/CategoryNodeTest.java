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

package com.testify.ecfeed.test.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class CategoryNodeTest{

	@Test
	public void getLeafPartitionsTest(){
		CategoryNode category = new CategoryNode("category", "type");
		
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
		
		category.addPartition(p1);
		category.addPartition(p2);
		category.addPartition(p3);
		p2.addPartition(p21);
		p2.addPartition(p22);
		p2.addPartition(p23);
		p3.addPartition(p31);
		p3.addPartition(p32);
		p3.addPartition(p33);
		p32.addPartition(p321);
		p32.addPartition(p322);
		p32.addPartition(p323);
		
		assertTrue(category.getLeafPartitions().contains(p1));
		assertTrue(category.getLeafPartitions().contains(p21));
		assertTrue(category.getLeafPartitions().contains(p22));
		assertTrue(category.getLeafPartitions().contains(p23));
		assertTrue(category.getLeafPartitions().contains(p31));
		assertTrue(category.getLeafPartitions().contains(p321));
		assertTrue(category.getLeafPartitions().contains(p322));
		assertTrue(category.getLeafPartitions().contains(p323));
		assertTrue(category.getLeafPartitions().contains(p33));
		
		assertFalse(category.getLeafPartitions().contains(p2));
		assertFalse(category.getLeafPartitions().contains(p3));
		assertFalse(category.getLeafPartitions().contains(p32));
}
}
