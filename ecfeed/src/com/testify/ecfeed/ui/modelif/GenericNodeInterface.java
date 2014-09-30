package com.testify.ecfeed.ui.modelif;

import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modeladp.EImplementationStatus;
import com.testify.ecfeed.modeladp.IImplementationStatusResolver;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.java.ILoaderProvider;
import com.testify.ecfeed.modeladp.java.JavaImplementationStatusResolver;
import com.testify.ecfeed.modeladp.java.ModelClassLoader;
import com.testify.ecfeed.modeladp.operations.FactoryRemoveOperation;
import com.testify.ecfeed.modeladp.operations.FactoryShiftOperation;
import com.testify.ecfeed.modeladp.operations.GenericAddChildrenOperation;
import com.testify.ecfeed.modeladp.operations.GenericRemoveNodesOperation;
import com.testify.ecfeed.modeladp.operations.GenericShiftOperation;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;

public class GenericNodeInterface extends OperationExecuter{

	private ILoaderProvider fLoaderProvider;
	private GenericNode fTarget;
	private IImplementationStatusResolver fStatusResolver;
	
	public GenericNodeInterface(IModelUpdateContext updateContext) {
		super(updateContext);
		fLoaderProvider = new EclipseLoaderProvider();
		fStatusResolver = new JavaImplementationStatusResolver(fLoaderProvider);
	}

	public void setTarget(GenericNode target){
		fTarget = target;
	}
	
	public EImplementationStatus getImplementationStatus(GenericNode node){
		return fStatusResolver.getImplementationStatus(node);
	}
	
	public EImplementationStatus getImplementationStatus(){
		return getImplementationStatus(fTarget);
	}
	
	static public boolean validateName(String name){
		return true;
	}
	
	public String getName(){
		return fTarget.getName();
	}
	
	public boolean setName(String newName){
		return false;
	}

	public boolean remove(){
		return execute(FactoryRemoveOperation.getRemoveOperation(fTarget, true), Messages.DIALOG_REMOVE_NODE_PROBLEM_TITLE);
	}
	
	public boolean removeChildren(Collection<? extends GenericNode> children, String message){
		if(children == null || children.size() == 0) return false;
		for(GenericNode node : children){
			if(node.getParent() != fTarget) return false;
		}
		return execute(new GenericRemoveNodesOperation(children, true), message);
	}
	
	public boolean addChildren(Collection<? extends GenericNode> children){
		IModelOperation operation = new GenericAddChildrenOperation(fTarget, children, true);
		return execute(operation, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}
	
	public boolean addChildren(Collection<? extends GenericNode> children, int index){
		IModelOperation operation;
		if(index == -1){
			operation = new GenericAddChildrenOperation(fTarget, children, true);
		}
		else{
			operation = new GenericAddChildrenOperation(fTarget, children, index, true);
		}
		return execute(operation, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}
	
	public boolean pasteEnabled(Collection<? extends GenericNode> pasted){
		GenericAddChildrenOperation operation = new GenericAddChildrenOperation(fTarget, pasted, true);
		return operation.enabled();
	}
	
	public boolean pasteEnabled(Collection<? extends GenericNode> pasted, int index){
		GenericAddChildrenOperation operation = new GenericAddChildrenOperation(fTarget, pasted, index, true);
		return operation.enabled();
	}
	
	public boolean moveUpDown(boolean up) {
		try{
			GenericShiftOperation operation = FactoryShiftOperation.getShiftOperation(Arrays.asList(new GenericNode[]{fTarget}), up);
			if(operation.getShift() > 0){
				return executeMoveOperation(operation);
			}
		}catch(Exception e){}
		return false;
	}

	protected ModelClassLoader getLoader(boolean create, URLClassLoader parent){
		return fLoaderProvider.getLoader(create, parent);
	}

	protected boolean executeMoveOperation(IModelOperation moveOperation) {
		return execute(moveOperation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);	
	}
}
