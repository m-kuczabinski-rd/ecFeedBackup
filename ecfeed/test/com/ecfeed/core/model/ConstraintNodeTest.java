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

import org.junit.Test;

import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.StaticStatement;

public class ConstraintNodeTest {

	@Test
	public void compare(){
		ConstraintNode c1 = new ConstraintNode("c", new Constraint(new StaticStatement(true), new StaticStatement(true)));
		ConstraintNode c2 = new ConstraintNode("c", new Constraint(new StaticStatement(true), new StaticStatement(true)));
		
		assertTrue(c1.compare(c2));
		
		c1.setName("c1");
		assertFalse(c1.compare(c2));
		c2.setName("c1");
		assertTrue(c1.compare(c2));
		
		c1.getConstraint().setPremise(new StaticStatement(false));
		assertFalse(c1.compare(c2));
		c2.getConstraint().setPremise(new StaticStatement(false));
		assertTrue(c1.compare(c2));

		c1.getConstraint().setConsequence(new StaticStatement(false));
		assertFalse(c1.compare(c2));
		c2.getConstraint().setConsequence(new StaticStatement(false));
		assertTrue(c1.compare(c2));
}
}
