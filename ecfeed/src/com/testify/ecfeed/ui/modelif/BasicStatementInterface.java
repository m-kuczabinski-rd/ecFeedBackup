package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

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
			return parentIf().removeChild(fTarget, source, updateListener);
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
			return parentIf().addStatement(statement, source, updateListener);
		}
		return false;
	}
	
	public BasicStatementInterface parentIf(){
		BasicStatement parent = fTarget.getParent();
		return new StatementInterfaceFactory(getOperationManager()).getInterface(parent);
	}

	public boolean setRelation(Relation relation, BasicSection source, IModelUpdateListener updateListener) {
		return false;
	}

	public boolean updateCondition(String text, BasicSection source, IModelUpdateListener updateListener) {
		return false;
	}
}
