package com.testify.ecfeed.ui.modelif;

import java.util.List;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaMethodUtils;
import com.testify.ecfeed.modelif.java.method.MethodOperationRename;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class MethodInterface extends GenericNodeInterface {

	private MethodNode fTarget;

	public MethodInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	public void setTarget(MethodNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	public MethodNode getTarget(){
		return fTarget;
	}

	public List<String> getArgTypes(MethodNode method) {
		return JavaMethodUtils.getArgTypes(method);
	}

	public List<String> getArgNames(MethodNode method) {
		return JavaMethodUtils.getArgNames(method);
	}

	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(new MethodOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}

	
}
