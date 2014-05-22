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

import java.util.List;

import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class Constraint implements IConstraint<PartitionNode> {
	
	private final int ID;
	private static int fLastId = 0;

	private BasicStatement fPremise;
	private BasicStatement fConsequence; 

	public Constraint(BasicStatement premise, BasicStatement consequence){
		ID = fLastId++;
		fPremise = premise;
		fConsequence = consequence;
	}
	
	@Override
	public boolean evaluate(List<PartitionNode> values) {
		if(fPremise == null) return true;
		if(fPremise.evaluate(values) == true){
			if(fConsequence == null) return false;
			return fConsequence.evaluate(values);
		}
		return true;
	}

	@Override
	public boolean adapt(List<PartitionNode> values){
		if(fPremise == null) return true;
		if(fPremise.evaluate(values) == true){
			return fConsequence.adapt(values);
		}		
		return true;
	}

	@Override
	public String toString(){
		String premiseString = (fPremise != null)?fPremise.toString():"EMPTY";
		String consequenceString = (fConsequence != null)?fConsequence.toString():"EMPTY";
		return premiseString + " \u21d2 " + consequenceString;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof Constraint == false){
			return false;
		}
		return(ID == ((Constraint)obj).getId());
	}

	public int getId(){
		return ID;
	}
	
	public BasicStatement getPremise(){
		return fPremise;
	}
	
	public BasicStatement getConsequence(){
		return fConsequence;
	}

	public void setPremise(BasicStatement statement){
		fPremise = statement;
	}
	
	public void setConsequence(BasicStatement consequence){
		fConsequence = consequence;
	}
	
	public boolean mentions(CategoryNode category) {
		return fPremise.mentions(category) || fConsequence.mentions(category);
	}

	public boolean mentions(PartitionNode partition) {
		return fPremise.mentions(partition) || fConsequence.mentions(partition);
	}
}
