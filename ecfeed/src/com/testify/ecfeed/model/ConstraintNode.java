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

import java.util.List;

import nu.xom.Element;

import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.parsers.xml.XomConverter;

public class ConstraintNode extends GenericNode{

	private Constraint fConstraint;
	
	@Override
	public String toString(){
		return getName() + ": " + getConstraint().toString();
	}
	
	@Override
	public ConstraintNode getCopy(){
		return new ConstraintNode(getName(), fConstraint.getCopy());	
	}

	public ConstraintNode(String name, Constraint constraint) {
		super(name);
		fConstraint = constraint;
	}
	
	public Constraint getConstraint(){
		return fConstraint;
	}
	
	public MethodNode getMethod() {
		if(getParent() != null && getParent() instanceof MethodNode){
			return (MethodNode)getParent();
		}
		return null;
	}
	
	public void setMethod(MethodNode method){
		setParent(method);
	}

	public boolean evaluate(List<PartitionNode> values) {
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
		return fConstraint.mentions(category);
	}
	
	public boolean updateReferences(MethodNode method){
		if(fConstraint.updateRefrences(method)){
			setParent(method);
			return true;
		}
		return false;
	}
	
	public ConstraintNode getCopy(MethodNode method){
		ConstraintNode copy = getCopy();
		if(copy.updateReferences(method))
			return copy;
		else
			return null;
	}
	
	public boolean compare(IGenericNode node){
		if(node instanceof ConstraintNode == false){
			return false;
		}
		ConstraintNode compared = (ConstraintNode)node;
		if(getConstraint().getPremise().compare(compared.getConstraint().getPremise()) == false){
			return false;
		}
		
		if(getConstraint().getConsequence().compare(compared.getConstraint().getConsequence()) == false){
			return false;
		}
		
		return super.compare(node);
	}

	public Object convert(IConverter converter) {
		return converter.convert(this);
	}
	
}
