package com.testify.ecfeed.ui.modelif;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.modelif.ModelOperationManager;

public interface IModelUpdateContext {
	public ModelOperationManager getOperationManager();
	public AbstractFormPart getSourceForm();
	public IModelUpdateListener getUpdateListener();
}
