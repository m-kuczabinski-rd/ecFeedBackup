package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.ui.modelif.NodeClipboard;

public class CopyAction extends ModelSelectionAction {
	
	public CopyAction(ISelectionProvider selectionProvider){
		super(GlobalActions.COPY.getId(), GlobalActions.COPY.getName(), selectionProvider);
	}
	
	@Override
	public void run() {
		NodeClipboard.setContent(getSelectedNodes());
	}
	
	@Override
	public boolean isEnabled(){
		return getSelectedNodes().size() > 0 && isSelectionSingleType();
	}
}
