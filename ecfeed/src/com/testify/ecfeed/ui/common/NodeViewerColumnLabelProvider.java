package com.testify.ecfeed.ui.common;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.ui.modelif.GenericNodeInterface;

public abstract class NodeViewerColumnLabelProvider extends ColumnLabelProvider{

	private GenericNodeInterface fNodeIf;
	
	public NodeViewerColumnLabelProvider(){
		fNodeIf = new GenericNodeInterface();
	}
	
	@Override
	public abstract String getText(Object element);
	
	@Override
	public Color getForeground(Object element){
		if(element instanceof GenericNode){
			GenericNode node = (GenericNode)element;
			ImplementationStatus status = fNodeIf.implementationStatus(node);
			switch(status){
			case IMPLEMENTED: return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			default: return null;
			}
		}
		return null;
	}
}
