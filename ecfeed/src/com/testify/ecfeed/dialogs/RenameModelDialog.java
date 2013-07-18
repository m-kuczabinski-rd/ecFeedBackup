package com.testify.ecfeed.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.RootNode;

public class RenameModelDialog extends TitleAreaDialog {
	private Text fNameText;
	private String fNewName;
	private RootNode fModel;

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
		setTitle(DialogStrings.DIALOG_RENAME_MODEL_TITLE);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Text instructionText = new Text(container, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		instructionText.setText(DialogStrings.DIALOG_RENAME_MODEL_MESSAGE);
		instructionText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		instructionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		fNameText = new Text(container, SWT.BORDER);
		fNameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		fNameText.setText(fModel.getName());
		fNameText.setFocus();
		fNameText.setMessage(DialogStrings.DIALOG_RENAME_MODEL_MESSAGE);
		fNameText.setSelection(0,fModel.getName().length());

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
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
