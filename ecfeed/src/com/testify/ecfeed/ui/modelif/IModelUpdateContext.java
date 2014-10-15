package com.testify.ecfeed.ui.modelif;

import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.adapter.ModelOperationManager;

public interface IModelUpdateContext {
	public ModelOperationManager getOperationManager();
	public AbstractFormPart getSourceForm();
	public List<IModelUpdateListener> getUpdateListeners();
	public IUndoContext getUndoContext();
}
