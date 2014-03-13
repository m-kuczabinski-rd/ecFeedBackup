package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;

public class PartitionValueLabelProvider extends ColumnLabelProvider {

	private ColorManager fColorManager;

	public PartitionValueLabelProvider() {
		fColorManager = new ColorManager();
	}
	

	@Override
	public String getText(Object element){
		if(element instanceof PartitionNode){
			PartitionNode partition = (PartitionNode)element;
			return partition.isAbstract()?"[ABSTRACT]":partition.getValueString();
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
