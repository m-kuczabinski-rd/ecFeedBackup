package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ImplementationStatus;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.JavaUtils;
import com.testify.ecfeed.abstraction.operations.ClassOperationAddMethod;
import com.testify.ecfeed.abstraction.operations.ClassOperationAddMethods;
import com.testify.ecfeed.abstraction.operations.ClassOperationRemoveMethod;
import com.testify.ecfeed.abstraction.operations.ClassOperationRename;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;

public class ClassInterface extends GenericNodeInterface {
	
	public ClassInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	private ClassNode fTarget;	
	
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
	public boolean setName(String newName) {
		return setQualifiedName(newName);
	}

	public boolean setQualifiedName(String newName){
		if(newName.equals(getQualifiedName())){
			return false;
		}
		if(getImplementationStatus(fTarget) != ImplementationStatus.NOT_IMPLEMENTED){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_TITLE, 
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE) == false){
				return false;
			}
		}
		return execute(new ClassOperationRename(fTarget, newName), Messages.DIALOG_RENAME_CLASS_PROBLEM_TITLE);
	}

	public boolean setLocalName(String newLocalName){
		String newQualifiedName = getPackageName() + "." + newLocalName;
		return setQualifiedName(newQualifiedName);
	}

	public boolean setPackageName(String newPackageName){
		String newQualifiedName = newPackageName + "." + getLocalName(); 
		return setQualifiedName(newQualifiedName);
	}

	public MethodNode addNewMethod(){
		return addNewMethod(generateNewMethodName());
	}
	
	public MethodNode addNewMethod(String name){
		MethodNode method = new MethodNode(name);
		if(addMethod(method)){
			return method;
		}
		return null;
	}
	
	public boolean addMethods(Collection<MethodNode> methods){
		IModelOperation operation = new ClassOperationAddMethods(fTarget, methods, fTarget.getMethods().size());
		return execute(operation, Messages.DIALOG_ADD_METHODS_PROBLEM_TITLE);
	}
	
	public boolean addMethod(MethodNode method){
		IModelOperation operation = new ClassOperationAddMethod(fTarget, method, fTarget.getMethods().size());
		return execute(operation, Messages.DIALOG_ADD_METHOD_PROBLEM_TITLE);
	}

	public boolean removeMethod(MethodNode method){
		if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
				Messages.DIALOG_REMOVE_METHOD_TITLE, 
				Messages.DIALOG_REMOVE_METHOD_MESSAGE)){
			IModelOperation operation = new ClassOperationRemoveMethod(fTarget, method);
			return execute(operation, Messages.DIALOG_REMOVE_METHOD_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean removeMethods(Collection<MethodNode> methods){
		if(methods.size() == 0){
			return false;
		}
		if(methods.size() == 1){
			return removeMethod(new ArrayList<MethodNode>(methods).get(0));
		}
		else if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_REMOVE_METHODS_TITLE, 
					Messages.DIALOG_REMOVE_METHODS_MESSAGE)){
			return removeChildren(methods, Messages.DIALOG_REMOVE_METHODS_PROBLEM_TITLE);
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

	public void reassignClass() {
		TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getDefault().getActiveShell());
		
		if (dialog.open() == IDialogConstants.OK_ID) {
			IType selectedClass = (IType)dialog.getFirstResult();
			String qualifiedName = selectedClass.getFullyQualifiedName();
			setQualifiedName(qualifiedName);
		}
	}
}
