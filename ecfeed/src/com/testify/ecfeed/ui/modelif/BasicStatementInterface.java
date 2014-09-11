package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StaticStatement;

public class BasicStatementInterface extends OperationExecuter {
	
	BasicStatement fTarget;
	
	public BasicStatementInterface() {
	}

	
	public void setTarget(BasicStatement target){
		fTarget = target;
	}
	
	public boolean remove(IModelUpdateContext context){
		if(fTarget.getParent() != null){
			return getParentInterface().removeChild(fTarget, context);
		}
		return false;
	}
	
	public boolean removeChild(BasicStatement child, IModelUpdateContext context){
		return false;
	}
	
	
	public BasicStatement addNewStatement(IModelUpdateContext context){
		BasicStatement statement = new StaticStatement(true);
		if(addStatement(statement, context)){
			return statement;
		}
		return null;
	}
	
	public boolean addStatement(BasicStatement statement, IModelUpdateContext context){
		if(fTarget.getParent() != null){
			return getParentInterface().addStatement(statement, context);
		}
		return false;
	}
	
	public BasicStatementInterface getParentInterface(){
		BasicStatement parent = fTarget.getParent();
		if(parent != null){
			return StatementInterfaceFactory.getInterface(parent);
		}
		return null;
	}

	public boolean setRelation(Relation relation, IModelUpdateContext context) {
		return false;
	}

	public boolean setConditionValue(String text, IModelUpdateContext context) {
		return false;
	}

	public String getConditionValue() {
		return null;
	}

	public boolean setOperator(Operator operator, IModelUpdateContext context) {
		return false;
	}

	public Operator getOperator() {
		return null;
	}

	public boolean replaceChild(BasicStatement child, BasicStatement newStatement, IModelUpdateContext context) {
		return false;
	}
}
