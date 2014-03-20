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

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class ConditionStatement extends BasicStatement {

	private CategoryNode fCategory;
	private Relation fRelation;
	private ICondition fCondition;
	
	private interface ICondition{
		public Object getCondition();
		public boolean evaluate(List<PartitionNode> values);
	}
	
	private class LabelCondition implements ICondition{
		private String fLabel;
		
		public LabelCondition(String label){
			fLabel = label;
		}
		
		public Object getCondition(){
			return fLabel;
		}
		
		public boolean evaluate(List<PartitionNode> values){
			if(getCategory().getMethod() == null){
				return false;
			}
			int index = getCategory().getMethod().getCategories().indexOf(getCategory());
			boolean containsLabel = values.get(index).getAllLabels().contains(fLabel); 
			
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
			return fLabel;
		}
	}
	
	private class PartitionCondition implements ICondition{
		private PartitionNode fPartition;
		
		public PartitionCondition(PartitionNode partition){
			fPartition = partition;
		}

		public Object getCondition(){
			return fPartition;
		}
		
		public boolean evaluate(List<PartitionNode> values){
			CategoryNode parentCategory = fPartition.getCategory();
			MethodNode methodAncestor = parentCategory.getMethod();
			int categoryIndex = methodAncestor.getCategories().indexOf(parentCategory);

			if(values.size() < categoryIndex + 1){
				return false;
			}
			
			PartitionNode partition = values.get(categoryIndex);

			boolean isCondition = partition.is(fPartition);
			
			switch (getRelation()){
			case EQUAL:
				return isCondition;
			case NOT:
				return !isCondition;
			default:
				return false;
			}
		}
		
		@Override
		public String toString(){
			return fPartition.getQualifiedName();
		}
	}
	
	private class ExpectedValueCondition implements ICondition{
		Object fExpectedValue;

		public ExpectedValueCondition(Object condition){
			fExpectedValue = condition;
		}

		@Override
		public Object getCondition() {
			return fExpectedValue;
		}

		@Override
		public boolean evaluate(List<PartitionNode> values) {
			MethodNode methodAncestor = fCategory.getMethod();
			int categoryIndex = methodAncestor.getCategories().indexOf(fCategory);

			if(values.size() < categoryIndex + 1){
				return false;
			}
			PartitionNode partition = values.get(categoryIndex);
			return fExpectedValue.equals(partition.getValue());
		}
		
		@Override
		public String toString(){
			return fExpectedValue.toString();
		}
	}

	
	public ConditionStatement(CategoryNode category, Relation relation, String labelCondition){
		fCategory = category;
		fRelation = relation;
		fCondition = new LabelCondition(labelCondition);
	}
	
	public ConditionStatement(CategoryNode category, Relation relation, PartitionNode partitionCondition){
		fCategory = category;
		fRelation = relation;
		fCondition = new PartitionCondition(partitionCondition);
	}
	
	public ConditionStatement(ExpectedValueCategoryNode category, Relation relation, Object condition){
		fCategory = category;
		fRelation = relation;
		fCondition = new ExpectedValueCondition(condition); 
	}
	
	public void setCategory(CategoryNode category){
		fCategory = category;
	}
	
	public CategoryNode getCategory(){
		return fCategory;
	}
	
	public void setRelation(Relation relation){
		fRelation = relation;
	}
	
	public Relation getRelation(){
		return fRelation;
	}
	
	public void setCondition(String label){
		fCondition = new LabelCondition(label);
	}
	
	public void setCondition(PartitionNode partition){
		fCondition = new PartitionCondition(partition);
	}
	
	public void setCondition(Object condition){
		fCondition = new ExpectedValueCondition(condition);
	}
	
	public Object getConditionValue(){
		return fCondition.getCondition();
	}

	public String getConditionName(){
		return fCondition.toString();
	}

	@Override
	public boolean mentions(CategoryNode category){
		return getCategory() == category;
	}

	@Override
	public boolean mentions(PartitionNode partition){
		return getConditionValue() == partition;
	}

	public boolean evaluate(List<PartitionNode> values){
		return fCondition.evaluate(values);
	}
	
	@Override
	public String getLeftHandName() {
		return getCategory().getName();
	}
	
	@Override
	public String toString(){
		return getLeftHandName() + getRelation() + fCondition.toString();
	}
}

