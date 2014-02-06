package com.testify.ecfeed.model.constraint;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class LabelStatement extends BasicStatement {
	private Relation fRelation;
	private String fConditionLabel;
	private CategoryNode fCategory;

	public LabelStatement(CategoryNode category, String conditionLabel, Relation relation){
		fCategory = category;
		fConditionLabel = conditionLabel;
		fRelation = relation;
	}
	
	public String getCondition(){
		return fConditionLabel;
	}
	
	public void setCondition(String condition) {
		fConditionLabel = condition;
	}

	public Relation getRelation(){
		return fRelation;
	}

	public void setRelation(Relation relation) {
		fRelation = relation;
	}
	
	public void setCategory(CategoryNode category){
		fCategory = category;
	}

	public CategoryNode getCategory(){
		return fCategory;
	}
	
	@Override
	public boolean mentions(CategoryNode category){
		return category == fCategory;
	}

	@Override
	public boolean evaluate(List<PartitionNode> values) {
		if(fCategory.getMethod() == null){
			return false;
		}
		int index = fCategory.getMethod().getCategories().indexOf(fCategory);
		boolean containsLabel = values.get(index).getAllLabels().contains(fConditionLabel); 
		
		switch (fRelation){
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
		return fCategory.getName() + fRelation + fConditionLabel;
	}
}
