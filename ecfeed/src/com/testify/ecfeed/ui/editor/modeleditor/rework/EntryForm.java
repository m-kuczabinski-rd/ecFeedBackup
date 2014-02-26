package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class EntryForm extends AbstractFormPart {
	
	private static final int ENTRY_STYLE = SWT.NONE;
	
	Control fEntry;
	Button fButton;
	FormToolkit fToolkit;

	public EntryForm(Composite parent, FormToolkit toolkit, String label, String buttonLabel){
		fToolkit = toolkit;
		
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		if(label != null){
			toolkit.createLabel(composite, label);
		}
		fEntry = createEntry(composite, entryStyle(), entryGridData());
		fButton = toolkit.createButton(composite, buttonLabel, SWT.CENTER);
		fButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				buttonSelected(e);
			}
		});
		
		toolkit.paintBordersFor(composite);
	}
	
	protected abstract Control createEntry(Composite parent, int style, GridData gridData);
	
	protected abstract void buttonSelected(SelectionEvent e);
	
	protected int entryStyle(){
		return ENTRY_STYLE;
	}
	
	protected GridData entryGridData(){
		return new GridData(SWT.FILL, SWT.CENTER, true, false);
	}
	
	protected FormToolkit getToolkit(){
		return fToolkit;
	}
}
