/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import com.testify.ecfeed.utils.EclipseHelper;

public class SaveAsEctDialog {

	public static String open(String filterPath, String originalFileName) {
		FileDialog fFileDialog = new FileDialog(EclipseHelper.getActiveShell(), SWT.SAVE);
		fFileDialog.setFilterNames(new String[] { "Ect Files", "All Files (*.*)" });
		fFileDialog.setFilterExtensions(new String[] { "*.ect", "*.*" }); 
		fFileDialog.setFilterPath(filterPath);
		fFileDialog.setFileName(originalFileName);
		return fFileDialog.open();
	}

}