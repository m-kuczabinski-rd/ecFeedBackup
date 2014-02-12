package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.swt.events.SelectionEvent;

public class ExecuteOnlineTestAdapter extends ExecuteTestAdapter {
	MethodNodeDetailsPage fPage;

	public ExecuteOnlineTestAdapter(MethodNodeDetailsPage page) {
		super(page);
	}

	@Override
	public void widgetSelected(SelectionEvent e){
		System.out.println("dupa");
	}
}
