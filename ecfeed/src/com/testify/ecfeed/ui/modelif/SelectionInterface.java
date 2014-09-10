package com.testify.ecfeed.ui.modelif;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.operations.GenericRemoveNodesOperation;
import com.testify.ecfeed.modelif.operations.GenericShiftOperation;
import com.testify.ecfeed.modelif.operations.FactoryShiftOperation;

public class SelectionInterface extends OperationExecuter {

	private List<? extends GenericNode> fSelected;
	
	public SelectionInterface(ModelOperationManager operationManager) {
		super(operationManager);
	}

	public void setTarget(List<? extends GenericNode> target){
		fSelected = target;
	}
	
	public boolean delete(AbstractFormPart source, IModelUpdateListener updateListener){
		return execute(new GenericRemoveNodesOperation(fSelected), source, updateListener, Messages.DIALOG_REMOVE_CLASSES_PROBLEM_TITLE);
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
		GenericNode parent = fSelected.get(0).getParent();
		for(GenericNode node : fSelected){
			if(node.getParent() != parent){
				return false;
			}
		}
		try{
			IModelOperation operation = (IModelOperation) parent.accept(new FactoryShiftOperation(fSelected, up));
			executeMoveOperation(operation, source, updateListener);
		}catch(Exception e){}
		return false;
	}
	
	protected boolean executeMoveOperation(IModelOperation moveOperation,
			AbstractFormPart source, IModelUpdateListener updateListener) {
		return execute(moveOperation, source, updateListener, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);	
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
