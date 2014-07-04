 package com.testify.ecfeed.model.constraint;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class ExpectedValueStatement extends BasicStatement implements IRelationalStatement{

	CategoryNode fCategory;
	PartitionNode fCondition;
	
	public ExpectedValueStatement(CategoryNode category, PartitionNode condition) {
		fCategory = category;
		fCondition = condition.getLeaflessCopy();
	}
	
	@Override
	public String getLeftHandName() {
		return fCategory.getName();
	}
	
	public boolean mentions(CategoryNode category) {
		return category == fCategory;
	}
	
	@Override
	public boolean evaluate(List<PartitionNode> values) {
		return true;
	}

	@Override
	public boolean adapt(List<PartitionNode> values){
		if(values == null) return true;
		if(fCategory.getMethod() != null){
			int index = fCategory.getMethod().getCategories().indexOf(fCategory);
			values.set(index, fCondition.getLeaflessCopy());
		}
		return true;
	}

	@Override
	public Relation[] getAvailableRelations() {
		return new Relation[]{Relation.EQUAL};
	}

	@Override
	public Relation getRelation() {
		return Relation.EQUAL;
	}

	@Override
	public void setRelation(Relation relation) {
	}
	
	public CategoryNode getCategory(){
		return fCategory;
	}
	
	public PartitionNode getCondition(){
		return fCondition;
	}
	
	public String toString(){
		return getCategory().getName() + getRelation().toString() + fCondition.getValueString();
	}
	
	@Override
	public ExpectedValueStatement getCopy(){
		return new ExpectedValueStatement(fCategory, fCondition.getCopy());
	}
	
	@Override
	public boolean updateReferences(MethodNode method){
		CategoryNode category = method.getCategory(fCategory.getName());
		if(category != null && category.isExpected()){
			fCategory = category;
			fCondition.setParent(category);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean compare(IStatement statement){
		if(statement instanceof ExpectedValueStatement == false){
			return false;
		}
		
		ExpectedValueStatement compared = (ExpectedValueStatement)statement;
		if(getCategory().getName().equals(compared.getCategory().getName()) == false){
			return false;
		}
		
		if(getCondition().getValueString().equals(compared.getCondition().getValueString()) == false){
			return false;
		}
		
		return true;
	}
}
