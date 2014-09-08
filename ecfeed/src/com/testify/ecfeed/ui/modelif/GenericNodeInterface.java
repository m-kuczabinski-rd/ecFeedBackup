package com.testify.ecfeed.ui.modelif;

import java.net.URLClassLoader;
import java.util.Collection;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IImplementationStatusResolver;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.ILoaderProvider;
import com.testify.ecfeed.modelif.java.JavaImplementationStatusResolver;
import com.testify.ecfeed.modelif.java.ModelClassLoader;
import com.testify.ecfeed.modelif.java.common.GenericAddChildrenOperation;
import com.testify.ecfeed.modelif.java.common.GenericMoveOperation;
import com.testify.ecfeed.modelif.java.common.GenericShiftOperation;
import com.testify.ecfeed.modelif.java.common.RemoveNodesOperation;
import com.testify.ecfeed.modelif.java.common.RemoveOperationFactory;

public class GenericNodeInterface extends OperationExecuter{

	private ILoaderProvider fLoaderProvider;
	private IImplementationStatusResolver fStatusResolver;
	private GenericNode fTarget;
	
	public GenericNodeInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
		fLoaderProvider = new EclipseLoaderProvider();
		fStatusResolver = new JavaImplementationStatusResolver(fLoaderProvider);
	}

	public void setTarget(GenericNode target){
		fTarget = target;
	}
	
	public ImplementationStatus implementationStatus(GenericNode node){
		return fStatusResolver.getImplementationStatus(node);
	}
	
	public ImplementationStatus implementationStatus(){
		return fStatusResolver.getImplementationStatus(fTarget);
	}
	
	public boolean removeNodes(Collection<? extends GenericNode> nodes, AbstractFormPart source, IModelUpdateListener updateListener){
		return execute(new RemoveNodesOperation(nodes), source, updateListener, Messages.DIALOG_REMOVE_CLASSES_PROBLEM_TITLE);
	}
	
	static public boolean validateName(String name){
		return true;
	}
	
	public String getName(){
		return fTarget.getName();
	}
	
	public boolean setName(String newName, AbstractFormPart source, IModelUpdateListener updateListener){
		return false;
	}

	public boolean remove(AbstractFormPart source, IModelUpdateListener updateListener){
		return execute(RemoveOperationFactory.getRemoveOperation(fTarget), source, updateListener, Messages.DIALOG_REMOVE_NODE_PROBLEM_TITLE);
	}
	
	public boolean move(GenericNode newParent, AbstractFormPart source, IModelUpdateListener updateListener){
		return move(newParent, 0, source, updateListener);
	}
	
	public boolean removeChildren(Collection<? extends GenericNode> children, AbstractFormPart source, IModelUpdateListener updateListener, String message){
		if(children == null || children.size() == 0) return false;
		for(GenericNode node : children){
			if(node.getParent() != fTarget) return false;
		}
		return execute(new RemoveNodesOperation(children), source, updateListener, message);
	}
	
	public boolean addChildren(Collection<? extends GenericNode> children, AbstractFormPart source, IModelUpdateListener updateListener, String message){
		IModelOperation operation = new GenericAddChildrenOperation(fTarget, children);
		return execute(operation, source, updateListener, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}
	
	public boolean canAddChildren(Collection<? extends GenericNode> children){
		GenericAddChildrenOperation operation = new GenericAddChildrenOperation(fTarget, children);
		return operation.posible();
	}
	
	public boolean move(GenericNode newParent, int newIndex, AbstractFormPart source, IModelUpdateListener updateListener){
		try {
			IModelOperation operation = new GenericMoveOperation(fTarget, newParent, newIndex);
			return executeMoveOperation(operation, source, updateListener);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean moveUpDown(boolean up, AbstractFormPart source, IModelUpdateListener updateListener) {
		try{
			int index = GenericShiftOperation.nextAllowedIndex(fTarget, up);
			if(index != -1){
				return executeMoveOperation(new GenericMoveOperation(fTarget, (GenericNode)fTarget.getParent(), index), source, updateListener);
			}
		}catch(Exception e){}
		return false;
	}

	protected ModelClassLoader getLoader(boolean create, URLClassLoader parent){
		return fLoaderProvider.getLoader(create, parent);
	}

	protected boolean executeMoveOperation(IModelOperation moveOperation,
			AbstractFormPart source, IModelUpdateListener updateListener) {
		return execute(moveOperation, source, updateListener, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);	
	}
}
