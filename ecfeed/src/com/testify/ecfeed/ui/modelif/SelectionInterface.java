package com.testify.ecfeed.ui.modelif;

import java.util.List;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.operations.FactoryShiftOperation;
import com.testify.ecfeed.abstraction.operations.GenericMoveOperation;
import com.testify.ecfeed.abstraction.operations.GenericRemoveNodesOperation;
import com.testify.ecfeed.abstraction.operations.GenericShiftOperation;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.common.Messages;

public class SelectionInterface extends OperationExecuter {

	private List<? extends GenericNode> fSelected;
	
	public void setTarget(List<GenericNode> target){
		fSelected = target;
	}
	
	public boolean delete(IModelUpdateContext context){
		return execute(new GenericRemoveNodesOperation(fSelected, true), context, Messages.DIALOG_REMOVE_NODES_PROBLEM_TITLE);
	}
	
	public boolean deleteEnabled(){
		if(fSelected.size() == 0) return false;
		GenericNode root = fSelected.get(0).getRoot();
		for(GenericNode selected : fSelected){
			if(selected == root) return false;
		}
		return true;
	}
	
	public boolean move(GenericNode newParent, IModelUpdateContext context){
		return move(newParent, -1, context);
	}

	public boolean move(GenericNode newParent, int newIndex, IModelUpdateContext context){
		try{
			IModelOperation operation;
			if(newIndex == -1){
				operation = new GenericMoveOperation(fSelected, newParent);
			}
			else{
				operation = new GenericMoveOperation(fSelected, newParent, newIndex);
			}
			return execute(operation, context, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);
		}catch(ModelIfException e){
			return false;
		}
	}
	
	public boolean moveUpDown(boolean up, IModelUpdateContext context) {
		GenericNode parent = fSelected.get(0).getParent();
		for(GenericNode node : fSelected){
			if(node.getParent() != parent){
				return false;
			}
		}
		try{
			IModelOperation operation = (IModelOperation) parent.accept(new FactoryShiftOperation(fSelected, up));
			executeMoveOperation(operation, context);
		}catch(Exception e){}
		return false;
	}
	
	protected boolean executeMoveOperation(IModelOperation moveOperation,
			IModelUpdateContext context) {
		return execute(moveOperation, context, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);	
	}

	public boolean moveUpDownEnabed(boolean up) {
		
		GenericNode parent = getCommonParent(fSelected);
		if(parent != null){
			try {
				GenericShiftOperation operation = (GenericShiftOperation) parent.accept(new FactoryShiftOperation(fSelected, up));
				return operation.getShift() != 0;
			} catch (Exception e) {}
		}
		return false;
	}

	private GenericNode getCommonParent(List<? extends GenericNode> list) {
		if(list == null || list.size() == 0) return null;
		GenericNode parent = list.get(0).getParent();
		for(GenericNode node : list){
			if(node.getParent() != parent) return null;
		}
		return parent;
	}

}
