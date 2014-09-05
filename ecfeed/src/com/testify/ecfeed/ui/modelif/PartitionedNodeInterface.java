package com.testify.ecfeed.ui.modelif;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.category.GenericOperationAddPartition;
import com.testify.ecfeed.modelif.java.category.GenericOperationRemovePartition;
import com.testify.ecfeed.modelif.java.common.RemoveNodesOperation;
import com.testify.ecfeed.ui.common.Constants;

public class PartitionedNodeInterface extends GenericNodeInterface {

	private PartitionedNode fTarget;
	
	public void setTarget(PartitionedNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	public PartitionedNodeInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	public PartitionNode addNewPartition(AbstractFormPart source, IModelUpdateListener updateListener) {
		String name = generatePartitionName();
		String value = generateNewPartitionValue();
		PartitionNode newPartition = new PartitionNode(name, value);
		if(addPartition(newPartition, source, updateListener)){
			return newPartition;
		}
		return null;
	}
	
	public boolean addPartition(PartitionNode newPartition, AbstractFormPart source, IModelUpdateListener updateListener) {
		IModelOperation operation = new GenericOperationAddPartition(fTarget, newPartition, fTarget.getPartitions().size()); 
		return execute(operation, source, updateListener, Messages.DIALOG_ADD_PARTITION_PROBLEM_TITLE);
	}
	
	public boolean removePartition(PartitionNode partition, AbstractFormPart source, IModelUpdateListener updateListener) {
		IModelOperation operation = new GenericOperationRemovePartition(fTarget, partition);
		return execute(operation, source, updateListener, Messages.DIALOG_REMOVE_PARTITION_TITLE);
	}

	public boolean removePartitions(Collection<PartitionNode> partitions, AbstractFormPart source, IModelUpdateListener updateListener) {
		boolean displayWarning = false;
		for(PartitionNode p : partitions){
			if(fTarget.getCategory().getMethod().mentioningConstraints(p).size() > 0 || fTarget.getCategory().getMethod().mentioningTestCases(p).size() > 0){
				displayWarning = true;
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
		EclipseModelBuilder builder = new EclipseModelBuilder();
		String type = fTarget.getCategory().getType();
		String value = builder.getDefaultExpectedValue(type);
		if(isPrimitive() == false && builder.getSpecialValues(type).size() == 0){
			int i = 0;
			while(fTarget.getLeafPartitionValues().contains(value)){
				value = builder.getDefaultExpectedValue(type) + i++; 
			}
		}
		return value;
	}

	public boolean isPrimitive() {
		return CategoryInterface.isPrimitive(fTarget.getCategory().getType());
	}
	
	public boolean isUserType() {
		return !isPrimitive();
	}

	public List<String> getSpecialValues() {
		return new EclipseModelBuilder().getSpecialValues(fTarget.getCategory().getType());
	}

	public boolean hasLimitedValuesSet() {
		return !isPrimitive() || isBoolean();
	}

	public  boolean isBoolean() {
		return CategoryInterface.isBoolean(fTarget.getCategory().getType());
	}

	protected String generatePartitionName(){
		String name = Constants.DEFAULT_NEW_PARTITION_NAME;
		int i = 0;
		while(fTarget.getPartitionNames().contains(name)){
			name = Constants.DEFAULT_NEW_PARTITION_NAME + i++; 
		}
		return name;
	}

}
