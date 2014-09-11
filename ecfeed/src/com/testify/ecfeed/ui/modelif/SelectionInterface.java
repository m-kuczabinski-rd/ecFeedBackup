package com.testify.ecfeed.ui.modelif;

import java.util.List;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.operations.FactoryShiftOperation;
import com.testify.ecfeed.modelif.operations.GenericRemoveNodesOperation;
import com.testify.ecfeed.modelif.operations.GenericShiftOperation;

public class SelectionInterface extends OperationExecuter {

	private List<? extends GenericNode> fSelected;
	
	public void setTarget(List<? extends GenericNode> target){
		fSelected = target;
	}
	
	public boolean delete(IModelUpdateContext context){
		return execute(new GenericRemoveNodesOperation(fSelected), context, Messages.DIALOG_REMOVE_NODES_PROBLEM_TITLE);
	}
	
	public boolean deleteEnabled(){
		if(fSelected.size() == 0) return false;
		GenericNode root = fSelected.get(0).getRoot();
		for(GenericNode selected : fSelected){
			if(selected.getRoot() == root) return false;
		}
		return true;
	}
	
	public boolean move(GenericNode newParent, IModelUpdateContext context){
		return move(newParent, 0, context);
	}

	public boolean move(GenericNode newParent, int newIndex, IModelUpdateContext context){
		if(fSelected.size() != 1){
			return false;
		}
		GenericNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(fSelected.get(0));
		return nodeIf.move(newParent, newIndex, context);
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
		GenericNode parent = list.get(0).getParent();
		for(GenericNode node : list){
			if(node.getParent() != parent) return null;
		}
		return parent;
	}

}
