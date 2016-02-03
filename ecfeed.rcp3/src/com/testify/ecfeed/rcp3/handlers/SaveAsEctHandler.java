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
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.testify.ecfeed.core.utils.DiskFileHelper;
import com.testify.ecfeed.core.utils.UriHelper;
import com.testify.ecfeed.ui.dialogs.basic.ReplaceExistingFileDialog;
import com.testify.ecfeed.ui.dialogs.basic.SaveAsEctDialog;
import com.testify.ecfeed.ui.editor.ModelEditor;
import com.testify.ecfeed.utils.EclipseHelper;


public class SaveAsEctHandler extends org.eclipse.core.commands.AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ModelEditor modelEditor = EclipseHelper.getActiveModelEditor();
		if (modelEditor == null) {
			return null;
		}

		FileStoreEditorInput editorInput = EclipseHelper.getFileStoreEditorInput(modelEditor);
		if (editorInput == null) {
			return null;
		}

		String pathWithFileName = UriHelper.convertUriToFilePath(editorInput.getURI());
		executeSaveAs(pathWithFileName, modelEditor);
		return null;
	}

	private void executeSaveAs(String pathWithFileName, ModelEditor modelEditor) {
		String fileName = DiskFileHelper.extractFileName(pathWithFileName);
		String path = DiskFileHelper.extractPath(pathWithFileName);

		String newFile = SaveAsEctDialog.open(path, fileName);
		if (newFile == null) {
			return;
		}
		if (!writeAllowed(newFile)) {
			return; 
		}

		modelEditor.saveModelToFile(newFile);
		modelEditor.setEditorFile(newFile);
	}

	private boolean writeAllowed(String pathWithFileName) {
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
