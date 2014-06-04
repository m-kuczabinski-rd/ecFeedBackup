package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.ui.editor.NodeClipboard;

public class MenuCopyOperation extends MenuOperation{
	private NodeClipboard fSource;
	private IGenericNode fTarget;

	@Override
	public void execute(){
		fSource.setClipboardNode(fTarget);
	}

	@Override
	public boolean isEnabled(){
		return (fTarget != null);
	}
	
	public MenuCopyOperation(IGenericNode target, NodeClipboard source){
		super("Copy");
		fTarget = target;
		fSource = source;
	}

}