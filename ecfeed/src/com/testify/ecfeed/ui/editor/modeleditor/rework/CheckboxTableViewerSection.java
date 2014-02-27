package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class CheckboxTableViewerSection extends TableViewerSection {

	public CheckboxTableViewerSection(BasicDetailsPage parent, FormToolkit toolkit, int style) {
		super(parent, toolkit, style);
	}

	@Override
	protected Table createTable(Composite parent, int style){
		return new Table(parent, style | SWT.CHECK);
	}
	
	@Override
	protected StructuredViewer createViewer(Composite parent, int style){
		Table table = createTable(parent, style);
		table.setLayoutData(viewerLayoutData());
		return new CheckboxTableViewer(table);
	}
	
	protected CheckboxTableViewer getCheckboxViewer(){
		return (CheckboxTableViewer)getViewer();
	}
}
