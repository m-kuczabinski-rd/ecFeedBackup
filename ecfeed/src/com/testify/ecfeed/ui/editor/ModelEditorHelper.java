/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.FileStoreEditorInput;

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
		return getFileStoreEditorInput(editorInput);
	}	
	
	public static FileStoreEditorInput getFileStoreEditorInput(IEditorInput editorInput) {
		if (!(editorInput instanceof FileStoreEditorInput)) {
			return null;
		}
		return  (FileStoreEditorInput)editorInput;
	}	
	
}
