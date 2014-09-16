package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelModyfyingAction extends ModelSelectionAction {

	private IModelUpdateContext fUPdateContext;

	public ModelModyfyingAction(String id, String name, ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(id, name, selectionProvider);
		fUPdateContext = updateContext;
	}

	protected IModelUpdateContext getUpdateContext(){
		return fUPdateContext;
	}
}
