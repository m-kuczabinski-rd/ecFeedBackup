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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class ErrorDialog {

	private static final String DIALOG_TITLE = "Reported problem";

	public static void open(String message) {
		MessageDialog.openError(Display.getDefault().getActiveShell(), DIALOG_TITLE, message);		
	}
	
	public static void open(String title, String message) {
		MessageDialog.openError(Display.getDefault().getActiveShell(), title, message);		
	}	
}
