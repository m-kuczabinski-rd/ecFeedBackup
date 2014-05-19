/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Mariusz Strozynski (m.strozynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.ui.common.Messages;

public class EditTestItemDialog extends TitleAreaDialog {
	private Text fNewClassNameText;
	private String fNewClassName;
	private Button fOkButton;
	private String fEditorTitle;
	private String fTitle;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EditTestItemDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		if (fTitle != null) {
			super.setTitle(fTitle);
		}
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblEnterNewClassName = new Label(container, SWT.NONE);
		if (fEditorTitle != null) {
			lblEnterNewClassName.setText(fEditorTitle);
		}
		
		fNewClassNameText = new Text(container, SWT.BORDER);
		fNewClassNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		if (fNewClassName != null) {
			fNewClassNameText.setText(fNewClassName);
		}
		
		fNewClassNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				verifyInput();
			}
		});
		
		return area;
	}

	private void verifyInput() {
		if ((fNewClassNameText.getText().length() == 0) || fNewClassNameText.getText().length() > 64){
			setErrorMessage(Messages.DIALOG_TEST_CLASS_NAME_ERROR_MESSAGE);
			fOkButton.setEnabled(false);
		}
		else{
			setErrorMessage(null);
			fOkButton.setEnabled(true);
		}
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		fOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		fOkButton.setEnabled(false);
	}

	@Override
	public void okPressed(){
		fNewClassName = fNewClassNameText.getText();
		super.okPressed();
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 250);
	}
	
	public String getNewClassName(){
		return fNewClassName;
	}
	
	public void setInputText(String text) {
		fNewClassName = text;
	}
	
	public void setTitle(String text) {
		fTitle = text;
	}
	
	public void setEditorTitle(String text) {
		fEditorTitle = text;
	}
}
