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
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class LabelStatementTest {
	
	@Test
	public void evaluateTest(){
		PartitionNode q = new PartitionNode("q", 0);
		
		PartitionNode p = new PartitionNode("p", 0);
		PartitionNode p1 = new PartitionNode("p1", 0);
		PartitionNode p2 = new PartitionNode("p2", 0);
		PartitionNode p11 = new PartitionNode("p11", 0);
		PartitionNode p12 = new PartitionNode("p12", 0);
		PartitionNode p21 = new PartitionNode("p21", 0);
		PartitionNode p22 = new PartitionNode("p22", 0);
		
		p.addPartition(p1);
		p.addPartition(p2);
		p1.addPartition(p11);
		p1.addPartition(p12);
		p2.addPartition(p21);
		p2.addPartition(p22);
		
		p.addLabel("p");
		p1.addLabel("p1");
		p2.addLabel("p2");
		p11.addLabel("p11");
		p12.addLabel("p12");
		p21.addLabel("p21");
		p22.addLabel("p22");
		
		CategoryNode c1 = new CategoryNode("c1", "type");
		c1.addPartition(p);
		CategoryNode c2 = new CategoryNode("c2", "type");
		c2.addPartition(q);
		
		MethodNode method = new MethodNode("method");
		method.addCategory(c1);
		method.addCategory(c2);

		List<PartitionNode> pq = new ArrayList<PartitionNode>();
		pq.add(p); pq.add(q);
		
		List<PartitionNode> p1q = new ArrayList<PartitionNode>();
		p1q.add(p1); p1q.add(q);
		
		List<PartitionNode> p2q = new ArrayList<PartitionNode>();
		p2q.add(p2); p2q.add(q);
		
		List<PartitionNode> p11q = new ArrayList<PartitionNode>();
		p11q.add(p11); p11q.add(q);
		
		List<PartitionNode> p21q = new ArrayList<PartitionNode>();
		p21q.add(p21); p21q.add(q);
		
		ConditionStatement pEqual = new ConditionStatement(c1, Relation.EQUAL, "p");
		ConditionStatement pNotEqual = new ConditionStatement(c1, Relation.NOT, "p");
		
		ConditionStatement p1Equal = new ConditionStatement(c1, Relation.EQUAL, "p1");
		ConditionStatement p1NotEqual = new ConditionStatement(c1, Relation.NOT, "p1");
		
		ConditionStatement p11Equal = new ConditionStatement(c1, Relation.EQUAL, "p11");
		ConditionStatement p11NotEqual = new ConditionStatement(c1, Relation.NOT, "p11");
		
		//Check that all pEqual statements evaluates to true for all vectors
		assertTrue(pEqual.evaluate(pq));
		assertTrue(pEqual.evaluate(p1q));
		assertTrue(pEqual.evaluate(p2q));
		assertTrue(pEqual.evaluate(p11q));
		assertTrue(pEqual.evaluate(p21q));
		//Check that all pNotEqual statements evaluates to true for all vectors
		assertFalse(pNotEqual.evaluate(pq));
		assertFalse(pNotEqual.evaluate(p1q));
		assertFalse(pNotEqual.evaluate(p11q));
		assertFalse(pNotEqual.evaluate(p2q));
		assertFalse(pNotEqual.evaluate(p21q));
		
		//Check that p1qEqual evaluates to true only for vectors with p1 and deriving
		assertFalse(p1Equal.evaluate(pq));
		assertTrue(p1Equal.evaluate(p1q));
		assertFalse(p1Equal.evaluate(p2q));
		assertTrue(p1Equal.evaluate(p11q));
		assertFalse(p1Equal.evaluate(p21q));
		//Check that p1qNotEqual evaluates to false for vectors with p1 and deriving
		assertTrue(p1NotEqual.evaluate(pq));
		assertFalse(p1NotEqual.evaluate(p1q));
		assertTrue(p1NotEqual.evaluate(p2q));
		assertFalse(p1NotEqual.evaluate(p11q));
		assertTrue(p1NotEqual.evaluate(p21q));
		
		//Check that p11qEqual evaluates to true only for vectors with p11
		assertFalse(p11Equal.evaluate(pq));
		assertFalse(p11Equal.evaluate(p1q));
		assertFalse(p11Equal.evaluate(p2q));
		assertTrue(p11Equal.evaluate(p11q));
		assertFalse(p11Equal.evaluate(p21q));
		//Check that p11qNotEqual evaluates to false for vectors with p11
		assertTrue(p11NotEqual.evaluate(pq));
		assertTrue(p11NotEqual.evaluate(p1q));
		assertTrue(p11NotEqual.evaluate(p2q));
		assertFalse(p11NotEqual.evaluate(p11q));
		assertTrue(p11NotEqual.evaluate(p21q));
	}

}
