package com.testify.ecfeed.ui.modelif;

import java.util.List;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.operations.FactoryShiftOperation;
import com.testify.ecfeed.modeladp.operations.GenericMoveOperation;
import com.testify.ecfeed.modeladp.operations.GenericRemoveNodesOperation;
import com.testify.ecfeed.modeladp.operations.GenericShiftOperation;
import com.testify.ecfeed.ui.common.Messages;

public class SelectionInterface extends OperationExecuter {

	public SelectionInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	private List<? extends GenericNode> fSelected;
	
	public void setTarget(List<GenericNode> target){
		fSelected = target;
	}
	
	public boolean delete(){
		if(fSelected.size() > 0){
			return execute(new GenericRemoveNodesOperation(fSelected, true), Messages.DIALOG_REMOVE_NODES_PROBLEM_TITLE);
		}
		return false;
	}
	
	public boolean deleteEnabled(){
		if(fSelected.size() == 0) return false;
		GenericNode root = fSelected.get(0).getRoot();
		for(GenericNode selected : fSelected){
			if(selected == root) return false;
		}
		return true;
	}
	
	public boolean move(GenericNode newParent){
		return move(newParent, -1);
	}

	public boolean move(GenericNode newParent, int newIndex){
		try{
			IModelOperation operation;
			if(newIndex == -1){
				operation = new GenericMoveOperation(fSelected, newParent);
			}
			else{
				operation = new GenericMoveOperation(fSelected, newParent, newIndex);
			}
			return execute(operation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);
		}catch(ModelOperationException e){
			return false;
		}
	}
	
	public boolean moveUpDown(boolean up) {
		GenericNode parent = fSelected.get(0).getParent();
		for(GenericNode node : fSelected){
			if(node.getParent() != parent){
				return false;
			}
		}
		try{
			IModelOperation operation = (IModelOperation) parent.accept(new FactoryShiftOperation(fSelected, up));
			executeMoveOperation(operation);
		}catch(Exception e){}
		return false;
	}
	
	public boolean moveUpDownEnabed(boolean up) {
		
		GenericNode parent = getCommonParent();
		if(parent != null){
			try {
				GenericShiftOperation operation = (GenericShiftOperation) parent.accept(new FactoryShiftOperation(fSelected, up));
				return operation.getShift() != 0;
			} catch (Exception e) {}
		}
		return false;
	}

	public GenericNode getCommonParent() {
		if(fSelected == null || fSelected.size() == 0) return null;
		GenericNode parent = fSelected.get(0).getParent();
		for(GenericNode node : fSelected){
			if(node.getParent() != parent) return null;
		}
		return parent;
	}

	public boolean isSingleType(){
		if(fSelected == null || fSelected.size() == 0) return false;
		Class<?> type = fSelected.get(0).getClass();
		for(GenericNode node : fSelected){
			if(node.getClass().equals(type) == false) return false;
		}
		return true;
		
	}

	protected boolean executeMoveOperation(IModelOperation moveOperation) {
		return execute(moveOperation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);	
	}
}
