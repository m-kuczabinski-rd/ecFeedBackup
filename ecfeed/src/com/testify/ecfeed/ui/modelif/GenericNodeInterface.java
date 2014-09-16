package com.testify.ecfeed.ui.modelif;

import java.net.URLClassLoader;
import java.util.Collection;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IImplementationStatusResolver;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.java.ILoaderProvider;
import com.testify.ecfeed.modelif.java.JavaImplementationStatusResolver;
import com.testify.ecfeed.modelif.java.ModelClassLoader;
import com.testify.ecfeed.modelif.operations.FactoryRemoveOperation;
import com.testify.ecfeed.modelif.operations.FactoryShiftOperation;
import com.testify.ecfeed.modelif.operations.GenericAddChildrenOperation;
import com.testify.ecfeed.modelif.operations.GenericMoveOperation;
import com.testify.ecfeed.modelif.operations.GenericRemoveNodesOperation;
import com.testify.ecfeed.modelif.operations.GenericShiftOperation;
import com.testify.ecfeed.ui.common.Messages;

public class GenericNodeInterface extends OperationExecuter implements IImplementationStatusResolver{

	private ILoaderProvider fLoaderProvider;
	private IImplementationStatusResolver fStatusResolver;
	private GenericNode fTarget;
	
	public GenericNodeInterface() {
		fLoaderProvider = new EclipseLoaderProvider();
		fStatusResolver = new JavaImplementationStatusResolver(fLoaderProvider);
	}

	public void setTarget(GenericNode target){
		fTarget = target;
	}
	
	public ImplementationStatus getImplementationStatus(GenericNode node){
		return fStatusResolver.getImplementationStatus(node);
	}
	
	public ImplementationStatus implementationStatus(){
		return fStatusResolver.getImplementationStatus(fTarget);
	}
	
	static public boolean validateName(String name){
		return true;
	}
	
	public String getName(){
		return fTarget.getName();
	}
	
	public boolean setName(String newName, IModelUpdateContext context){
		return false;
	}

	public boolean remove(IModelUpdateContext context){
		return execute(FactoryRemoveOperation.getRemoveOperation(fTarget), context, Messages.DIALOG_REMOVE_NODE_PROBLEM_TITLE);
	}
	
	public boolean move(GenericNode newParent, IModelUpdateContext context){
		return move(newParent, 0, context);
	}
	
	public boolean removeChildren(Collection<? extends GenericNode> children, IModelUpdateContext context, String message){
		if(children == null || children.size() == 0) return false;
		for(GenericNode node : children){
			if(node.getParent() != fTarget) return false;
		}
		return execute(new GenericRemoveNodesOperation(children), context, message);
	}
	
	public boolean addChildren(Collection<? extends GenericNode> children, IModelUpdateContext context){
		IModelOperation operation = new GenericAddChildrenOperation(fTarget, children);
		return execute(operation, context, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}
	
	public boolean addChildren(Collection<? extends GenericNode> children, int index, IModelUpdateContext context, String message){
		IModelOperation operation;
		if(index == -1){
			operation = new GenericAddChildrenOperation(fTarget, children);
		}
		else{
			operation = new GenericAddChildrenOperation(fTarget, children, index);
		}
		return execute(operation, context, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}
	
	public boolean pasteEnabled(Collection<? extends GenericNode> pasted){
		GenericAddChildrenOperation operation = new GenericAddChildrenOperation(fTarget, pasted);
		return operation.enabled();
	}
	
	public boolean pasteEnabled(Collection<? extends GenericNode> pasted, int index){
		GenericAddChildrenOperation operation = new GenericAddChildrenOperation(fTarget, pasted, index);
		return operation.enabled();
	}
	
	public boolean move(GenericNode newParent, int newIndex, IModelUpdateContext context){
		try {
			IModelOperation operation = new GenericMoveOperation(fTarget, newParent, newIndex);
			return executeMoveOperation(operation, context);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean moveUpDown(boolean up, IModelUpdateContext context) {
		try{
			GenericShiftOperation operation = (GenericShiftOperation)fTarget.getParent().accept(new FactoryShiftOperation(fTarget, up));
			if(operation.getShift() > 0){
				return executeMoveOperation(operation, context);
			}
		}catch(Exception e){}
		return false;
	}

	protected ModelClassLoader getLoader(boolean create, URLClassLoader parent){
		return fLoaderProvider.getLoader(create, parent);
	}

	protected boolean executeMoveOperation(IModelOperation moveOperation, IModelUpdateContext context) {
		return execute(moveOperation, context, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);	
	}
}
