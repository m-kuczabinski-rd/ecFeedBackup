package com.testify.ecfeed.modeladp.operations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionedNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

public class GenericMoveOperation extends BulkOperation {

	public GenericMoveOperation(List<? extends GenericNode> moved, GenericNode newParent) throws ModelOperationException {
		this(moved, newParent, -1);
	}
	
	public GenericMoveOperation(List<? extends GenericNode> moved, GenericNode newParent, int newIndex) throws ModelOperationException {
		super(OperationNames.MOVE, true);
		Set<MethodNode> methodsInvolved = new HashSet<>();
		try {
			//all nodes have parents other than newParent
			if(externalNodes(moved, newParent)){
				for(GenericNode node : moved){
					if(node instanceof PartitionedNode){
						methodsInvolved.add(((PartitionedNode)node).getCategory().getMethod());
					}
					addOperation((IModelOperation)node.getParent().accept(new FactoryRemoveChildOperation(node, false)));
					if(newIndex != -1){
						addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(node, newIndex, false)));
					}
					else{
						addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(node, false)));
					}
					for(MethodNode method : methodsInvolved){
						addOperation(new MethodOperationMakeConsistent(method));
					}
				}
			}
			else if(internalNodes(moved, newParent)){
				boolean up = moved.get(0).getIndex() > newIndex;
				GenericShiftOperation operation = (GenericShiftOperation)newParent.accept(new FactoryShiftOperation(moved, up));
				addOperation(operation);
			}
		} catch (Exception e) {
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
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
