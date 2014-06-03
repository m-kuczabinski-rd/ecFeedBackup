package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.ui.editor.NodeClipboard;

public class MenuCopyOperation extends MenuOperation{
	private NodeClipboard fSource;
	private IGenericNode fTarget;

	public MenuCopyOperation(IGenericNode target, NodeClipboard source){
		super("Copy");
		fTarget = target;
		fSource = source;
	}

	@Override
	public void operate(){
		fSource.setClipboardNode(fTarget);
	}

	@Override
	public boolean isEnabled(){
		return (fTarget != null);
	}
}