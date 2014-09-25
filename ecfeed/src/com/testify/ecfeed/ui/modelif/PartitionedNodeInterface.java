package com.testify.ecfeed.ui.modelif;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.operations.GenericOperationAddPartition;
import com.testify.ecfeed.modeladp.operations.GenericOperationRemovePartition;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.Messages;

public class PartitionedNodeInterface extends GenericNodeInterface {

	private PartitionedNode fTarget;
	
	public PartitionedNodeInterface(IModelUpdateContext updateContext){
		super(updateContext);
	}
	
	public void setTarget(PartitionedNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	public PartitionNode addNewPartition() {
		String name = generatePartitionName();
		String value = generateNewPartitionValue();
		PartitionNode newPartition = new PartitionNode(name, value);
		if(addPartition(newPartition)){
			return newPartition;
		}
		return null;
	}
	
	public boolean addPartition(PartitionNode newPartition) {
		IModelOperation operation = new GenericOperationAddPartition(fTarget, newPartition, fTarget.getPartitions().size(), true); 
		return execute(operation, Messages.DIALOG_ADD_PARTITION_PROBLEM_TITLE);
	}
	
	public boolean removePartition(PartitionNode partition) {
		IModelOperation operation = new GenericOperationRemovePartition(fTarget, partition, true);
		return execute(operation, Messages.DIALOG_REMOVE_PARTITION_TITLE);
	}

	public boolean removePartitions(Collection<PartitionNode> partitions) {
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
		return removeChildren(partitions, Messages.DIALOG_REMOVE_PARTITIONS_PROBLEM_TITLE);
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
