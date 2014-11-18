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

public class PartitionedCategoryStatement extends BasicStatement implements IRelationalStatement{

	private ParameterNode fCategory;
	private EStatementRelation fRelation;
	private ICondition fCondition;

	public interface ICondition{
		public Object getCondition();
		public boolean evaluate(List<PartitionNode> values);
		public boolean adapt(List<PartitionNode> values);
		public ICondition getCopy();
		public boolean updateReferences(ParameterNode category);
		public boolean compare(ICondition condition);
		public Object accept(IStatementVisitor visitor) throws Exception;
	}

	public class LabelCondition implements ICondition{
		private String fLabel;

		public LabelCondition(String label){
			fLabel = label;
		}

		public String getLabel(){
			return fLabel;
		}

		@Override
		public String toString(){
			return fLabel + (fCategory.getAllPartitionNames().contains(fLabel)?"[label]":"");
		}

		@Override
		public boolean adapt(List<PartitionNode> values) {
			return false;
		}

		@Override
		public LabelCondition getCopy(){
			return new LabelCondition(fLabel);
		}

		@Override
		public boolean updateReferences(ParameterNode category){
			return true;
		}

		@Override
		public Object getCondition(){
			return fLabel;
		}

		@Override
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
		public boolean compare(ICondition condition){
			if(condition instanceof LabelCondition == false){
				return false;
			}
			LabelCondition compared = (LabelCondition)condition;

			return (getCondition().equals(compared.getCondition()));
		}

		@Override
		public Object accept(IStatementVisitor visitor) throws Exception {
			return visitor.visit(this);
		}
	}

	public class PartitionCondition implements ICondition{
		private PartitionNode fPartition;

		public PartitionCondition(PartitionNode partition){
			fPartition = partition;
		}

		public PartitionNode getPartition() {
			return fPartition;
		}

		@Override
		public String toString(){
			return fPartition.getQualifiedName();
		}

		@Override
		public boolean adapt(List<PartitionNode> values) {
			return false;
		}

		@Override
		public PartitionCondition getCopy(){
			return new PartitionCondition(fPartition.getCopy());
		}

		@Override
		public boolean updateReferences(ParameterNode category){
			PartitionNode condition = category.getPartition(fPartition.getQualifiedName());
			if(condition != null){
				fPartition = condition;
			}
			else{
				return false;
			}
			return true;
		}

		@Override
		public Object getCondition(){
			return fPartition;
		}

		@Override
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
		public boolean compare(ICondition condition){
			if(condition instanceof PartitionCondition == false){
				return false;
			}
			PartitionCondition compared = (PartitionCondition)condition;

			return (fPartition.compare((PartitionNode)compared.getCondition()));
		}

		@Override
		public Object accept(IStatementVisitor visitor) throws Exception {
			return visitor.visit(this);
		}

	}

	public PartitionedCategoryStatement(ParameterNode category, EStatementRelation relation, String labelCondition){
		fCategory = category;
		fRelation = relation;
		fCondition = new LabelCondition(labelCondition);
	}

	public PartitionedCategoryStatement(ParameterNode category, EStatementRelation relation, PartitionNode partitionCondition){
		fCategory = category;
		fRelation = relation;
		fCondition = new PartitionCondition(partitionCondition);
	}

	private PartitionedCategoryStatement(ParameterNode category, EStatementRelation relation, ICondition condition){
		fCategory = category;
		fRelation = relation;
		fCondition = condition;
	}

	@Override
	public boolean mentions(ParameterNode category){
		return getCategory() == category;
	}

	@Override
	public boolean mentions(ParameterNode category, String label) {
		return getCategory() == category && getConditionValue().equals(label);
	}

	@Override
	public boolean mentions(PartitionNode partition){
		return getConditionValue() == partition;
	}

	@Override
	public boolean evaluate(List<PartitionNode> values){
		return fCondition.evaluate(values);
	}

	@Override
	public String getLeftOperandName() {
		return getCategory().getName();
	}

	@Override
	public String toString(){
		return getLeftOperandName() + getRelation() + fCondition.toString();
	}

	@Override
	public EStatementRelation[] getAvailableRelations() {
		return new EStatementRelation[]{EStatementRelation.EQUAL, EStatementRelation.NOT};
	}

	@Override
	public PartitionedCategoryStatement getCopy(){
		return new PartitionedCategoryStatement(fCategory, fRelation, fCondition.getCopy());
	}

	@Override
	public boolean updateReferences(MethodNode method){
		ParameterNode category = method.getCategory(fCategory.getName());
		if(category != null && !category.isExpected()){
			if(fCondition.updateReferences(category)){
				fCategory = category;
				return true;
			}
		}
		return false;
	}

	public ParameterNode getCategory(){
		return fCategory;
	}

	@Override
	public void setRelation(EStatementRelation relation){
		fRelation = relation;
	}

	@Override
	public EStatementRelation getRelation(){
		return fRelation;
	}

	public void setCondition(ICondition condition){
		fCondition = condition;
	}

	public void setCondition(String label){
		fCondition = new LabelCondition(label);
	}

	public void setCondition(PartitionNode partition){
		fCondition = new PartitionCondition(partition);
	}

	public void setCondition(ParameterNode category, PartitionNode partition){
		fCondition = new PartitionCondition(partition);
	}

	public ICondition getCondition(){
		return fCondition;
	}

	public Object getConditionValue(){
		return fCondition.getCondition();
	}

	public String getConditionName(){
		return fCondition.toString();
	}

	@Override
	public boolean compare(IStatement statement){
		if(statement instanceof PartitionedCategoryStatement == false){
			return false;
		}

		PartitionedCategoryStatement compared = (PartitionedCategoryStatement)statement;

		if(getCategory().getName().equals(compared.getCategory().getName()) == false){
			return false;
		}

		if(getRelation() != compared.getRelation()){
			return false;
		}

		return getCondition().compare(compared.getCondition());
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}
}

