/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.rcp3.handlers;

import java.io.File;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.testify.ecfeed.rcp3.dialogs.FileOpenDialog;


public class OpenHandler extends org.eclipse.core.commands.AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		File file = selectFile();
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		try {
			IDE.openEditorOnFileStore(page, fileStore);
		} catch (PartInitException e) {
			throw new ExecutionException(e.getMessage());
		}

		return null;
	}

	private File selectFile() {
		FileOpenDialog dialog = new FileOpenDialog();
		String path = dialog.open();
		File file = new File(path);

		if (!file.exists()) {
			return null;
		}
		if (!file.isFile()) {
			return null;
		}
		return file;
	}

}
