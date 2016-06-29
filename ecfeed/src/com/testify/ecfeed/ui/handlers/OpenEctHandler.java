/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.handlers;

import org.eclipse.core.commands.ExecutionException;

import com.testify.ecfeed.ui.dialogs.basic.FileOpenEctDialog;
import com.testify.ecfeed.utils.EclipseHelper;


public class OpenEctHandler {

	public static void execute() throws ExecutionException {
		String pathWithFileName = FileOpenEctDialog.open();
		if (pathWithFileName == null) {
			return;
		}
		EclipseHelper.openEditorOnExistingExtFile(pathWithFileName);
	}

}
