package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaClassUtils;
import com.testify.ecfeed.modelif.java.classx.ClassOperationAddMethod;
import com.testify.ecfeed.modelif.java.classx.ClassOperationAddMethods;
import com.testify.ecfeed.modelif.java.classx.ClassOperationRemoveMethod;
import com.testify.ecfeed.modelif.java.classx.ClassOperationRemoveMethods;
import com.testify.ecfeed.modelif.java.classx.ClassOperationRename;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class ClassInterface extends GenericNodeInterface {
	private ClassNode fTarget;	
	
	public ClassInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}
	
	public void setTarget(ClassNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	public ClassNode getTarget(){
		return fTarget;
	}

	public String getQualifiedName(){
		return JavaClassUtils.getQualifiedName(fTarget);
	}
	
	public String getLocalName(){
		return JavaClassUtils.getLocalName(fTarget);
	}

	public String getPackageName(){
		return JavaClassUtils.getPackageName(fTarget);
	}

	public boolean setQualifiedName(String newName, BasicSection source, IModelUpdateListener updateListener){
		if(newName.equals(getQualifiedName())){
			return false;
		}
		if(implementationStatus(fTarget) != ImplementationStatus.NOT_IMPLEMENTED){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_TITLE, 
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE) == false){
				return false;
			}
		}
		return execute(new ClassOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_CLASS_PROBLEM_TITLE);
	}

	public boolean setLocalName(String newLocalName, BasicSection source, IModelUpdateListener updateListener){
		String newQualifiedName = getPackageName() + "." + newLocalName;
		return setQualifiedName(newQualifiedName, source, updateListener);
	}

	public boolean setPackageName(String newPackageName, BasicSection source, IModelUpdateListener updateListener){
		String newQualifiedName = newPackageName + "." + getLocalName(); 
		return setQualifiedName(newQualifiedName, source, updateListener);
	}
	
	public MethodNode addNewMethod(BasicSection source, IModelUpdateListener updateListener){
		return addNewMethod(generateNewMethodName(), source, updateListener);
	}
	
	public MethodNode addNewMethod(String name, BasicSection source, IModelUpdateListener updateListener){
		MethodNode method = new MethodNode(name);
		if(addMethod(method, source, updateListener)){
			return method;
		}
		return null;
	}
	
	public boolean addMethods(Collection<MethodNode> methods, BasicSection source, IModelUpdateListener updateListener){
		return execute(new ClassOperationAddMethods(fTarget, methods), source, updateListener, Messages.DIALOG_ADD_METHODS_PROBLEM_TITLE);
	}
	
	public boolean addMethod(MethodNode method, BasicSection source, IModelUpdateListener updateListener){
		return execute(new ClassOperationAddMethod(fTarget, method), source, updateListener, Messages.DIALOG_ADD_METHOD_PROBLEM_TITLE);
	}

	public boolean removeMethod(MethodNode method, BasicSection source, IModelUpdateListener updateListener){
		if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
				Messages.DIALOG_REMOVE_METHOD_TITLE, 
				Messages.DIALOG_REMOVE_METHOD_MESSAGE)){
			return execute(new ClassOperationRemoveMethod(fTarget, method), source, updateListener, Messages.DIALOG_REMOVE_METHOD_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean removeMethods(Collection<MethodNode> methods, BasicSection source, IModelUpdateListener updateListener){
		if(methods.size() == 0){
			return false;
		}
		if(methods.size() == 1){
			return removeMethod(new ArrayList<MethodNode>(methods).get(0), source, updateListener);
		}
		else if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_REMOVE_METHODS_TITLE, 
					Messages.DIALOG_REMOVE_METHODS_MESSAGE)){
			return execute(new ClassOperationRemoveMethods(fTarget, methods), source, updateListener, Messages.DIALOG_REMOVE_METHODS_PROBLEM_TITLE);
		}
		return false;
	}

	private String generateNewMethodName() {
		String name = Constants.DEFAULT_NEW_METHOD_NAME;
		int i = 0;
		while(fTarget.getMethod(name, new ArrayList<String>()) != null){
			name = Constants.DEFAULT_NEW_METHOD_NAME + i++;
		}
		return name;
	}
	
//	public MethodNode getMethod(String name, List<String> argTypes){
//		return JavaMethodUtils.getMethod(fTarget, name, argTypes);
//	}
}
