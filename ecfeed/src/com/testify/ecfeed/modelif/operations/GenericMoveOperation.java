package com.testify.ecfeed.modelif.operations;

import java.util.Arrays;
import java.util.List;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class GenericMoveOperation extends BulkOperation {

	public GenericMoveOperation(GenericNode target, GenericNode newParent, int newIndex) throws ModelIfException {
		this(Arrays.asList(new GenericNode[]{target}), newParent, newIndex);
	}

	
	public GenericMoveOperation(List<? extends GenericNode> moved, GenericNode newParent, int newIndex) throws ModelIfException {
		super(true);
		try {
			//all nodes have parents other than newParent
			if(externalNodes(moved, newParent)){
				for(GenericNode node : moved){
					addOperation((IModelOperation)node.getParent().accept(new RemoveChildOperationFactory(node)));
					addOperation((IModelOperation)newParent.accept(new AddChildOperationProvider(node, newIndex)));
				}
			}
			else if(internalNodes(moved, newParent)){
				boolean up = moved.get(0).getIndex() > newIndex;
				GenericShiftOperation operation = (GenericShiftOperation)newParent.accept(new ShiftOperationFactory(moved, up));
				addOperation(operation);
			}
		} catch (Exception e) {
			throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}
	
	protected boolean externalNodes(List<? extends GenericNode> moved, GenericNode newParent){
		for(GenericNode node : moved){
			if(node.getParent() == newParent){
				return false;
			}
		}
		return true;
	}

	protected boolean internalNodes(List<? extends GenericNode> moved, GenericNode newParent){
		for(GenericNode node : moved){
			if(node.getParent() != newParent){
				return false;
			}
		}
		return true;
	}
}
