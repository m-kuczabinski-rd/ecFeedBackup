package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IImplementationStatusResolver;

public class NodeNameColumnLabelProvider extends NodeViewerColumnLabelProvider {

	public NodeNameColumnLabelProvider(IImplementationStatusResolver statusResolver) {
		super(statusResolver);
	}

	private final String EMPTY_STRING = "";
	
	@Override
	public String getText(Object element) {
		if(element instanceof GenericNode){
			return ((GenericNode)element).getName();
		}
		return EMPTY_STRING;
	}

}
