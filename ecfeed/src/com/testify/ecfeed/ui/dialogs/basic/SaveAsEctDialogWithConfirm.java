/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.widgets.Shell;

import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.utils.IChecker;

public class SaveAsEctDialogWithConfirm {

	public static String open(String filterPath, String originalPathWithFileName, IChecker additionalFileChecker, Shell shell) {
		for(;;) {
			String newFile = openOnce(filterPath, originalPathWithFileName, additionalFileChecker, shell);

			if (newFile != null) {
				return newFile;
			}

			YesNoDialog.Result result = YesNoDialog.open("Do you want to continue saving the file: " + originalPathWithFileName + "?", shell);

			if (result == YesNoDialog.Result.NO) {
				return null;
			}
		}		
	}

	public static String openOnce(String filterPath, String originalFileName, IChecker additionalFileChecker, Shell shell) {
		String newFile = SaveAsEctDialog.open(filterPath, originalFileName, shell);
		if (newFile == null) {
			return null;
		}

		if (additionalFileChecker != null) {
			if (!additionalFileChecker.check(newFile)) {
				InfoDialog.open(additionalFileChecker.getErrorMessage(newFile), shell);
				return null;
			}
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
