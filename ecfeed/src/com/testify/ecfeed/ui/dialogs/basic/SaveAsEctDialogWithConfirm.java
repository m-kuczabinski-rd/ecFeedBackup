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
import com.testify.ecfeed.utils.EclipseHelper;

public class SaveAsEctDialogWithConfirm {

	public static String open(String filterPath, String originalFileName) {
		return open(filterPath, originalFileName, EclipseHelper.getActiveShell());
	}
	
	public static String open(String filterPath, String originalFileName, Shell shell) {
		String newFile = SaveAsEctDialog.open(filterPath, originalFileName, shell);
		if (newFile == null) {
			return null;
		}
		if (!writeAllowed(newFile)) {
			return null; 
		}

		return newFile;		
	}

	private static boolean writeAllowed(String pathWithFileName) {
		if (!DiskFileHelper.fileExists(pathWithFileName)) {
			return true;
		}
		ReplaceExistingFileDialog.Result result = ReplaceExistingFileDialog.open(pathWithFileName);
		if (result == ReplaceExistingFileDialog.Result.YES) {
			return true;
		}
		return false;
	}
}
