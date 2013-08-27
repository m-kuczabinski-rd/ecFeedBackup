package com.testify.ecfeed.model;

import com.testify.ecfeed.api.IConstraint;

public class ConstraintNode extends GenericNode{

	private Constraint fConstraint;

	public ConstraintNode(String name, Constraint constraint) {
		super(name);
		fConstraint = constraint;
	}
	
	public IConstraint getConstraint(){
		return fConstraint;
	}
}
