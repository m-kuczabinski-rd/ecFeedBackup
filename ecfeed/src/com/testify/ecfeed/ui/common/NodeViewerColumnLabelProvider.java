package com.testify.ecfeed.ui.common;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public abstract class NodeViewerColumnLabelProvider extends ColumnLabelProvider{

//	private IImplementationStatusResolver fStatusResolver;
//
//	public NodeViewerColumnLabelProvider(){
//		fStatusResolver = new JavaImplementationStatusResolver(new EclipseLoaderProvider());
//	}
//	
//	@Override
//	public abstract String getText(Object element);
//	
//	@Override
//	public Color getForeground(Object element){
//		if(element instanceof GenericNode){
//			GenericNode node = (GenericNode)element;
//			EImplementationStatus status = fStatusResolver.getImplementationStatus(node);
//			switch(status){
//			case IMPLEMENTED: return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
//			default: return null;
//			}
//		}
//		return null;
//	}
}
