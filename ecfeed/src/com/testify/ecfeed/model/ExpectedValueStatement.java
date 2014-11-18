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

import com.testify.ecfeed.adapter.java.JavaUtils;

public class ExpectedValueStatement extends BasicStatement implements IRelationalStatement{

	ParameterNode fCategory;
	PartitionNode fCondition;

	public ExpectedValueStatement(ParameterNode category, PartitionNode condition) {
		fCategory = category;
		fCondition = condition.getCopy();
	}

	@Override
	public String getLeftOperandName() {
		return fCategory.getName();
	}

	@Override
	public boolean mentions(ParameterNode category) {
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
			values.set(index, fCondition.getCopy());
		}
		return true;
	}

	@Override
	public EStatementRelation[] getAvailableRelations() {
		return new EStatementRelation[]{EStatementRelation.EQUAL};
	}

	@Override
	public EStatementRelation getRelation() {
		return EStatementRelation.EQUAL;
	}

	@Override
	public void setRelation(EStatementRelation relation) {
	}

	public ParameterNode getCategory(){
		return fCategory;
	}

	public PartitionNode getCondition(){
		return fCondition;
	}

	@Override
	public String toString(){
		return getCategory().getName() + getRelation().toString() + fCondition.getValueString();
	}

	@Override
	public ExpectedValueStatement getCopy(){
		return new ExpectedValueStatement(fCategory, fCondition.getCopy());
	}

	@Override
	public boolean updateReferences(MethodNode method){
		ParameterNode category = method.getCategory(fCategory.getName());
		if(category != null && category.isExpected()){
			fCategory = category;
			fCondition.setParent(category);
			String type = category.getType();
			if(JavaUtils.isUserType(type)){
				PartitionNode choice = category.getPartition(fCondition.getQualifiedName());
				if(choice != null){
					fCondition = choice;
				}
				else{
					return false;
				}
			}
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

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}
}
