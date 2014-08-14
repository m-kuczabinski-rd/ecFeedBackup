package com.testify.ecfeed.ui.common;

import org.eclipse.jface.viewers.LabelProvider;

import com.testify.ecfeed.model.GenericNode;

public class GenericNodeNameLabelProvidee extends LabelProvider {

	@Override
	public String getText(Object element){
		if(element instanceof GenericNode){
			return ((GenericNode)element).getName();
		}
		return null;
	}
}
