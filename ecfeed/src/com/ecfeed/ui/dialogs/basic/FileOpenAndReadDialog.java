/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.utils.EclipseHelper;

public class FileOpenAndReadDialog {

	public static String open(String title, String[] fileExtensions)  {
		FileDialog fileDialog = new FileDialog(EclipseHelper.getActiveShell(), SWT.OPEN);
		
		fileDialog.setText(title);
		fileDialog.setFilterExtensions(fileExtensions);
		String filename = fileDialog.open();

		if (filename == null) {
			return null;
		}

		return DiskFileHelper.readStringFromFile(filename);
	}

}
