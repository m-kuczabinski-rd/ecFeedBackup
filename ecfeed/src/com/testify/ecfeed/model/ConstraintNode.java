package com.testify.ecfeed.model;

import java.util.ArrayList;

import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.model.constraint.Constraint;

public class ConstraintNode extends GenericNode implements IConstraint{

	private Constraint fConstraint;

	public ConstraintNode(String name, Constraint constraint) {
		super(name);
		fConstraint = constraint;
	}
	
	public Constraint getConstraint(){
		return fConstraint;
	}
	
	@Override
	public String toString(){
		return getName() + ": " + getConstraint().toString();
	}
	
	public MethodNode getMethod() {
		if(getParent() != null && getParent() instanceof MethodNode){
			return (MethodNode)getParent();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean evaluate(ArrayList values) {
		if(fConstraint != null){
			return fConstraint.evaluate(values);
		}
		return false;
	}

	public boolean mentions(PartitionNode partition) {
		if(fConstraint.mentions(partition)){
			return true;
		}
		return false;
	}

	public boolean mentions(CategoryNode category) {
		if(fConstraint.mentions(category)){
			return true;
		}
		return false;
	}
}
