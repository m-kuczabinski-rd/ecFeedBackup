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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;

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
		
		p.addChoice(p1);
		p.addChoice(p2);
		p1.addChoice(p11);
		p1.addChoice(p12);
		p2.addChoice(p21);
		p2.addChoice(p22);
		
		p.addLabel("p");
		p1.addLabel("p1");
		p2.addLabel("p2");
		p11.addLabel("p11");
		p12.addLabel("p12");
		p21.addLabel("p21");
		p22.addLabel("p22");
		
		MethodParameterNode c1 = new MethodParameterNode("c1", "type", "0", false);
		c1.addChoice(p);
		MethodParameterNode c2 = new MethodParameterNode("c2", "type", "0", false);
		c2.addChoice(q);
		
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
		
		ChoicesParentStatement pEqual = new ChoicesParentStatement(c1, EStatementRelation.EQUAL, "p");
		ChoicesParentStatement pNotEqual = new ChoicesParentStatement(c1, EStatementRelation.NOT, "p");
		
		ChoicesParentStatement p1Equal = new ChoicesParentStatement(c1, EStatementRelation.EQUAL, "p1");
		ChoicesParentStatement p1NotEqual = new ChoicesParentStatement(c1, EStatementRelation.NOT, "p1");
		
		ChoicesParentStatement p11Equal = new ChoicesParentStatement(c1, EStatementRelation.EQUAL, "p11");
		ChoicesParentStatement p11NotEqual = new ChoicesParentStatement(c1, EStatementRelation.NOT, "p11");
		
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
