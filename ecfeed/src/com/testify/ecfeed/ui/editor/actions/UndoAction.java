package com.testify.ecfeed.ui.editor.actions;

import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class UndoAction extends ModelModyfyingAction {
	public UndoAction(IModelUpdateContext updateContext) {
		super(null, updateContext);
	}

	@Override
	public boolean isEnabled(){
		return getUpdateContext().getOperationManager().undoEnabled();
	}
	
	@Override
	public void run(){
		try {
			getUpdateContext().getOperationManager().undo();
			getUpdateContext().getUpdateListener().modelUpdated(getUpdateContext().getSourceForm());
		} catch (ModelIfException e) {}
	}
}
