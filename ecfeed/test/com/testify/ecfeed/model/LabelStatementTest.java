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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.DecomposedParameterStatement;
import com.testify.ecfeed.model.EStatementRelation;

public class LabelStatementTest {
	
	@Test
	public void evaluateTest(){
		ChoiceNode q = new ChoiceNode("q", "0");
		
		ChoiceNode p = new ChoiceNode("p", "0");
		ChoiceNode p1 = new ChoiceNode("p1", "0");
		ChoiceNode p2 = new ChoiceNode("p2", "0");
		ChoiceNode p11 = new ChoiceNode("p11", "0");
		ChoiceNode p12 = new ChoiceNode("p12", "0");
		ChoiceNode p21 = new ChoiceNode("p21", "0");
		ChoiceNode p22 = new ChoiceNode("p22", "0");
		
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
		
		ParameterNode c1 = new ParameterNode("c1", "type", "0", false);
		c1.addPartition(p);
		ParameterNode c2 = new ParameterNode("c2", "type", "0", false);
		c2.addPartition(q);
		
		MethodNode method = new MethodNode("method");
		method.addParameter(c1);
		method.addParameter(c2);

		List<ChoiceNode> pq = new ArrayList<ChoiceNode>();
		pq.add(p); pq.add(q);
		
		List<ChoiceNode> p1q = new ArrayList<ChoiceNode>();
		p1q.add(p1); p1q.add(q);
		
		List<ChoiceNode> p2q = new ArrayList<ChoiceNode>();
		p2q.add(p2); p2q.add(q);
		
		List<ChoiceNode> p11q = new ArrayList<ChoiceNode>();
		p11q.add(p11); p11q.add(q);
		
		List<ChoiceNode> p21q = new ArrayList<ChoiceNode>();
		p21q.add(p21); p21q.add(q);
		
		DecomposedParameterStatement pEqual = new DecomposedParameterStatement(c1, EStatementRelation.EQUAL, "p");
		DecomposedParameterStatement pNotEqual = new DecomposedParameterStatement(c1, EStatementRelation.NOT, "p");
		
		DecomposedParameterStatement p1Equal = new DecomposedParameterStatement(c1, EStatementRelation.EQUAL, "p1");
		DecomposedParameterStatement p1NotEqual = new DecomposedParameterStatement(c1, EStatementRelation.NOT, "p1");
		
		DecomposedParameterStatement p11Equal = new DecomposedParameterStatement(c1, EStatementRelation.EQUAL, "p11");
		DecomposedParameterStatement p11NotEqual = new DecomposedParameterStatement(c1, EStatementRelation.NOT, "p11");
		
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
