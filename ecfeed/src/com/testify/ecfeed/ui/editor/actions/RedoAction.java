package com.testify.ecfeed.ui.editor.actions;

import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class RedoAction extends ModelModyfyingAction {
	public RedoAction(IModelUpdateContext updateContext) {
		super(GlobalActions.REDO.getId(), GlobalActions.REDO.getName(), null, updateContext);
	}

	@Override
	public boolean isEnabled(){
//		return getUpdateContext().getOperationManager().redoEnabled();
		return true;
	}
	
	@Override
	public void run(){
		try {
			getUpdateContext().getOperationManager().redo();
//			getUpdateContext().getUpdateListener().modelUpdated(getUpdateContext().getSourceForm());
		} catch (ModelIfException e) {}
	}
}
