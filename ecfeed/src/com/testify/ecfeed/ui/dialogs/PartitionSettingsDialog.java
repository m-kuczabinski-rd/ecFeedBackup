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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

import org.eclipse.wb.swt.SWTResourceManager;

public class PartitionSettingsDialog extends TitleAreaDialog {
	private CategoryNode fCategory;
	private PartitionNode fPartition;
	private Text fNameText;
	private Text fValueText;
	private String fPartitionName;
	private Object fPartitionValue;
	private Button fOkButton;
	private Composite fMainContainer;
	private Text fErrorMessage;

	/**
	 * Create dialog
	 * @param parentShell parent shell
	 * @param parent Category parent for configured partition
	 * @param partition Configured partition, if null, new partition will be created
	 */
	public PartitionSettingsDialog(Shell parentShell, CategoryNode parent, PartitionNode partition) {
		super(parentShell);
		fCategory = parent;
		fPartition = partition;
		setHelpAvailable(false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.DIALOG_PARTITION_SETTINGS_DIALOG_TITLE);
		setMessage(Messages.DIALOG_PARTITION_SETTINGS_DIALOG_MESSAGE);
		Composite area = (Composite) super.createDialogArea(parent);
		fMainContainer = new Composite(area, SWT.NONE);
		fMainContainer.setLayout(new GridLayout(2, false));
		fMainContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblPartitionName = new Label(fMainContainer, SWT.NONE);
		lblPartitionName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPartitionName.setText("Partition name");
		
		fNameText = new Text(fMainContainer, SWT.BORDER);
		fNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				verifyInput();
			}
		});
		
		Label lblPartitionValue = new Label(fMainContainer, SWT.NONE);
		lblPartitionValue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPartitionValue.setText("Partition value");
		
		fValueText = new Text(fMainContainer, SWT.BORDER);
		fValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label separator = new Label(fMainContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		fErrorMessage = new Text(fMainContainer, SWT.READ_ONLY | SWT.WRAP);
		fErrorMessage.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridData errorMessageGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2);
		errorMessageGridData.heightHint = 100;
		fErrorMessage.setLayoutData(errorMessageGridData);
		fErrorMessage.setText(Messages.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE + 
				"\n\n" + Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE);
		fValueText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				verifyInput();
			}
		});

		if(fPartition != null){
			fNameText.setText(fPartition.getName());
			fValueText.setText(fPartition.getValueString());
		}

		return area;
	}

	private void verifyInput(){
		boolean inputValid = true;
		String errorTitle = "";
		String errorMessage = "";
		if(!fCategory.validatePartitionStringValue(fValueText.getText())){
			inputValid = false;
			errorTitle = Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE;
			errorMessage = Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE;
		}
		if(!fCategory.validatePartitionName(fNameText.getText())){
			inputValid = false;
			errorTitle = Messages.DIALOG_PARTITION_NAME_PROBLEM_TITLE;
			errorMessage = Messages.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE;
		}

		if(inputValid){
			fOkButton.setEnabled(true);
			fErrorMessage.setText("");
		}
		else{
			fOkButton.setEnabled(false);
			fErrorMessage.setText(errorTitle + "\n\n" + errorMessage);
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
		fOkButton.setEnabled(false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(484, 350);
	}

	@Override
	protected void okPressed() {
		fPartitionName = fNameText.getText();
		fPartitionValue = fCategory.getPartitionValueFromString(fValueText.getText());
		super.okPressed();
	}

	public String getPartitionName() {
		return fPartitionName;
	}

	public Object getPartitionValue() {
		return fPartitionValue;
	}

}