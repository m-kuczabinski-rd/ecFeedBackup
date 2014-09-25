package com.testify.ecfeed.ui.modelif;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.modeladp.ModelOperationManager;

public interface IModelUpdateContext {
	public ModelOperationManager getOperationManager();
	public AbstractFormPart getSourceForm();
	public IModelUpdateListener getUpdateListener();
	public IUndoContext getUndoContext();
}
