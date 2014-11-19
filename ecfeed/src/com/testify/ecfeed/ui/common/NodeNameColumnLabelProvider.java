package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.model.AbstractNode;

public class NodeNameColumnLabelProvider extends NodeViewerColumnLabelProvider {

	public NodeNameColumnLabelProvider() {
		super();
	}

	private final String EMPTY_STRING = "";
	
	@Override
	public String getText(Object element) {
		if(element instanceof AbstractNode){
			return ((AbstractNode)element).getName();
		}
		return EMPTY_STRING;
	}

}
