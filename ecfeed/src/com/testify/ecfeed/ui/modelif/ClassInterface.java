package com.testify.ecfeed.ui.modelif;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.classx.ClassOperationRename;
import com.testify.ecfeed.modelif.java.classx.JavaClassUtils;
import com.testify.ecfeed.modelif.java.common.ImplementationStatus;
import com.testify.ecfeed.ui.common.GenericNodeInterface;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

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
		return JavaClassUtils.getLocalName(fTarget);
	}

	public String getPackageName(){
		return JavaClassUtils.getPackageName(fTarget);
	}

	public ImplementationStatus implementationStatus(){
		return JavaClassUtils.implementationStatus(fTarget);
	}
	
	public void setQualifiedName(String newName, BasicSection source, IModelUpdateListener updateListener){
		if(JavaClassUtils.implementationStatus(fTarget) != ImplementationStatus.NOT_IMPLEMENTED){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_TITLE, 
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE)){
				execute(new ClassOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_CLASS_PROBLEM_TITLE);
			}
		}
	}

	public void setLocalName(String newLocalName, BasicSection source, IModelUpdateListener updateListener){
		String newQualifiedName = getPackageName() + "." + newLocalName;
		setQualifiedName(newQualifiedName, source, updateListener);
	}

	public void setPackageName(String newPackageName, BasicSection source, IModelUpdateListener updateListener){
		String newQualifiedName = newPackageName + "." + getLocalName(); 
		setQualifiedName(newQualifiedName, source, updateListener);
	}
}
