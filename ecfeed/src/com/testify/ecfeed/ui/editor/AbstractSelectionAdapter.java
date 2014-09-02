package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public abstract class AbstractSelectionAdapter extends SelectionAdapter {

	@Override
	public abstract void widgetSelected(SelectionEvent e);
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e){
		widgetSelected(e);
	}
}
