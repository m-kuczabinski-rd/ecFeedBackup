/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.StaticStatement;

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
