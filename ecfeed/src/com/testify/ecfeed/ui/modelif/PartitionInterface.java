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

import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.category.GenericOperationAddPartition;
import com.testify.ecfeed.modelif.java.common.RemoveNodesOperation;
import com.testify.ecfeed.modelif.java.partition.PartitionOperationAddLabel;
import com.testify.ecfeed.modelif.java.partition.PartitionOperationRemoveLabels;
import com.testify.ecfeed.modelif.java.partition.PartitionOperationRename;
import com.testify.ecfeed.modelif.java.partition.PartitionOperationRenameLabel;
import com.testify.ecfeed.modelif.java.partition.PartitionOperationSetValue;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class PartitionInterface extends GenericNodeInterface{

	PartitionNode fTarget;
	
	public PartitionInterface(ModelOperationManager modelAbstraction) {
		super(modelAbstraction);
	}

	public void setTarget(PartitionNode partition){
		super.setTarget(partition);
		fTarget = partition;
	}
	
	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener){
		return execute(new PartitionOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_PARTITION_PROBLEM_TITLE);
	}

	public void setValue(String newValue, BasicSection source, IModelUpdateListener updateListener){
		execute(new PartitionOperationSetValue(fTarget, newValue), source, updateListener, Messages.DIALOG_SET_PARTITION_VALUE_PROBLEM_TITLE);
	}

	public String getValue() {
		return fTarget.getValueString();
	}

	public CategoryNode getCategory() {
		return fTarget.getCategory();
	}

	public PartitionNode addNewPartition(BasicSection source, IModelUpdateListener updateListener) {
		String name = generatePartitionName();
		String value = generateNewPartitionValue();
		PartitionNode newPartition = new PartitionNode(name, value);
		if(addPartition(newPartition, source, updateListener)){
			return newPartition;
		}
		return null;
	}
	
	public boolean addPartition(PartitionNode newPartition, BasicSection source, IModelUpdateListener updateListener) {
		IModelOperation operation = new GenericOperationAddPartition(fTarget, newPartition, fTarget.getPartitions().size()); 
		return execute(operation, source, updateListener, Messages.DIALOG_ADD_PARTITION_PROBLEM_TITLE);
	}

	public boolean removePartitions(Collection<PartitionNode> partitions, BasicSection source, IModelUpdateListener updateListener) {
		boolean displayWarning = false;
		for(PartitionNode p : partitions){
			if(fTarget.getCategory().getMethod().mentioningConstraints(p).size() > 0 || fTarget.getCategory().getMethod().mentioningTestCases(p).size() > 0){
				displayWarning = true;
				break;
			}
		}
		if(displayWarning){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_REMOVE_PARTITION_WARNING_TITLE, 
					Messages.DIALOG_REMOVE_PARTITION_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return execute(new RemoveNodesOperation(partitions), source, updateListener, Messages.DIALOG_REMOVE_PARTITIONS_PROBLEM_TITLE);
	}

	protected String generateNewPartitionValue() {
		String type = fTarget.getCategory().getType();
		EclipseModelBuilder builder = new EclipseModelBuilder();
		String value = builder.getDefaultExpectedValue(type);
		if(CategoryInterface.isPrimitive(type) == false && builder.getSpecialValues(type).size() == 0){
			int i = 0;
			while(fTarget.getCategory().getLeafPartitionValues().contains(value)){
				value = builder.getDefaultExpectedValue(type) + i++; 
			}
		}
		return value;
	}

	protected String generatePartitionName(){
		String name = Constants.DEFAULT_NEW_PARTITION_NAME;
		int i = 0;
		while(fTarget.getPartitionNames().contains(name)){
			name = Constants.DEFAULT_NEW_PARTITION_NAME + i++; 
		}
		return name;
	}

	public boolean removeLabels(Collection<String> labels, BasicSection source, IModelUpdateListener updateListener) {
		MethodNode method = fTarget.getCategory().getMethod();
		boolean removeMentioningConstraints = false;
		for(String label : labels){
			if(method.mentioningConstraints(fTarget.getCategory(), label).size() > 0 && fTarget.getCategory().getLabeledPartitions(label).size() == 1){
				removeMentioningConstraints = true;
				break;
			}
		}
		if(removeMentioningConstraints){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_REMOVE_LABELS_WARNING_TITLE, 
					Messages.DIALOG_REMOVE_LABELS_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return execute(new PartitionOperationRemoveLabels(fTarget, labels), source, updateListener, Messages.DIALOG_REMOVE_LABEL_PROBLEM_TITLE);
	}

	public String addNewLabel(BasicSection source, IModelUpdateListener updateListener) {
		String newLabel = Constants.DEFAULT_NEW_PARTITION_LABEL;
		int i = 1;
		while(fTarget.getLeafLabels().contains(newLabel)){
			newLabel = Constants.DEFAULT_NEW_PARTITION_LABEL + "(" + i + ")";
			i++;
		}
		if(addLabel(newLabel, source, updateListener)){
			return newLabel;
		}
		return null;
	}

	private boolean addLabel(String newLabel, BasicSection source, IModelUpdateListener updateListener) {
		IModelOperation operation = new PartitionOperationAddLabel(fTarget, newLabel);
		return execute(operation, source, updateListener, Messages.DIALOG_ADD_LABEL_PROBLEM_TITLE);
	}

	public boolean isLabelInherited(String label) {
		return fTarget.getInheritedLabels().contains(label);
	}

	public boolean renameLabel(String label, String newValue, BasicSection source, IModelUpdateListener updateListener) {
		if(label.equals(newValue)){
			return false;
		}
		if(fTarget.getInheritedLabels().contains(newValue)){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_RENAME_LABELS_ERROR_TITLE, 
					Messages.DIALOG_LABEL_IS_ALREADY_INHERITED);
				return false;
		}
		if(fTarget.getLeafLabels().contains(newValue)){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_RENAME_LABELS_WARNING_TITLE, 
					Messages.DIALOG_DESCENDING_LABELS_WILL_BE_REMOVED_WARNING_TITLE) == false){
				return false;
			}
		}
		
		IModelOperation operation = new PartitionOperationRenameLabel(fTarget, label, newValue);
		return execute(operation, source, updateListener, Messages.DIALOG_CHANGE_LABEL_PROBLEM_TITLE);
	}

}
