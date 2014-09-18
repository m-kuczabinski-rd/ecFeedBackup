package com.testify.ecfeed.ui.modelif;

import java.net.URLClassLoader;
import java.util.Collection;

import com.testify.ecfeed.abstraction.IImplementationStatusResolver;
import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ImplementationStatus;
import com.testify.ecfeed.abstraction.java.ILoaderProvider;
import com.testify.ecfeed.abstraction.java.JavaImplementationStatusResolver;
import com.testify.ecfeed.abstraction.java.ModelClassLoader;
import com.testify.ecfeed.abstraction.operations.FactoryRemoveOperation;
import com.testify.ecfeed.abstraction.operations.FactoryShiftOperation;
import com.testify.ecfeed.abstraction.operations.GenericAddChildrenOperation;
import com.testify.ecfeed.abstraction.operations.GenericRemoveNodesOperation;
import com.testify.ecfeed.abstraction.operations.GenericShiftOperation;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;

public class GenericNodeInterface extends OperationExecuter{

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
		return execute(FactoryRemoveOperation.getRemoveOperation(fTarget, true), context, Messages.DIALOG_REMOVE_NODE_PROBLEM_TITLE);
	}
	
	public boolean removeChildren(Collection<? extends GenericNode> children, IModelUpdateContext context, String message){
		if(children == null || children.size() == 0) return false;
		for(GenericNode node : children){
			if(node.getParent() != fTarget) return false;
		}
		return execute(new GenericRemoveNodesOperation(children, true), context, message);
	}
	
	public boolean addChildren(Collection<? extends GenericNode> children, IModelUpdateContext context){
		IModelOperation operation = new GenericAddChildrenOperation(fTarget, children, true);
		return execute(operation, context, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}
	
	public boolean addChildren(Collection<? extends GenericNode> children, int index, IModelUpdateContext context){
		IModelOperation operation;
		if(index == -1){
			operation = new GenericAddChildrenOperation(fTarget, children, true);
		}
		else{
			operation = new GenericAddChildrenOperation(fTarget, children, index, true);
		}
		return execute(operation, context, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}
	
	public boolean pasteEnabled(Collection<? extends GenericNode> pasted){
		GenericAddChildrenOperation operation = new GenericAddChildrenOperation(fTarget, pasted, true);
		return operation.enabled();
	}
	
	public boolean pasteEnabled(Collection<? extends GenericNode> pasted, int index){
		GenericAddChildrenOperation operation = new GenericAddChildrenOperation(fTarget, pasted, index, true);
		return operation.enabled();
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
