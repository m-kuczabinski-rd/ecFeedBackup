/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.handlers;

import org.eclipse.core.commands.ExecutionException;

import com.ecfeed.core.utils.DiskFileHelper;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.testify.ecfeed.ui.dialogs.basic.NewFileEctDialog;
import com.testify.ecfeed.ui.dialogs.basic.ReplaceExistingFileDialog;
import com.testify.ecfeed.utils.EclipseHelper;
import com.testify.ecfeed.utils.EctFileHelper;


public class NewEctHandler {

	public static void execute() throws ExecutionException {
		String pathWithFileName = NewFileEctDialog.open();
		if (pathWithFileName == null) {
			return;
		}
		if (!preparePlaceForNewFile(pathWithFileName)) {
			return;
		}

		try {
			EctFileHelper.createNewFile(pathWithFileName);
			EclipseHelper.openEditorOnExistingExtFile(pathWithFileName);
		} catch (Exception e) {
			ExceptionCatchDialog.open("Can not open editor.", e.getMessage());
		}

		return;
	}

	private static boolean preparePlaceForNewFile(String pathWithFileName) {
		if (!DiskFileHelper.fileExists(pathWithFileName)) {
			return true;
		}
		if (ReplaceExistingFileDialog.open(pathWithFileName) == ReplaceExistingFileDialog.Result.YES) {
			DiskFileHelper.deleteFile(pathWithFileName);
			return true;
		}
		return false;
	}

}
