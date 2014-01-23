/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.model.RootNode;

public class RenameModelDialog extends TitleAreaDialog {
	private Text fNameText;
	private String fNewName;
	private RootNode fModel;
	private Button fOkButton;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RenameModelDialog(Shell parentShell, RootNode model) {
		super(parentShell);
		fModel = model;
		setHelpAvailable(false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.DIALOG_RENAME_MODEL_TITLE);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Text instructionText = new Text(container, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		instructionText.setText(Messages.DIALOG_RENAME_MODEL_MESSAGE);
		instructionText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		instructionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		fNameText = new Text(container, SWT.BORDER);
		fNameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		fNameText.setText(fModel.getName());
		fNameText.setFocus();
		fNameText.setMessage(Messages.DIALOG_RENAME_MODEL_MESSAGE);
		fNameText.setSelection(0,fModel.getName().length());
		fNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				verifyName();
			}
		});

		return area;
	}

	private void verifyName() {
		if (RootNode.validateModelName(fNameText.getText()) == false){
			fOkButton.setEnabled(false);
		}
		else{
			fOkButton.setEnabled(true);
		}
		
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		fOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	public void okPressed(){
		fNewName = fNameText.getText();
		super.okPressed();
	}
	
	public String getNewName(){
		return fNewName;
	}
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}