package com.testify.ecfeed.ui.modelif;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.modelif.ModelOperationManager;

public class BasicStatementInterface extends OperationExecuter {
	
	BasicStatement fTarget;
	
	public BasicStatementInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	
	public void setTarget(BasicStatement target){
		fTarget = target;
	}
	
	public boolean remove(AbstractFormPart source, IModelUpdateListener updateListener){
		if(fTarget.getParent() != null){
			return getParentInterface().removeChild(fTarget, source, updateListener);
		}
		return false;
	}
	
	public boolean removeChild(BasicStatement child, AbstractFormPart source, IModelUpdateListener updateListener){
		return false;
	}
	
	
	public BasicStatement addNewStatement(AbstractFormPart source, IModelUpdateListener updateListener){
		BasicStatement statement = new StaticStatement(true);
		if(addStatement(statement, source, updateListener)){
			return statement;
		}
		return null;
	}
	
	public boolean addStatement(BasicStatement statement, AbstractFormPart source, IModelUpdateListener updateListener){
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

	public boolean setRelation(Relation relation, AbstractFormPart source, IModelUpdateListener updateListener) {
		return false;
	}

	public boolean setConditionValue(String text, AbstractFormPart source, IModelUpdateListener updateListener) {
		return false;
	}

	public String getConditionValue() {
		return null;
	}

	public boolean setOperator(Operator operator, AbstractFormPart source, IModelUpdateListener updateListener) {
		return false;
	}

	public Operator getOperator() {
		return null;
	}

	public boolean replaceChild(BasicStatement child, BasicStatement newStatement, AbstractFormPart source, IModelUpdateListener updateListener) {
		return false;
	}
}
