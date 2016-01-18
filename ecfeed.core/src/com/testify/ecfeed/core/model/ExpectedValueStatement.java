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

package com.testify.ecfeed.core.model;

import java.util.List;

public class ExpectedValueStatement extends AbstractStatement implements IRelationalStatement{

	MethodParameterNode fParameter;
	ChoiceNode fCondition;
	private IPrimitiveTypePredicate fPredicate;

	public ExpectedValueStatement(MethodParameterNode parameter, ChoiceNode condition, IPrimitiveTypePredicate predicate) {
		fParameter = parameter;
		fCondition = condition.getCopy();
		fPredicate = predicate;
	}

	@Override
	public String getLeftOperandName() {
		return fParameter.getName();
	}

	@Override
	public boolean mentions(MethodParameterNode parameter) {
		return parameter == fParameter;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> values) {
		return true;
	}

	@Override
	public boolean adapt(List<ChoiceNode> values){
		if(values == null) return true;
		if(fParameter.getMethod() != null){
			int index = fParameter.getMethod().getParameters().indexOf(fParameter);
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

	public MethodParameterNode getParameter(){
		return fParameter;
	}

	public ChoiceNode getCondition(){
		return fCondition;
	}

	@Override
	public String toString(){
		return getParameter().getName() + getRelation().toString() + fCondition.getValueString();
	}

	@Override
	public ExpectedValueStatement getCopy(){
		return new ExpectedValueStatement(fParameter, fCondition.getCopy(), fPredicate);
	}

	@Override
	public boolean updateReferences(MethodNode method){
		MethodParameterNode parameter = (MethodParameterNode)method.getParameter(fParameter.getName());
		if(parameter != null && parameter.isExpected()){
			fParameter = parameter;
			fCondition.setParent(parameter);
			String type = parameter.getType();
			//TODO remove reference to JavaUtils
			if(fPredicate.isPrimitive(type) == false){
				ChoiceNode choice = parameter.getChoice(fCondition.getQualifiedName());
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
		if(getParameter().getName().equals(compared.getParameter().getName()) == false){
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

	public boolean isParameterPrimitive(){
		return fPredicate.isPrimitive(fParameter.getType());
	}
}
