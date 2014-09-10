package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.operations.ClassOperationAddMethod;
import com.testify.ecfeed.modelif.operations.ClassOperationAddMethods;
import com.testify.ecfeed.modelif.operations.ClassOperationRemoveMethod;
import com.testify.ecfeed.modelif.operations.ClassOperationRename;
import com.testify.ecfeed.ui.common.Constants;

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

	public static String getQualifiedName(ClassNode classNode){
		return classNode.getName();
	}
	
	public static String getQualifiedName(String packageName, String localName){
		return packageName + "." + localName;
	}
	
	public String getQualifiedName(){
		return getQualifiedName(fTarget);
	}
	
	public String getLocalName(){
		return getLocalName(fTarget);
	}

	public static String getLocalName(ClassNode classNode){
		return JavaUtils.getLocalName(classNode.getName());
	}

	public static String getPackageName(ClassNode classNode){
		return JavaUtils.getPackageName(classNode.getName());
	}

	public String getPackageName(){
		return getPackageName(fTarget);
	}

	@Override
	public boolean setName(String newName, AbstractFormPart source, IModelUpdateListener updateListener) {
		return setQualifiedName(newName, source, updateListener);
	}

	public boolean setQualifiedName(String newName, AbstractFormPart source, IModelUpdateListener updateListener){
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

	public boolean setLocalName(String newLocalName, AbstractFormPart source, IModelUpdateListener updateListener){
		String newQualifiedName = getPackageName() + "." + newLocalName;
		return setQualifiedName(newQualifiedName, source, updateListener);
	}

	public boolean setPackageName(String newPackageName, AbstractFormPart source, IModelUpdateListener updateListener){
		String newQualifiedName = newPackageName + "." + getLocalName(); 
		return setQualifiedName(newQualifiedName, source, updateListener);
	}

	public MethodNode addNewMethod(AbstractFormPart source, IModelUpdateListener updateListener){
		return addNewMethod(generateNewMethodName(), source, updateListener);
	}
	
	public MethodNode addNewMethod(String name, AbstractFormPart source, IModelUpdateListener updateListener){
		MethodNode method = new MethodNode(name);
		if(addMethod(method, source, updateListener)){
			return method;
		}
		return null;
	}
	
	public boolean addMethods(Collection<MethodNode> methods, AbstractFormPart source, IModelUpdateListener updateListener){
		return execute(new ClassOperationAddMethods(fTarget, methods, fTarget.getMethods().size()), source, updateListener, Messages.DIALOG_ADD_METHODS_PROBLEM_TITLE);
	}
	
	public boolean addMethod(MethodNode method, AbstractFormPart source, IModelUpdateListener updateListener){
		return execute(new ClassOperationAddMethod(fTarget, method, fTarget.getMethods().size()), source, updateListener, Messages.DIALOG_ADD_METHOD_PROBLEM_TITLE);
	}

	public boolean removeMethod(MethodNode method, AbstractFormPart source, IModelUpdateListener updateListener){
		if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
				Messages.DIALOG_REMOVE_METHOD_TITLE, 
				Messages.DIALOG_REMOVE_METHOD_MESSAGE)){
			return execute(new ClassOperationRemoveMethod(fTarget, method), source, updateListener, Messages.DIALOG_REMOVE_METHOD_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean removeMethods(Collection<MethodNode> methods, AbstractFormPart source, IModelUpdateListener updateListener){
		if(methods.size() == 0){
			return false;
		}
		if(methods.size() == 1){
			return removeMethod(new ArrayList<MethodNode>(methods).get(0), source, updateListener);
		}
		else if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_REMOVE_METHODS_TITLE, 
					Messages.DIALOG_REMOVE_METHODS_MESSAGE)){
			return removeChildren(methods, source, updateListener, Messages.DIALOG_REMOVE_METHODS_PROBLEM_TITLE);
		}
		return false;
	}

	public static List<MethodNode> getOtherMethods(ClassNode target){
		List<MethodNode> otherMethods = new ArrayList<MethodNode>();
		EclipseModelBuilder builder = new EclipseModelBuilder();
		try{
			ClassNode completeModel = builder.buildClassModel(JavaUtils.getQualifiedName(target), false);
			for(MethodNode method : completeModel.getMethods()){
				if(target.getMethod(method.getName(), method.getCategoriesTypes()) == null){
					otherMethods.add(method);
				}
			}
		}catch (ModelIfException e){}
		return otherMethods;
	}

	
	public List<MethodNode> getOtherMethods(){
		return getOtherMethods(fTarget);
	}

	private String generateNewMethodName() {
		String name = Constants.DEFAULT_NEW_METHOD_NAME;
		int i = 0;
		while(fTarget.getMethod(name, new ArrayList<String>()) != null){
			name = Constants.DEFAULT_NEW_METHOD_NAME + i++;
		}
		return name;
	}
}
