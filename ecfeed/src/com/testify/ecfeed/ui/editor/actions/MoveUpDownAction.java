package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class MoveUpDownAction extends ModelModyfyingAction {

	private boolean fUp;

	public MoveUpDownAction(boolean up, ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(selectionProvider, updateContext);
		fUp = up;
	}
	
	@Override
	public boolean isEnabled(){
		return getSelectionInterface().moveUpDownEnabed(fUp);
	}
	
	@Override 
	public void run(){
		getSelectionInterface().moveUpDown(fUp, getUpdateContext());
	}

}
