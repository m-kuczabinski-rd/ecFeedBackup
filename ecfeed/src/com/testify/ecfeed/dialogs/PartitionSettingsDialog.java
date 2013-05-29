package com.testify.ecfeed.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class PartitionSettingsDialog extends TitleAreaDialog {
	
	private boolean fNewPartition;
	private PartitionNode fPartition;
	private Text fPartitionNameText;
	private Text fPartitionValueText;
	private String fPartitionName;
	private Object fPartitionValue;
	private String fType;
	private Button fOkButton;
	private String fErrorMessage;


	public PartitionSettingsDialog(Shell parentShell, PartitionNode partition, String type) {
		super(parentShell);
		fPartition = partition;
		fNewPartition = (partition == null);
		fType = type;
	}

	@Override
	public void create() {
		super.create();
		setTitle(fNewPartition?"New Partition":"Edit Partition");
		setMessage("Set partition name and value", IMessageProvider.NONE);
	}	

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;

		parent.setLayoutData(gridData);

		fOkButton = createOkButton(parent, OK, "Ok", true);
		createCancelButton(parent, CANCEL, "Cancel", false);

		verifyInput();
		if(fNewPartition){
			setErrorMessage(null);
		}

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		// The text fields will grow with the size of the dialog
		Label partitionNameLabel = new Label(parent, SWT.NONE);
		partitionNameLabel.setText("Partition Name");
		createPartitionNameText(parent);

		Label partitionValueLabel = new Label(parent, SWT.NONE);
		partitionValueLabel.setText("Partition Value");
		createPartitionValueText(parent);
		
		return parent;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getPartitionName() {
		return fPartitionName;
	}

	public Object getPartitionValue() {
		return fPartitionValue;
	}

	private void createPartitionNameText(Composite parent) {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
	
		fPartitionNameText = new Text(parent, SWT.BORDER);
		fPartitionNameText.setLayoutData(gridData);
		if(!fNewPartition){
			fPartitionName = fPartition.getName();
			fPartitionNameText.setText(fPartitionName);
		}
		fPartitionNameText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				verifyInput();
			}
		});
	}

	private void createPartitionValueText(Composite parent) {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		fPartitionValueText = new Text(parent, SWT.BORDER);
		fPartitionValueText.setLayoutData(gridData);
		if(!fNewPartition){
			if(fPartition.getValue() == null){
				fPartitionValueText.setText(Constants.NULL_VALUE_STRING_REPRESENTATION);
			}
			else{
				fPartitionValueText.setText(fPartition.getValue().toString());
			}
		}
		fPartitionValueText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				verifyInput();
			}
		});
	}

	private boolean verifyInput() {
		boolean inputValid = true;
		
		inputValid &= verifyName();
		inputValid &= verifyValue();
		
		if(!inputValid){
			fOkButton.setEnabled(false);
			setErrorMessage(fErrorMessage);
		}
		else{
			fOkButton.setEnabled(true);
			setErrorMessage(null);
		}
		
		return inputValid;
	}

	private boolean verifyValue() {
		boolean inputValid = true;
		if(!CategoryNode.isStringValueValid(fPartitionValueText.getText(), fType)){
			fErrorMessage = "Invalid value";
			inputValid = false;
		}
		return inputValid;
	}

	private boolean verifyName() {
		boolean inputValid = true;
		if(fPartitionNameText.getText().length() < 1){
			fErrorMessage = "Partition name cannot be empty";
			inputValid = false;
		}
		return inputValid;
	}

	protected Button createOkButton(Composite parent, int id, 
			String label, boolean defaultButton) {
		
	    ((GridLayout) parent.getLayout()).numColumns++;
	    Button okButton = new Button(parent, SWT.PUSH);
	    okButton.setText(label);
	    okButton.setFont(JFaceResources.getDialogFont());
	    okButton.setData(new Integer(id));

		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
		});
	    if (defaultButton) {
	        Shell shell = parent.getShell();
	        if (shell != null) {
	          shell.setDefaultButton(okButton);
	        }
	      }
	    setButtonLayoutData(okButton);
	    return okButton;
	}

	protected void createCancelButton(Composite parent, int cancel,
			String string, boolean b) {

		Button cancelButton = createButton(parent, CANCEL, "Cancel", false);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}

	private void saveInput() {
		fPartitionName = fPartitionNameText.getText();
		fPartitionValue = CategoryNode.getValueFromString(fPartitionValueText.getText(), fType);
	}

}
