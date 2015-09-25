package com.testify.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.ui.common.Messages;

public class DataExportDialog extends TitleAreaDialog{

	private String fHeaderTemplate;
	private String fTestCaseTemplate;
	private String fTailTemplate;
	private Text fHeaderTemplateText;
	private Text fTestCaseTemplateText;
	private Text fTailTemplateText;
	private Text fTargetFileText;
	private String fTargetFile;
	
	public DataExportDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.DIALOG_EXPORT_TEST_DATA_TITLE);
		setMessage(Messages.DIALOG_EXPORT_TEST_DATA_MESSAGE);

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTemplateDefinitionContainer(container);
		createTargetFileContainer(container);
		
		return area;

	}

	private void createTemplateDefinitionContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createTemplateHeaderComposite(parent);
		createTestCaseTemplateComposite(parent);
		createTemplateTailComposite(parent);
	}

	private void createTemplateHeaderComposite(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.EXPORT_TEST_DATA_HEADER_TEMPLATE_LABEL);

		fHeaderTemplateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 150;
		fHeaderTemplateText.setLayoutData(gd);

	}

	private void createTestCaseTemplateComposite(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.EXPORT_TEST_DATA_TEST_CASE_TEMPLATE_LABEL);
		
		fTestCaseTemplateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 150;
		fTestCaseTemplateText.setLayoutData(gd);
	}

	private void createTemplateTailComposite(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.EXPORT_TEST_DATA_TAIL_TEMPLATE_LABEL);
		
		fTailTemplateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 150;
		fTailTemplateText.setLayoutData(gd);
	}

	private void createTargetFileContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label targetFileLabel = new Label(parent, SWT.NONE); 
		targetFileLabel.setText(Messages.EXPORT_TEST_DATA_TARGET_FILE_LABEL);

		Composite targetFileContainer = new Composite(parent, SWT.NONE);
		targetFileContainer.setLayout(new GridLayout(2, false));
		targetFileContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		fTargetFileText = new Text(targetFileContainer, SWT.BORDER);
		fTargetFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		;
		
		Button browseButton = new Button(targetFileContainer, SWT.NONE);
		browseButton.setText("Browse...");
	}
	
	@Override
	protected void okPressed(){
		fHeaderTemplate = fHeaderTemplateText.getText();
		fTestCaseTemplate = fTestCaseTemplateText.getText();
		fTailTemplate = fTailTemplateText.getText();
		fTargetFile = fTargetFileText.getText();
		super.okPressed();
	}

	public String getHeaderTemplate(){
		return fHeaderTemplate;
	}

	public String getTestCaseTemplate(){
		return fTestCaseTemplate;
	}

	public String getTailTemplate(){
		return fTailTemplate;
	}

	public String getTargetFile(){
		return fTargetFile;
	}

}
