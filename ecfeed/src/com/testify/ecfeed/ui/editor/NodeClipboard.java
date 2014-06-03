package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.IGenericNode;

public class NodeClipboard{

	private IGenericNode fClipboardNode = null;
	private IGenericNode fOriginalNode = null;

	public IGenericNode getClipboardNode(){
		return fClipboardNode;
	}

	public IGenericNode getOriginalNode(){
		return fOriginalNode;
	}

	public void setClipboardNode(IGenericNode node){
		fClipboardNode = node.getCopy();
		fOriginalNode = node;
	}

}
