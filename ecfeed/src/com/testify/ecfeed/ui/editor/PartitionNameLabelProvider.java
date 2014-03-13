package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;

public class PartitionNameLabelProvider extends ColumnLabelProvider {
	private ColorManager fColorManager;

	public PartitionNameLabelProvider() {
		fColorManager = new ColorManager();
	}
	
	@Override
	public String getText(Object element){
		if(element instanceof PartitionNode){
			return ((PartitionNode)element).getName();
		}
		return "";
	}

	@Override
	public Color getForeground(Object element){
		if(element instanceof PartitionNode){
			PartitionNode partition = (PartitionNode)element;
			if(partition.isAbstract()){
				return fColorManager.getColor(ColorConstants.ABSTRACT_PARTITION);
			}
		}
		return null;
	}
}
