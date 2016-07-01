/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.adapter.operations.ChoiceOperationAddLabel;
import com.ecfeed.core.adapter.operations.ChoiceOperationAddLabels;
import com.ecfeed.core.adapter.operations.ChoiceOperationRemoveLabels;
import com.ecfeed.core.adapter.operations.ChoiceOperationRenameLabel;
import com.ecfeed.core.adapter.operations.ChoiceOperationSetValue;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.JavaModelAnalyser;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;

public class ChoiceInterface extends ChoicesParentInterface{

	public ChoiceInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext, fileInfoProvider);
	}

	public void setValue(String newValue){
		IModelOperation operation = new ChoiceOperationSetValue(getTarget(), newValue, new EclipseTypeAdapterProvider());
		execute(operation, Messages.DIALOG_SET_CHOICE_VALUE_PROBLEM_TITLE);
	}

	public String getValue() {
		return getTarget().getValueString();
	}

	@Override
	public AbstractParameterNode getParameter() {
		return getTarget().getParameter();
	}

	public boolean removeLabels(Collection<String> labels) {
		boolean removeMentioningConstraints = false;
		for(String label : labels){
			if(getTarget().getParameter().mentioningConstraints(label).size() > 0 && getTarget().getParameter().getLabeledChoices(label).size() == 1){
				removeMentioningConstraints = true;
			}
		}
		if(removeMentioningConstraints){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_REMOVE_LABELS_WARNING_TITLE,
					Messages.DIALOG_REMOVE_LABELS_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return execute(new ChoiceOperationRemoveLabels(getTarget(), labels), Messages.DIALOG_REMOVE_LABEL_PROBLEM_TITLE);
	}

	public String addNewLabel() {
		String newLabel = Constants.DEFAULT_NEW_PARTITION_LABEL;
		int i = 1;
		while(getTarget().getLeafLabels().contains(newLabel)){
			newLabel = Constants.DEFAULT_NEW_PARTITION_LABEL + "(" + i + ")";
			i++;
		}
		if(addLabel(newLabel)){
			return newLabel;
		}
		return null;
	}

	public boolean addLabels(List<String> labels) {
		IModelOperation operation = new ChoiceOperationAddLabels(getTarget(), labels);
		return execute(operation, Messages.DIALOG_ADD_LABEL_PROBLEM_TITLE);
	}

	public boolean addLabel(String newLabel) {
		IModelOperation operation = new ChoiceOperationAddLabel(getTarget(), newLabel);
		return execute(operation, Messages.DIALOG_ADD_LABEL_PROBLEM_TITLE);
	}

	public boolean isLabelInherited(String label) {
		return getTarget().getInheritedLabels().contains(label);
	}

	public boolean renameLabel(String label, String newValue) {
		if(label.equals(newValue)){
			return false;
		}
		if(getTarget().getInheritedLabels().contains(newValue)){
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_RENAME_LABELS_ERROR_TITLE,
					Messages.DIALOG_LABEL_IS_ALREADY_INHERITED);
			return false;
		}
		if(getTarget().getLeafLabels().contains(newValue)){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_RENAME_LABELS_WARNING_TITLE,
					Messages.DIALOG_DESCENDING_LABELS_WILL_BE_REMOVED_WARNING_TITLE) == false){
				return false;
			}
		}

		IModelOperation operation = new ChoiceOperationRenameLabel(getTarget(), label, newValue);
		return execute(operation, Messages.DIALOG_CHANGE_LABEL_PROBLEM_TITLE);
	}

	@Override
	public boolean goToImplementationEnabled(){
		if(JavaUtils.isPrimitive(getTarget().getParameter().getType())){
			return false;
		}
		if(getTarget().isAbstract()){
			return false;
		}
		return super.goToImplementationEnabled();
	}

	@Override
	public void goToImplementation(){
		try{
			IType type = JavaModelAnalyser.getIType(getParameter().getType());
			if(type != null && getTarget().isAbstract() == false){
				for(IField field : type.getFields()){
					if(field.getElementName().equals(getTarget().getValueString())){
						JavaUI.openInEditor(field);
						break;
					}
				}
			}
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
	}

	@Override
	protected ChoiceNode getTarget(){
		return (ChoiceNode)super.getTarget();
	}

	@Override
	public boolean commentsImportExportEnabled(){
		return super.commentsImportExportEnabled() && getImplementationStatus() != EImplementationStatus.NOT_IMPLEMENTED;
	}

	@Override
	public String canAddChildren(Collection<? extends AbstractNode> newChildren) {
		String existingChoiceName = choiceNameAlreadyExists(newChildren);

		if (existingChoiceName != null) {
			return Messages.CHOICE_ALREADY_EXISTS(existingChoiceName); 
		}
		return null;
	}

	private String choiceNameAlreadyExists(Collection<? extends AbstractNode> newChildren) {
		List<String> existingChildrenNames = getListOfChildrenChoiceNames();

		for (AbstractNode newChild : newChildren) {
			String newChildName = newChild.getName();
			if (existingChildrenNames.indexOf(newChildName) != -1) {
				return newChildName;
			}
		}
		return null;
	}

	List<String> getListOfChildrenChoiceNames() {
		ChoiceNode choiceNode = getTarget();
		List<ChoiceNode> existingChoices = choiceNode.getChoices();

		List<String> names = new ArrayList<String>();

		for (ChoiceNode choice : existingChoices) {
			names.add(choice.getName());
		}
		return names;
	}
}
