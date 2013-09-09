package com.testify.ecfeed.model.constraint;

import java.util.Vector;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class Statement extends BasicStatement{
	private PartitionNode fCondition = null;
	private Relation fRelation;

	public Statement(PartitionNode condition, Relation relation){
		fCondition = condition;
		fRelation = relation;
	}
	
	public PartitionNode getCondition(){
		return fCondition;
	}
	
	public Relation getRelation(){
		return fRelation;
	}

	@Override
	public boolean evaluate(Vector<PartitionNode> values) {
		CategoryNode parentCategory = (CategoryNode)fCondition.getParent();
		MethodNode methodAncestor = ((MethodNode)parentCategory.getParent());
		int categoryIndex = methodAncestor.getCategories().indexOf(parentCategory);

		if(values.size() < categoryIndex + 1){
			return false;
		}
		
		PartitionNode partition = values.elementAt(categoryIndex);
		Vector<PartitionNode> siblings = parentCategory.getPartitions();
		int conditionIndex = siblings.indexOf(fCondition);
		int partitionIndex = siblings.indexOf(partition);
		
		if(partition.getCategory() != parentCategory){
			return false;
		}
		
		switch (fRelation){
		case EQUAL:
			return partition == fCondition;
		case GREATER:
			return partitionIndex > conditionIndex;
		case GREATER_EQUAL:
			return partitionIndex >= conditionIndex;
		case LESS:
			return partitionIndex < conditionIndex;
		case LESS_EQUAL:
			return partitionIndex <= conditionIndex;
		case NOT:
			return partition != fCondition;
		default:
			return false;
		}
	}
	
	@Override
	public String toString(){
		return fCondition.getParent().getName() + " " + fRelation + " " + fCondition.getName();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		
		if(obj instanceof Statement == false){
			return false;
		}
		Statement statement = (Statement)obj;
		if(fRelation.equals(statement.getRelation()) == false){
			return false;
		}
		if(fCondition.equals(statement.getCondition()) == false){
			return false;
		}
		return super.equals(obj);
	}

	public void setRelation(Relation relation) {
		fRelation = relation;
	}

	public void setCondition(PartitionNode condition) {
		fCondition = condition;
	}
	
	@Override
	public boolean mentions(PartitionNode partition){
		return fCondition == partition;
	}
}