package com.testify.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import com.testify.ecfeed.core.utils.DiskFileHelper;
import com.testify.ecfeed.utils.EclipseHelper;

public class FileSaveDialog {

	public static void display(String text) {
		FileDialog fileDialog = new FileDialog(EclipseHelper.getActiveShell(), SWT.SAVE);
		String fileName = fileDialog.open();

		if (fileName == null) {
			return;
		}

		if (DiskFileHelper.fileExists(fileName) &&
				ReplaceExistingFileDialog.open(fileName) == ReplaceExistingFileDialog.Result.NO) {

			return;
		}

		DiskFileHelper.saveStringToFile(fileName, text);
	}

}
