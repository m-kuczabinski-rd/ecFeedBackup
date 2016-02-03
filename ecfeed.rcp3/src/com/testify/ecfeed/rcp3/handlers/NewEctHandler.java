/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.rcp3.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.testify.ecfeed.core.utils.DiskFileHelper;
import com.testify.ecfeed.ui.dialogs.basic.NewFileEctDialog;
import com.testify.ecfeed.ui.dialogs.basic.ReplaceExistingFileDialog;
import com.testify.ecfeed.utils.EclipseHelper;
import com.testify.ecfeed.utils.EctFileHelper;
import com.testify.ecfeed.utils.ExceptionCatchDialog;


public class NewEctHandler extends org.eclipse.core.commands.AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String pathWithFileName = NewFileEctDialog.open();
		if (pathWithFileName == null) {
			return null;
		}
		if (!preparePlaceForNewFile(pathWithFileName)) {
			return false;
		}

		try {
			EctFileHelper.createNewFile(pathWithFileName);
			EclipseHelper.openEditorOnExistingExtFile(pathWithFileName);
		} catch (Exception e) {
			ExceptionCatchDialog.display("Can not open editor.", e.getMessage());
		}

		return null;
	}

	private boolean preparePlaceForNewFile(String pathWithFileName) {
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
