package com.testify.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import com.testify.ecfeed.core.utils.DiskFileHelper;
import com.testify.ecfeed.utils.EclipseHelper;

public class FileOpenAndReadDialog {

	public static String display()  {
		FileDialog fileDialog = new FileDialog(EclipseHelper.getActiveShell(), SWT.OPEN);
		String filename = fileDialog.open();

		if (filename == null) {
			return null;
		}

		return DiskFileHelper.readStringFromFile(filename);
	}

}
