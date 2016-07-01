/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.model.ModelConverter;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.serialization.IModelParser;
import com.ecfeed.core.serialization.ParserException;
import com.ecfeed.core.serialization.ect.EctParser;
import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.UriHelper;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.testify.ecfeed.ui.dialogs.basic.SaveAsEctDialogWithConfirm;
import com.testify.ecfeed.utils.EclipseHelper;

public class ModelEditorHelper {

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
		return castToFileStoreEditorInput(editorInput);
	}	

	public static FileStoreEditorInput castToFileStoreEditorInput(IEditorInput editorInput) {
		if (!(editorInput instanceof FileStoreEditorInput)) {
			return null;
		}
		return  (FileStoreEditorInput)editorInput;
	}	

	public static boolean isInMemFileInput(IEditorInput editorInput) {
		String fileName = getFileNameFromEditorInput(editorInput);

		if (fileName == null) {
			return false;
		}

		if (EditorInMemFileHelper.isInMemFile(fileName)) {
			return true;
		}

		return false;		
	}

	public static String getFileNameFromEditorInput(IEditorInput editorInput) {
		FileStoreEditorInput fileStoreInput = ModelEditorHelper.castToFileStoreEditorInput(editorInput);
		if (fileStoreInput == null) {
			return null;
		}

		return UriHelper.convertUriToFilePath(fileStoreInput.getURI());
	}

	public static InputStream getInitialInputStreamForIDE(IEditorInput input) throws ModelOperationException {
		if (input instanceof FileStoreEditorInput) {
			final String CAN_NOT_OPEN_FILE = "Can not open file: ";
			final String ERR_MSG_1 = "It is not allowed to open standalone ect files created outside of Java project structure.";
			final String ERR_MSG_2 = "Please add ect file to the Java project first."; 

			ModelOperationException.report(
					CAN_NOT_OPEN_FILE + input.getName() + ". "+ ERR_MSG_1 + " " + ERR_MSG_2);
			return null;
		}

		FileEditorInput fileInput = EclipseHelper.getFileEditorInput(input);
		if (fileInput == null) {
			reportInvalidInputTypeException();
		}

		IFile file = fileInput.getFile();
		try {
			return file.getContents();
		} catch (CoreException e) {
			displayDialogErrInputStream(e);
			return null;
		}
	}

	public static InputStream getInitialInputStreamForRCP(IEditorInput input) {
		String fileName = ModelEditorHelper.getFileNameFromEditorInput(input);

		if (fileName == null) {
			return null;
		}

		if (EditorInMemFileHelper.isInMemFile(fileName)) {
			return EditorInMemFileHelper.getInitialInputStream(fileName);
		}

		File file = new File(fileName);
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			displayDialogErrInputStream(e);
			return null;
		}
	}

	private static void displayDialogErrInputStream(Exception e) {
		ExceptionCatchDialog.open("Can not get input stream for file.", e.getMessage());
	}

	private static void reportInvalidInputTypeException() {
		ExceptionHelper.reportRuntimeException("Invalid input type.");
	}

	public static RootNode parseModel(InputStream iStream) {
		try {
			IModelParser parser = new EctParser();
			return ModelConverter.convertToCurrentVersion(parser.parseModel(iStream));

		} catch (ParserException e) {
			ExceptionCatchDialog.open("Can not parse model.", e.getMessage());
			return null;
		}
	}	

	public static String selectFileForSaveAs(IEditorInput editorInput, Shell shell) {
		if (editorInput instanceof FileEditorInput) {
			return selectFileForFileEditorInput((FileEditorInput)editorInput);
		}
		if (editorInput instanceof FileStoreEditorInput) { 
			return selectFileForFileStoreEditorInput((FileStoreEditorInput)editorInput, shell);
		}
		return null;
	}

	private static String selectFileForFileEditorInput(FileEditorInput fileEditorInput) {
		SaveAsDialog dialog = new SaveAsDialog(Display.getDefault().getActiveShell());
		IFile original = fileEditorInput.getFile();
		dialog.setOriginalFile(original);

		dialog.create();
		if (dialog.open() == Window.CANCEL) {
			return null;
		}

		IPath path = dialog.getResult();
		return path.toOSString();
	}	

	private static String selectFileForFileStoreEditorInput(FileStoreEditorInput fileStoreEditorInput, Shell shell) {

		String pathWithFileName = UriHelper.convertUriToFilePath(fileStoreEditorInput.getURI());
		String fileName = DiskFileHelper.extractFileName(pathWithFileName);
		String path = DiskFileHelper.extractPathWithSeparator(pathWithFileName);

		CanAddDocumentChecker checker = new CanAddDocumentChecker();
		return SaveAsEctDialogWithConfirm.open(path, fileName, checker, shell);
	}

	private interface IModelEditorWorker {
		public enum WorkStatus {
			OK,
			CANCEL_ALL_NEXT
		}
		WorkStatus doWork(ModelEditor modelEditor);
	}

	private static void iterateOverModelEditors(IModelEditorWorker modelEditorWorker) {
		IWorkbenchPage page = EclipseHelper.getActiveWorkBenchPage();

		IEditorReference editors[] = page.getEditorReferences();

		for (int i = 0; i < editors.length; i++) {
			IEditorPart editorPart = editors[i].getEditor(true);
			if (!(editorPart instanceof ModelEditor)) {
				continue;
			}
			ModelEditor modelEditor = (ModelEditor)editorPart;
			IModelEditorWorker.WorkStatus workStatus = modelEditorWorker.doWork(modelEditor);

			if (workStatus == IModelEditorWorker.WorkStatus.CANCEL_ALL_NEXT) {
				break;
			}
		}		
	}

	private static class NextFreeNumberFinder implements IModelEditorWorker {

		int fMaxNumber = 0;

		@Override
		public WorkStatus doWork(ModelEditor modelEditor) {
			IEditorInput editorInput = modelEditor.getEditorInput();

			int extractedNumber = extractUntitledDocNumber(editorInput.getToolTipText());
			if ( extractedNumber == 0) {
				return IModelEditorWorker.WorkStatus.OK;
			}

			fMaxNumber = Math.max(fMaxNumber, extractedNumber);
			return IModelEditorWorker.WorkStatus.OK;
		}

		private int extractUntitledDocNumber(String pathWithFileName) {
			String fileNameWithExt = DiskFileHelper.extractFileName(pathWithFileName);
			String fileName = DiskFileHelper.extractFileNameWithoutExtension(fileNameWithExt);
			String fileNumber = StringHelper.removePrefix(EditorInMemFileHelper.getFilePrefix(), fileName);
			return Integer.parseInt(fileNumber);
		}

		public int getNextFreeNumber() {
			return fMaxNumber + 1;
		}

	}

	public static int getNextFreeUntitledNumber() {
		NextFreeNumberFinder nextFreeNumberFinder = new NextFreeNumberFinder();
		iterateOverModelEditors(nextFreeNumberFinder);
		return nextFreeNumberFinder.getNextFreeNumber();
	}

	private static class FileFinder implements IModelEditorWorker {

		private String fFileToFind;
		private boolean fFound;

		FileFinder(String fileToFind) {
			fFileToFind = fileToFind;
			fFound = false;
		}

		@Override
		public WorkStatus doWork(ModelEditor modelEditor) {
			IEditorInput editorInput = modelEditor.getEditorInput();

			String fileWithPath = editorInput.getToolTipText();
			if (fileWithPath.equals(fFileToFind)) {
				fFound = true;
				return IModelEditorWorker.WorkStatus.CANCEL_ALL_NEXT;
			}

			return IModelEditorWorker.WorkStatus.OK;
		}

		public boolean isFileFound() {
			return fFound;
		}

	}

	public static boolean isFileAlreadyOpen(String pathWithFileName) {
		FileFinder fileFinder = new FileFinder(pathWithFileName);
		iterateOverModelEditors(fileFinder);
		return fileFinder.isFileFound();
	}

}
