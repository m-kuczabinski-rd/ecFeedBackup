package com.testify.ecfeed.ui.modelif;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.common.RemoveNodesOperation;

public class SelectionInterface extends OperationExecuter {

	private List<? extends GenericNode> fSelected;
	
	public SelectionInterface(ModelOperationManager operationManager) {
		super(operationManager);
	}

	public void setTarget(List<? extends GenericNode> target){
		fSelected = target;
	}
	
	public boolean delete(AbstractFormPart source, IModelUpdateListener updateListener){
		return execute(new RemoveNodesOperation(fSelected), source, updateListener, Messages.DIALOG_REMOVE_CLASSES_PROBLEM_TITLE);
	}
	
	public boolean move(GenericNode newParent, AbstractFormPart source, IModelUpdateListener updateListener){
		return move(newParent, 0, source, updateListener);
	}

	public boolean move(GenericNode newParent, int newIndex, AbstractFormPart source, IModelUpdateListener updateListener){
		if(fSelected.size() != 1){
			return false;
		}
		GenericNodeInterface nodeIf = new NodeInterfaceFactory(getOperationManager()).getNodeInterface(fSelected.get(0));
		return nodeIf.move(newParent, newIndex, source, updateListener);
	}
	
	public boolean moveUpDown(boolean up, AbstractFormPart source, IModelUpdateListener updateListener) {
		if(fSelected.size() != 1){
			return false;
		}
		GenericNodeInterface nodeIf = new NodeInterfaceFactory(getOperationManager()).getNodeInterface(fSelected.get(0));
		return nodeIf.moveUpDown(up, source, updateListener);
	}
	
	protected boolean executeMoveOperation(IModelOperation moveOperation,
			AbstractFormPart source, IModelUpdateListener updateListener) {
		return execute(moveOperation, source, updateListener, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);	
	}

}
