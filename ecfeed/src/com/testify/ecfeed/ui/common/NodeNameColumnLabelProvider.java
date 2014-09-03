package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.model.GenericNode;

public class NodeNameColumnLabelProvider extends NodeViewerColumnLabelProvider {

	private final String EMPTY_STRING = "";
	
	@Override
	public String getText(Object element) {
		if(element instanceof GenericNode){
			return ((GenericNode)element).getName();
		}
		return EMPTY_STRING;
	}

}
