package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.root.RootOperationRename;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class RootInterface extends GenericNodeInterface {

	private RootNode fTarget;
	
	public RootInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}
	
	public void setTarget(RootNode target){
		fTarget = target;
	}
	
	public void setName(String newName, BasicSection source, IModelUpdateListener updateListener){
		execute(new RootOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_MODEL_PROBLEM_TITLE);
	}

}
