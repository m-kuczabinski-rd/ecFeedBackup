package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.common.GenericNodeInterface;

public class ClassInterface extends GenericNodeInterface {
	private ClassNode fTarget;	
	
	public ClassInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}
	
	public void setTarget(ClassNode target){
		fTarget = target;
	}
	
	public String getQualifiedName(){
		return fTarget.getName();
	}
	
	public String getLocalName(){
		String qualifiedName = getQualifiedName();
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)?qualifiedName: qualifiedName.substring(lastDotIndex + 1);
	}
	
	public String getPackageName(){
		String qualifiedName = getQualifiedName();
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)? "" : qualifiedName.substring(0, lastDotIndex);
	}

}
