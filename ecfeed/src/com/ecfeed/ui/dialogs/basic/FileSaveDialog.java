/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.utils.EclipseHelper;

public class FileSaveDialog {

	public enum Result {
		SAVED,
		CANCELLED,
	}	

	public static Result open(String title, String text, String[] fileExtensions) {
		FileDialog fileDialog = new FileDialog(EclipseHelper.getActiveShell(), SWT.SAVE);

		fileDialog.setText(title);
		fileDialog.setFilterExtensions(fileExtensions);

		String fileName = fileDialog.open();

		if (fileName == null) {
			return Result.CANCELLED;
		}

		if (DiskFileHelper.fileExists(fileName) &&
				ReplaceExistingFileDialog.open(fileName) == ReplaceExistingFileDialog.Result.NO) {
			return Result.CANCELLED;
		}

		DiskFileHelper.saveStringToFile(fileName, text);
		return Result.SAVED;
	}

}
