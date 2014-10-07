package com.testify.ecfeed.ui.common;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.IImplementationStatusResolver;
import com.testify.ecfeed.adapter.java.JavaImplementationStatusResolver;
import com.testify.ecfeed.model.GenericNode;

public abstract class NodeViewerColumnLabelProvider extends ColumnLabelProvider{

	private IImplementationStatusResolver fStatusResolver;

	public NodeViewerColumnLabelProvider(){
		fStatusResolver = new JavaImplementationStatusResolver(new EclipseLoaderProvider());
	}
	
	@Override
	public abstract String getText(Object element);
	
	@Override
	public Color getForeground(Object element){
		if(element instanceof GenericNode){
			GenericNode node = (GenericNode)element;
			EImplementationStatus status = fStatusResolver.getImplementationStatus(node);
			switch(status){
			case IMPLEMENTED: return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			default: return null;
			}
		}
		return null;
	}
}
