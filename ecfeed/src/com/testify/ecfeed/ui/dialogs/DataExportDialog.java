package com.testify.ecfeed.ui.dialogs;

import java.awt.Label;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.ui.common.Messages;

public class DataExportDialog extends TitleAreaDialog{

	private String fPrefaceTemplate;
	private String fTestCaseTemplate;
	private String fTailTemplate;
	private Text fHeaderTemplateText;
	private Text fTestCaseTemplateText;
	private Text fTailTemplateText;
	
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
		new Label(Messages.EXPORT_TEST_DATA_HEADER_TEMPLATE_LABEL);

		fHeaderTemplateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		fHeaderTemplateText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void createTestCaseTemplateComposite(Composite parent) {
		new Label(Messages.EXPORT_TEST_DATA_TEST_CASE_TEMPLATE_LABEL);
		
		fTestCaseTemplateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		fTestCaseTemplateText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void createTemplateTailComposite(Composite parent) {
		new Label(Messages.EXPORT_TEST_DATA_TAIL_TEMPLATE_LABEL);
		
		fTailTemplateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		fTailTemplateText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void createTargetFileContainer(Composite container) {
		
	}
}
