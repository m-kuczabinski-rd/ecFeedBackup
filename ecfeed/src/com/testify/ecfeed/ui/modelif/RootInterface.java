package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.root.RootOperationAddNewClass;
import com.testify.ecfeed.modelif.java.root.RootOperationRename;
import com.testify.ecfeed.ui.common.GenericNodeInterface;
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
	
	public void renameModel(String newName, BasicSection source, IModelUpdateListener updateListener){
		execute(new RootOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_MODEL_PROBLEM_TITLE);
	}

	public ClassNode addNewClass(String className, BasicSection source, IModelUpdateListener updateListener){
		if(className == null){
			className = generateClassName();
		}
		ClassNode addedClass = new ClassNode(className);
		if(execute(new RootOperationAddNewClass(fTarget, addedClass), source, updateListener, Messages.DIALOG_ADD_NEW_CLASS_PROBLEM_TITLE)){
			return addedClass;
		}
		return null;
	}

	private String generateClassName() {
		String className = Constants.DEFAULT_NEW_PACKAGE_NAME + "." + Constants.DEFAULT_NEW_CLASS_NAME;
		int i = 0;
		while(fTarget.getClassModel(className + String.valueOf(i)) != null){
			i++;
		}
		return className + String.valueOf(i);
	}
}
