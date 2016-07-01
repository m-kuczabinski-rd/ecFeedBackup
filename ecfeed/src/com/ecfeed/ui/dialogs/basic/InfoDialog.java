/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.ui.dialogs.basic;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.ecfeed.utils.EclipseHelper;

public class InfoDialog {

	public static void open(String message, Shell shell) {
		MessageDialog.openInformation(shell, "Information", message);
	}

	public static void open(String message) {
		open(message, EclipseHelper.getActiveShell());
	}
}
