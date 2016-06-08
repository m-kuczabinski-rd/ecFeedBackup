package com.testify.ecfeed.ui.dialogs.basic;

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
