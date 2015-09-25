package com.testify.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

public class DataExportDialog extends TitleAreaDialog{

	private String fPrefaceTemplate;
	private String fTestTemplate;
	private String fTailTemplate;
	
	public DataExportDialog(Shell parentShell) {
		super(parentShell);
	}

}
