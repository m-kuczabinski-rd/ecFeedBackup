/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs.basic;

import org.eclipse.jface.dialogs.MessageDialog;

import com.testify.ecfeed.utils.EclipseHelper;

public class InfoDialog {

	public static void open(String message) {
		MessageDialog.openInformation(
				EclipseHelper.getActiveShell(), 
				"Information", 
				message);
	}
}
