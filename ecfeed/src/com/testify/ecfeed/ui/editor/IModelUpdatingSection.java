package com.testify.ecfeed.ui.editor;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public interface IModelUpdatingSection extends IModelUpdateContext {
	public AbstractFormPart getSourceForm();
}
