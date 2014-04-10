package com.testify.ecfeed.model.constraint;

import java.util.List;

import com.testify.ecfeed.model.AbstractCategoryNode;
import com.testify.ecfeed.model.ExpectedCategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class ExpectedValueStatement extends BasicStatement implements IRelationalStatement{

	ExpectedCategoryNode fCategory;
	PartitionNode fCondition;
	
	public ExpectedValueStatement(ExpectedCategoryNode category, PartitionNode condition) {
		fCategory = category;
		fCondition = condition.getCopy();
	}
	
	@Override
	public String getLeftHandName() {
		return fCategory.getName();
	}
	
	public boolean mentions(AbstractCategoryNode category) {
		return category == fCategory;
	}
	
	@Override
	public boolean evaluate(List<PartitionNode> values) {
//		if(fCategory.getMethod() != null){
//			int index = fCategory.getMethod().getCategories().indexOf(fCategory);
//			return values.get(index).getValue().equals(fCondition.getValue());
//		}
//		return false;
		return true;
	}

	@Override
	public boolean adapt(List<PartitionNode> values){
		if(values == null) return true;
		if(fCategory.getMethod() != null){
			int index = fCategory.getMethod().getCategories().indexOf(fCategory);
			values.set(index, fCondition.getCopy());
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
	
	public ExpectedCategoryNode getCategory(){
		return fCategory;
	}
	
	public PartitionNode getCondition(){
		return fCondition;
	}
	
	public String toString(){
		return getCategory().getName() + getRelation().toString() + fCondition.getValueString();
	}
}
