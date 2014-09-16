package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class DeleteAction extends ModelModyfyingAction {

	public DeleteAction(ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(GlobalActions.DELETE.getId(), GlobalActions.DELETE.getName(), selectionProvider, updateContext);
	}
	
	@Override
	public boolean isEnabled(){
		return getSelectionInterface().deleteEnabled();
	}

	@Override
	public void run(){
		getSelectionInterface().delete(getUpdateContext());
	}
	
}
