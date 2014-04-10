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

import com.testify.ecfeed.model.AbstractCategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedCategoryNode;

public class PartitionedCategoryStatement extends BasicStatement implements IRelationalStatement{

	private PartitionedCategoryNode fCategory;
	private Relation fRelation;
	private ICondition fCondition;
	
	private interface ICondition{
		public Object getCondition();
		public boolean evaluate(List<PartitionNode> values);
		public boolean adapt(List<PartitionNode> values);
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

		@Override
		public boolean adapt(List<PartitionNode> values) {
			return false;
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
			if(getCategory().getMethod() == null){
				return false;
			}
			
			if(values == null){
				return true;
			}
			
			int index = getCategory().getMethod().getCategories().indexOf(getCategory());

			if(values.size() < index + 1){
				return false;
			}
			
			PartitionNode partition = values.get(index);

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

		@Override
		public boolean adapt(List<PartitionNode> values) {
			return false;
		}
	}
	
	public PartitionedCategoryStatement(PartitionedCategoryNode category, Relation relation, String labelCondition){
		fCategory = category;
		fRelation = relation;
		fCondition = new LabelCondition(labelCondition);
	}
	
	public PartitionedCategoryStatement(PartitionedCategoryNode category, Relation relation, PartitionNode partitionCondition){
		fCategory = category;
		fRelation = relation;
		fCondition = new PartitionCondition(partitionCondition);
	}
	
	public PartitionedCategoryNode getCategory(){
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
	
	public void setCondition(PartitionedCategoryNode category, PartitionNode partition){
		fCondition = new PartitionCondition(partition);
	}
	
	public Object getConditionValue(){
		return fCondition.getCondition();
	}

	public String getConditionName(){
		return fCondition.toString();
	}

	@Override
	public boolean mentions(AbstractCategoryNode category){
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
	
	@Override
	public Relation[] getAvailableRelations() {
		return new Relation[]{Relation.EQUAL, Relation.NOT};
	}

}

