package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.editor.BasicSection;

public class BasicStatementInterface extends OperationExecuter {
	
	BasicStatement fTarget;
	
	public BasicStatementInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	
	public void setTarget(BasicStatement target){
		fTarget = target;
	}
	
	public boolean remove(BasicSection source, IModelUpdateListener updateListener){
		if(fTarget.getParent() != null){
			return getParentInterface().removeChild(fTarget, source, updateListener);
		}
		return false;
	}
	
	public boolean removeChild(BasicStatement child, BasicSection source, IModelUpdateListener updateListener){
		return false;
	}
	
	
	public BasicStatement addNewStatement(BasicSection source, IModelUpdateListener updateListener){
		BasicStatement statement = new StaticStatement(true);
		if(addStatement(statement, source, updateListener)){
			return statement;
		}
		return null;
	}
	
	public boolean addStatement(BasicStatement statement, BasicSection source, IModelUpdateListener updateListener){
		if(fTarget.getParent() != null){
			return getParentInterface().addStatement(statement, source, updateListener);
		}
		return false;
	}
	
	public BasicStatementInterface getParentInterface(){
		BasicStatement parent = fTarget.getParent();
		if(parent != null){
			return new StatementInterfaceFactory(getOperationManager()).getInterface(parent);
		}
		return null;
	}

	public boolean setRelation(Relation relation, BasicSection source, IModelUpdateListener updateListener) {
		return false;
	}

	public boolean setConditionValue(String text, BasicSection source, IModelUpdateListener updateListener) {
		return false;
	}

	public String getConditionValue() {
		return null;
	}

	public boolean setOperator(Operator operator, BasicSection source, IModelUpdateListener updateListener) {
		return false;
	}

	public Operator getOperator() {
		return null;
	}

	public boolean replaceChild(BasicStatement child, BasicStatement newStatement, BasicSection source, IModelUpdateListener updateListener) {
		return false;
	}
}
