package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.BasicStatement;
import com.testify.ecfeed.model.EStatementOperator;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.StaticStatement;

public class BasicStatementInterface extends OperationExecuter {
	
	BasicStatement fTarget;
	
	public BasicStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}
	
	public void setTarget(BasicStatement target){
		fTarget = target;
	}
	
	public boolean remove(){
		if(fTarget.getParent() != null){
			return getParentInterface().removeChild(fTarget);
		}
		return false;
	}
	
	public boolean removeChild(BasicStatement child){
		return false;
	}
	
	
	public BasicStatement addNewStatement(){
		BasicStatement statement = new StaticStatement(true);
		if(addStatement(statement)){
			return statement;
		}
		return null;
	}
	
	public boolean addStatement(BasicStatement statement){
		if(fTarget.getParent() != null){
			return getParentInterface().addStatement(statement);
		}
		return false;
	}
	
	public BasicStatementInterface getParentInterface(){
		BasicStatement parent = fTarget.getParent();
		if(parent != null){
			return StatementInterfaceFactory.getInterface(parent, getUpdateContext());
		}
		return null;
	}

	public boolean setRelation(EStatementRelation relation) {
		return false;
	}

	public boolean setConditionValue(String text) {
		return false;
	}

	public String getConditionValue() {
		return null;
	}

	public boolean setOperator(EStatementOperator operator) {
		return false;
	}

	public EStatementOperator getOperator() {
		return null;
	}

	public boolean replaceChild(BasicStatement child, BasicStatement newStatement) {
		return false;
	}
}
