package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class CutAction extends ModelModyfyingAction {

	private CopyAction fCopyAction;
	private DeleteAction fDeleteAction;

	public CutAction(ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(selectionProvider, updateContext);
		fCopyAction = new CopyAction(selectionProvider);
		fDeleteAction = new DeleteAction(selectionProvider, updateContext);
	}

	@Override
	public boolean isEnabled(){
		return fCopyAction.isEnabled() && fDeleteAction.isEnabled();
	}
	
	@Override
	public void run(){
		fCopyAction.run();
		fDeleteAction.run();
	}
}
