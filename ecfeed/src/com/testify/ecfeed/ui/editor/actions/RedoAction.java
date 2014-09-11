package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class RedoAction extends ModelModyfyingAction {
	private final String ERROR_MESSAGE = "Redo operation did not succeeded";
	
	public RedoAction(IModelUpdateContext updateContext) {
		super(null, updateContext);
	}

	@Override
	public boolean isEnabled(){
		return getUpdateContext().getOperationManager().redoEnabled();
	}
	
	@Override
	public void run(){
		try {
			getUpdateContext().getOperationManager().undo();
			getUpdateContext().getUpdateListener().modelUpdated(getUpdateContext().getSourceForm());
		} catch (ModelIfException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					ERROR_MESSAGE, 
					e.getMessage());
		}
	}
}
