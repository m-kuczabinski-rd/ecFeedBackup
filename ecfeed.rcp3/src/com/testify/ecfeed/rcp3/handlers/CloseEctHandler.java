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

import com.testify.ecfeed.ui.editor.ModelEditor;
import com.testify.ecfeed.ui.editor.ModelEditorHelper;


public class CloseEctHandler extends org.eclipse.core.commands.AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ModelEditor modelEditor = ModelEditorHelper.getActiveModelEditor(); 
		if (modelEditor == null) {
			return null;
		}
		modelEditor.close(true);
		return null;
	}
}
