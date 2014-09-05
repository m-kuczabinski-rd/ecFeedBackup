package com.testify.ecfeed.ui.modelif;

import java.net.URLClassLoader;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IImplementationStatusResolver;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.ILoaderProvider;
import com.testify.ecfeed.modelif.java.JavaImplementationStatusResolver;
import com.testify.ecfeed.modelif.java.ModelClassLoader;
import com.testify.ecfeed.modelif.java.common.GenericMoveOperation;
import com.testify.ecfeed.modelif.java.common.GenericShiftOperation;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.editor.BasicSection;

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
	
	static public boolean validateName(String name){
		return true;
	}
	
	public String getName(){
		return fTarget.getName();
	}
	
	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener){
		return false;
	}

	public boolean move(GenericNode newParent, BasicSection source, IModelUpdateListener updateListener){
		return move(newParent, 0, source, updateListener);
	}
	
	public boolean move(GenericNode newParent, int newIndex, BasicSection source, IModelUpdateListener updateListener){
		try {
			IModelOperation operation = new GenericMoveOperation(fTarget, newParent, newIndex);
			return executeMoveOperation(operation, source, updateListener);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean moveUpDown(boolean up, BasicSection source, IModelUpdateListener updateListener) {
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
			BasicSection source, IModelUpdateListener updateListener) {
		return execute(moveOperation, source, updateListener, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);	
	}
	
}
