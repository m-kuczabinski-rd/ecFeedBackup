package com.testify.ecfeed.ui.editor;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.abstraction.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;

public class AbstractModelUpdateContext implements IModelUpdateContext {

	private IUndoContext fUndoContext;
	private ModelOperationManager fOperationManager;

	public AbstractModelUpdateContext(ModelOperationManager operationManager, IUndoContext undoContext){
		fOperationManager = operationManager;
		fUndoContext = undoContext;
	}
	
	@Override
	public ModelOperationManager getOperationManager() {
		return fOperationManager;
	}

	@Override
	public AbstractFormPart getSourceForm() {
		return null;
	}

	@Override
	public IModelUpdateListener getUpdateListener() {
		return null;
	}

	@Override
	public IUndoContext getUndoContext() {
		return fUndoContext;
	}

}
