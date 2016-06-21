package com.testify.ecfeed.ui.handlers;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.testify.ecfeed.core.utils.DiskFileHelper;
import com.testify.ecfeed.core.utils.UriHelper;
import com.testify.ecfeed.ui.dialogs.basic.SaveAsEctDialogWithConfirm;
import com.testify.ecfeed.ui.editor.ModelEditor;
import com.testify.ecfeed.ui.editor.ModelEditorHelper;
import com.testify.ecfeed.utils.EclipseHelper;

public class SaveAsEctHandler {

	public static void execute() throws ExecutionException {
		ModelEditor modelEditor = ModelEditorHelper.getActiveModelEditor();
		if (modelEditor == null) {
			return;
		}

		FileStoreEditorInput editorInput = ModelEditorHelper.getFileStoreEditorInput(modelEditor);
		if (editorInput == null) {
			return;
		}

		String pathWithFileName = UriHelper.convertUriToFilePath(editorInput.getURI());
		if (pathWithFileName == null) {
			return;
		}

		executeSaveAs(pathWithFileName, modelEditor);
		return;
	}

	private static void executeSaveAs(String pathWithFileName, ModelEditor modelEditor) {
		String fileName = DiskFileHelper.extractFileName(pathWithFileName);
		String path = DiskFileHelper.extractPath(pathWithFileName);

		String newFile = SaveAsEctDialogWithConfirm.open(path, fileName, EclipseHelper.getActiveShell());

		if (newFile == null) {
			return;
		}

		modelEditor.saveModelToFile(newFile); 
		modelEditor.setEditorFile(newFile);
	}


}
