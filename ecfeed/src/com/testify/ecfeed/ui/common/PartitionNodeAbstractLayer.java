package com.testify.ecfeed.ui.common;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;
import com.testify.ecfeed.utils.ModelUtils;

public class PartitionNodeAbstractLayer{

	public static boolean removePartition(PartitionNode partition){
		if(partition.getCategory().getMethod().isPartitionMentioned(partition)){
			if(!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_REMOVE_PARTITIONS_TITLE,
					Messages.DIALOG_REMOVE_PARTITIONS_MESSAGE)){
				return false;
			}
		}
		partition.getParent().removePartition(partition);
		return true;
	}

	public static boolean removePartitions(List<PartitionNode> partitions, PartitionedNode parent){
		boolean warn = false;
		for(PartitionNode partition : partitions){
			if(partition.getCategory().getMethod().isPartitionMentioned(partition)){
				warn = true;
				break;
			}
		}
		if(warn){
			if(!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_REMOVE_PARTITIONS_TITLE,
					Messages.DIALOG_REMOVE_PARTITIONS_MESSAGE)){
				return false;
			}
		}
		for(PartitionNode partition : partitions){
			parent.removePartition((PartitionNode)partition);
		}
		return true;
	}

	public static boolean changePartitionValue(PartitionNode partition, String value){
		if(value.equals(partition.getValueString()) == false){
			if(ModelUtils.validatePartitionStringValue(value, partition.getCategory().getType())){
				partition.setValueString(value);
				return true;
			} else{
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE,
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE(value));
			}
		}
		return false;
	}

}
