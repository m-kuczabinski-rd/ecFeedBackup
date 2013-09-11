package com.testify.ecfeed.model.constraint;

import java.util.ArrayList;

import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.model.PartitionNode;

public class Constraint implements IConstraint {

	private BasicStatement fPremise;
	private BasicStatement fConsequence; 

	public Constraint(BasicStatement premise, BasicStatement consequence){
		fPremise = premise;
		fConsequence = consequence;
	}
	
	public BasicStatement getPremise(){
		return fPremise;
	}
	
	public void setPremise(BasicStatement statement){
		fPremise = statement;
	}
	
	public BasicStatement getConsequence(){
		return fConsequence;
	}
	
	public void setConsequence(BasicStatement consequence){
		fConsequence = consequence;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean evaluate(ArrayList values) {
		for(Object value : values){
			if(value instanceof PartitionNode == false){
				return false;
			}
		}
		if(fPremise == null) return true;
		if(fPremise.evaluate(values) == true){
			if(fConsequence == null) return false;
			return fConsequence.evaluate(values);
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

		Constraint constraint = (Constraint)obj;
		boolean result = true;
		if(fPremise == null){
			result &= (constraint.getPremise() == null);
		}
		else{
			result &= fPremise.equals(constraint.getPremise());
		}

		if(fConsequence == null){
			result &= (constraint.getConsequence() == null);
		}
		else{
			fConsequence.equals(constraint.getConsequence());
		}
		
		return result;
	}
}
