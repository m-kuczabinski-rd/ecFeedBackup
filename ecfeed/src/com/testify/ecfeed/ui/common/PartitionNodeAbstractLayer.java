package com.testify.ecfeed.ui.common;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.IPartitionedNode;
import com.testify.ecfeed.model.PartitionNode;

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
	
	public static boolean removePartitions(List<PartitionNode> partitions, IPartitionedNode parent){
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
}
