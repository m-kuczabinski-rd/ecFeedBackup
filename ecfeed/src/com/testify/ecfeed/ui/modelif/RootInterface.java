package com.testify.ecfeed.ui.modelif;

import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.operations.RootOperationAddClasses;
import com.testify.ecfeed.abstraction.operations.RootOperationAddNewClass;
import com.testify.ecfeed.abstraction.operations.RootOperationRename;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;

public class RootInterface extends GenericNodeInterface {

	private RootNode fTarget;
	
	public void setTarget(RootNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	public RootNode getTarget() {
		return fTarget;
	}

	public boolean setName(String newName, IModelUpdateContext context){
		return execute(new RootOperationRename(fTarget, newName), context, Messages.DIALOG_RENAME_MODEL_PROBLEM_TITLE);
	}

	public ClassNode addNewClass(IModelUpdateContext context){
		return addNewClass(generateClassName(), context);
	}
	
	public ClassNode addNewClass(String className, IModelUpdateContext context){
		ClassNode addedClass = new ClassNode(className);
		if(execute(new RootOperationAddNewClass(fTarget, addedClass, fTarget.getClasses().size()), context, Messages.DIALOG_ADD_NEW_CLASS_PROBLEM_TITLE)){
			return addedClass;
		}
		return null;
	}

	public ClassNode addImplementedClass(IModelUpdateContext context){
		TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getCurrent().getActiveShell());

		if (dialog.open() == IDialogConstants.OK_ID) {
			IType selectedClass = (IType)dialog.getFirstResult();
			boolean testOnly = dialog.getTestOnlyFlag();

			if(selectedClass != null){
				ClassNode classModel;
				try {
					classModel = new EclipseModelBuilder().buildClassModel(selectedClass, testOnly);
					if(execute(new RootOperationAddNewClass(fTarget, classModel, fTarget.getClasses().size()), context, Messages.DIALOG_ADD_NEW_CLASS_PROBLEM_TITLE)){
						return classModel;
					}
				} catch (ModelIfException e) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), 
							Messages.DIALOG_ADD_NEW_CLASS_PROBLEM_TITLE, 
							e.getMessage());
				}
			}
		}
		return null;
	}

	public boolean removeClasses(Collection<ClassNode> removedClasses, IModelUpdateContext context){
		if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
				Messages.DIALOG_REMOVE_CLASSES_TITLE, 
				Messages.DIALOG_REMOVE_CLASSES_MESSAGE)){
			return removeChildren(removedClasses, context, Messages.DIALOG_REMOVE_CLASSES_PROBLEM_TITLE);
		}
		return false;
	}
	
	public boolean addClasses(Collection<ClassNode> classes, IModelUpdateContext context) {
		IModelOperation operation = new RootOperationAddClasses(fTarget, classes, fTarget.getClasses().size());
		return execute(operation, context, Messages.DIALOG_ADD_METHODS_PROBLEM_TITLE);
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
