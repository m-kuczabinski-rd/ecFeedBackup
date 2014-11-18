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

public class DecomposedParameterStatement extends BasicStatement implements IRelationalStatement{

	private ParameterNode fParameter;
	private EStatementRelation fRelation;
	private ICondition fCondition;

	public interface ICondition{
		public Object getCondition();
		public boolean evaluate(List<ChoiceNode> values);
		public boolean adapt(List<ChoiceNode> values);
		public ICondition getCopy();
		public boolean updateReferences(ParameterNode parameter);
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
			return fLabel + (fParameter.getAllPartitionNames().contains(fLabel)?"[label]":"");
		}

		@Override
		public boolean adapt(List<ChoiceNode> values) {
			return false;
		}

		@Override
		public LabelCondition getCopy(){
			return new LabelCondition(fLabel);
		}

		@Override
		public boolean updateReferences(ParameterNode parameter){
			return true;
		}

		@Override
		public Object getCondition(){
			return fLabel;
		}

		@Override
		public boolean evaluate(List<ChoiceNode> values){
			if(getParameter().getMethod() == null){
				return false;
			}
			int index = getParameter().getMethod().getParameters().indexOf(getParameter());
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
		private ChoiceNode fPartition;

		public PartitionCondition(ChoiceNode partition){
			fPartition = partition;
		}

		public ChoiceNode getPartition() {
			return fPartition;
		}

		@Override
		public String toString(){
			return fPartition.getQualifiedName();
		}

		@Override
		public boolean adapt(List<ChoiceNode> values) {
			return false;
		}

		@Override
		public PartitionCondition getCopy(){
			return new PartitionCondition(fPartition.getCopy());
		}

		@Override
		public boolean updateReferences(ParameterNode parameter){
			ChoiceNode condition = parameter.getPartition(fPartition.getQualifiedName());
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
		public boolean evaluate(List<ChoiceNode> values){
			if(getParameter().getMethod() == null){
				return false;
			}

			if(values == null){
				return true;
			}

			int index = getParameter().getMethod().getParameters().indexOf(getParameter());

			if(values.size() < index + 1){
				return false;
			}

			ChoiceNode partition = values.get(index);

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

			return (fPartition.compare((ChoiceNode)compared.getCondition()));
		}

		@Override
		public Object accept(IStatementVisitor visitor) throws Exception {
			return visitor.visit(this);
		}

	}

	public DecomposedParameterStatement(ParameterNode parameter, EStatementRelation relation, String labelCondition){
		fParameter = parameter;
		fRelation = relation;
		fCondition = new LabelCondition(labelCondition);
	}

	public DecomposedParameterStatement(ParameterNode parameter, EStatementRelation relation, ChoiceNode partitionCondition){
		fParameter = parameter;
		fRelation = relation;
		fCondition = new PartitionCondition(partitionCondition);
	}

	private DecomposedParameterStatement(ParameterNode parameter, EStatementRelation relation, ICondition condition){
		fParameter = parameter;
		fRelation = relation;
		fCondition = condition;
	}

	@Override
	public boolean mentions(ParameterNode parameter){
		return getParameter() == parameter;
	}

	@Override
	public boolean mentions(ParameterNode parameter, String label) {
		return getParameter() == parameter && getConditionValue().equals(label);
	}

	@Override
	public boolean mentions(ChoiceNode partition){
		return getConditionValue() == partition;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> values){
		return fCondition.evaluate(values);
	}

	@Override
	public String getLeftOperandName() {
		return getParameter().getName();
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
	public DecomposedParameterStatement getCopy(){
		return new DecomposedParameterStatement(fParameter, fRelation, fCondition.getCopy());
	}

	@Override
	public boolean updateReferences(MethodNode method){
		ParameterNode parameter = method.getParameter(fParameter.getName());
		if(parameter != null && !parameter.isExpected()){
			if(fCondition.updateReferences(parameter)){
				fParameter = parameter;
				return true;
			}
		}
		return false;
	}

	public ParameterNode getParameter(){
		return fParameter;
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

	public void setCondition(ChoiceNode partition){
		fCondition = new PartitionCondition(partition);
	}

	public void setCondition(ParameterNode parameter, ChoiceNode partition){
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
		if(statement instanceof DecomposedParameterStatement == false){
			return false;
		}

		DecomposedParameterStatement compared = (DecomposedParameterStatement)statement;

		if(getParameter().getName().equals(compared.getParameter().getName()) == false){
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

