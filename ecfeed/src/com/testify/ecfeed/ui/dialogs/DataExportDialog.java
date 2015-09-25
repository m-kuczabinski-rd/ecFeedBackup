package com.testify.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.ui.common.Messages;

public class DataExportDialog extends TitleAreaDialog{

	private String fPrefaceTemplate;
	private String fTestTemplate;
	private String fTailTemplate;
	
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

	private void createTemplateDefinitionContainer(Composite container) {
		
	}

	private void createTargetFileContainer(Composite container) {
		
	}
}
