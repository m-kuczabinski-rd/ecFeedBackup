package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class TextForm extends EntryForm {

	Text fText;
	
	public TextForm(Composite parent, FormToolkit toolkit, String label,
			String buttonLabel) {
		super(parent, toolkit, label, buttonLabel);
	}

	@Override
	protected Control createEntry(Composite parent, int style, GridData gridData) {
		return createText(parent, style, gridData);
	}
	
	protected Text createText(Composite parent, int style, GridData gridData) {
		fText = getToolkit().createText(parent, null);
		fText.setLayoutData(gridData);
		fText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					newText(fText.getText());
				}
			}
		});
		return fText;
	}

	protected void newText(String text){
	}

	protected void buttonSelected(SelectionEvent e){
		newText(fText.getText());
	}
	
	public void setText(String text){
		fText.setText(text);
	}
	
	public String getText(){
		return fText.getText();
	}

}
