package com.testify.ecfeed.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.RootNode;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;

public class ModelSettingsDialog extends TitleAreaDialog {
	private Text fNameText;
	private String fName;
	private RootNode fModel;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ModelSettingsDialog(Shell parentShell, RootNode model) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.TITLE);
		setHelpAvailable(false);
		fModel = model;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Set new model name");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.heightHint = 113;
		container.setLayoutData(gd_container);
		
		Label lblModelName = new Label(container, SWT.NONE);
		lblModelName.setText("Model name");
		
		fNameText = new Text(container, SWT.BORDER);
		GridData gd_fNameText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_fNameText.minimumWidth = 200;
		gd_fNameText.widthHint = 332;
		fNameText.setLayoutData(gd_fNameText);
		fNameText.setText(fModel.getName());

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
	protected void okPressed(){
		saveInput();
		super.okPressed();
	}
	
	private void saveInput(){
		fName = fNameText.getText();
	}
	
	public String getName(){
		return fName;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Rename model");
		setMessage("Set model new name", IMessageProvider.NONE);
	}	

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 200);
	}

}
