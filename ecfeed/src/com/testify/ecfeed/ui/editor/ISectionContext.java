package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public interface ISectionContext {
	public ModelMasterSection getMasterSection();
	public Composite getSectionComposite();
	public FormToolkit getToolkit();
}
