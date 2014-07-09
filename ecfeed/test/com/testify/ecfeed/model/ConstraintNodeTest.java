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
