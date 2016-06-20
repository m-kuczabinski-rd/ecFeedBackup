/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.core.utils.DiskFileHelper;

public class SaveAsEctDialogWithConfirm {

	public static String open(String filterPath, String originalPathWithFileName, Shell shell) {
		for(;;) {
			String newFile = openOnce(filterPath, originalPathWithFileName, shell);

			if (newFile != null) {
				return newFile;
			}

			YesNoDialog.Result result = YesNoDialog.open("Do you want to cancel saving the file: " + originalPathWithFileName + "?", shell);

			if (result == YesNoDialog.Result.YES) {
				return null;
			}
		}		
	}

	public static String openOnce(String filterPath, String originalFileName, Shell shell) {
		String newFile = SaveAsEctDialog.open(filterPath, originalFileName, shell);
		if (newFile == null) {
			return null;
		}
		if (!writeAllowed(newFile, shell)) {
			return null; 
		}

		return newFile;		
	}

	private static boolean writeAllowed(String pathWithFileName, Shell shell) {
		if (!DiskFileHelper.fileExists(pathWithFileName)) {
			return true;
		}
		ReplaceExistingFileDialog.Result result = ReplaceExistingFileDialog.open(pathWithFileName, shell);
		if (result == ReplaceExistingFileDialog.Result.YES) {
			return true;
		}
		return false;
	}
}
