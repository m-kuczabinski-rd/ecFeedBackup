package com.testify.ecfeed.dialogs;

import java.util.Set;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.constants.Strings;

public class RenameTestSuiteDialog extends TitleAreaDialog {
	private Text fNewNameText;
	private String[] fTestSuites;
	private String fRenamedTestSuite;
	private String fNewName;
	private Combo fRenamedTestSuiteCombo;
	private Button fOkButton;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RenameTestSuiteDialog(Shell parentShell, Set<String> testSuites) {
		super(parentShell);
		fTestSuites = testSuites.toArray(new String[]{});
		setHelpAvailable(false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Rename test suite");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblSelectTestSuite = new Label(container, SWT.NONE);
		lblSelectTestSuite.setText("Select test suite to rename");
		
		fRenamedTestSuiteCombo = new Combo(container, SWT.READ_ONLY);
		fRenamedTestSuiteCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fRenamedTestSuiteCombo.setItems(fTestSuites);
		if(fTestSuites.length > 0){
			fRenamedTestSuiteCombo.select(0);
		}
		
		Label lblEnterNewName = new Label(container, SWT.NONE);
		lblEnterNewName.setText("Enter new name for this test suite");
		
		fNewNameText = new Text(container, SWT.BORDER);
		fNewNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fNewNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				verifyInput();
			}
		});

		return area;
	}

	private void verifyInput() {
		if (fNewNameText.getText().length() == 0 || fNewNameText.getText().length() > 64){
			setErrorMessage(Strings.DIALOG_TEST_SUITE_NAME_ERROR_MESSAGE);
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
		fOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	public void okPressed(){
		fRenamedTestSuite = fRenamedTestSuiteCombo.getText();
		fNewName = fNewNameText.getText();
		super.okPressed();
	}
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	public String getRenamedTestSuite(){
		return fRenamedTestSuite;
	}
	
	public String getNewName(){
		return fNewName;
	}
}
