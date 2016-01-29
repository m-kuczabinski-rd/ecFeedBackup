package com.testify.ecfeed.rcp3.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileOpenDialog {
	
	private FileDialog fFileDialog;
	
	public FileOpenDialog() {
		Shell shell = Display.getDefault().getActiveShell();
		fFileDialog = new FileDialog(shell, SWT.OPEN);
        fFileDialog.setText("Open");
        fFileDialog.setFilterPath("C:/");
        String[] filterExt = { "*.ect" };
        fFileDialog.setFilterExtensions(filterExt);
	}
	
	public String open() {
		return fFileDialog.open();
	}

}
