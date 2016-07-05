/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.ecfeed.ui.common.Messages;

public class TestClassImportDialog extends TestClassSelectionDialog {

	private Button fTestOnlyButton;
	private boolean fTestOnly;

	public TestClassImportDialog(Shell parent) {
		super(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent){
		Composite composite = (Composite)super.createDialogArea(parent);
		Composite testOnlyComposite = new Composite(parent, SWT.NONE);
		testOnlyComposite.setLayout(new GridLayout(2, false));
		fTestOnlyButton = new Button(testOnlyComposite, SWT.CHECK);
		fTestOnlyButton.setText("Import only methods annotated with @Test");
		fTestOnlyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				fTestOnly = fTestOnlyButton.getSelection();
			}
		});
		return composite;
	}

	public boolean getTestOnlyFlag(){
		return fTestOnly;
	}

	@Override
	protected String getDialogMessage(){
		return Messages.DIALOG_IMPORT_TEST_CLASS_SELECTION_MESSAGE;
	}
}
