package com.testify.ecfeed.model.constraint;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class LabelStatement extends CategoryConditionStatement {
	public LabelStatement(CategoryNode category, Relation relation, String conditionLabel){
		super(category, relation, conditionLabel);
	}
	
	public String getStringCondition(){
		return (String)getCondition();
	}
	
	public void setCondition(String condition) {
		super.setCondition(condition);
	}
	
	public String getLabelCondition(){
		return (String)getCondition();
	}
	
	@Override
	public String getConditionName(){
		return getLabelCondition();
	}
	
	@Override
	public boolean evaluate(List<PartitionNode> values) {
		if(getCategory().getMethod() == null){
			return false;
		}
		int index = getCategory().getMethod().getCategories().indexOf(getCategory());
		boolean containsLabel = values.get(index).getAllLabels().contains(getCondition()); 
		
		switch (getRelation()){
		case EQUAL:
			return containsLabel;
		case NOT:
			return !containsLabel;
		default:
			return false;
		}
	}
	
	@Override
	public String toString(){
		return getCategory().getName() + getRelation() + getCondition();
	}
}
