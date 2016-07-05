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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextAreaDialog extends TitleAreaDialog {

	private String fTitle;
	private String fMessage;
	private String fContent;
	private Text fTextControl;
	private String fCurrentText;

	public TextAreaDialog(Shell parentShell, String title, String message, String initialContent) {
		super(parentShell);
		fTitle = title;
		fMessage = message;
		fContent = initialContent;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(fTitle);
		setMessage(fMessage);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		fTextControl = new Text(container, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		fTextControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if(fContent != null){
			fTextControl.setText(fContent);
		}

		return area;
	}

	@Override
	protected void okPressed(){
		fCurrentText = fTextControl.getText();
		super.okPressed();
	}

	public String getText(){
		return fCurrentText;
	}

}
