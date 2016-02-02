package com.testify.ecfeed.utils;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.testify.ecfeed.ui.editor.ModelEditor;

public class EclipseHelper {
	public static IEditorPart getActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	public static ModelEditor getActiveModelEditor() {
		IEditorPart editorPart = EclipseHelper.getActiveEditor();		
		if (editorPart == null) {
			return null;
		}

		if (!(editorPart instanceof ModelEditor)) {
			return null;
		}

		return (ModelEditor)editorPart;
	}

	public static FileStoreEditorInput getFileStoreEditorInput(ModelEditor modelEditor) {
		IEditorInput editorInput = modelEditor.getEditorInput();
		return getFileStoreEditorInput(editorInput);
	}	

	public static FileStoreEditorInput getFileStoreEditorInput(IEditorInput editorInput) {
		if (!(editorInput instanceof FileStoreEditorInput)) {
			return null;
		}
		return  (FileStoreEditorInput)editorInput;
	}	

	public static FileEditorInput getFileEditorInput(IEditorInput editorInput) {
		if (!(editorInput instanceof FileEditorInput)) {
			return null;
		}
		return  (FileEditorInput)editorInput;
	}

	public static Shell getActiveShell() {
		return Display.getDefault().getActiveShell();
	}
}
