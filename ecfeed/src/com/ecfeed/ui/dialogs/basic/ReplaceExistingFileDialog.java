/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.widgets.Shell;

import com.ecfeed.utils.EclipseHelper;

public class ReplaceExistingFileDialog {

	public enum Result {
		NO,
		YES,
	}

	public static Result open(String pathWithFileName) {
		return open(pathWithFileName, EclipseHelper.getActiveShell());
	}

	public static Result open(String pathWithFileName, Shell shell) {
		String question = "The file: '" + pathWithFileName + "' already exists. Do you want to replace the existing file?"; 
		YesNoDialog.Result result = YesNoDialog.open(question, shell);

		if (result == YesNoDialog.Result.YES) {
			return Result.YES;
		}
		return Result.NO;
	}
}
