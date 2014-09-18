package com.testify.ecfeed.abstraction.operations;

import java.util.List;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.GenericNode;

public class GenericMoveOperation extends BulkOperation {

	public GenericMoveOperation(List<? extends GenericNode> moved, GenericNode newParent) throws ModelIfException {
		this(moved, newParent, -1);
	}
	
	public GenericMoveOperation(List<? extends GenericNode> moved, GenericNode newParent, int newIndex) throws ModelIfException {
		super(true);
		try {
			//all nodes have parents other than newParent
			if(externalNodes(moved, newParent)){
				for(GenericNode node : moved){
					addOperation((IModelOperation)node.getParent().accept(new FactoryRemoveChildOperation(node)));
					if(newIndex != -1){
						addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(node, newIndex)));
					}
					else{
						addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(node)));
					}
				}
			}
			else if(internalNodes(moved, newParent)){
				boolean up = moved.get(0).getIndex() > newIndex;
				GenericShiftOperation operation = (GenericShiftOperation)newParent.accept(new FactoryShiftOperation(moved, up));
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
