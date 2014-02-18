package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class CheckboxTableViewerSection extends TableViewerSection {

	public CheckboxTableViewerSection(Composite parent, FormToolkit toolkit,
			int style, int buttonsPosition) {
		super(parent, toolkit, style, buttonsPosition);
	}

	@Override
	protected Table createTable(Composite parent, int style){
		return new Table(parent, style | SWT.CHECK);
	}
}
