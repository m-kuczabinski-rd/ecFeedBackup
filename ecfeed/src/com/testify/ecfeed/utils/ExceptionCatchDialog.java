package com.testify.ecfeed.utils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.core.utils.StringHelper;
import com.testify.ecfeed.core.utils.SystemLogger;

public class ExceptionCatchDialog {

	private static final String DIALOG_TITLE = "Reported problem";

	public static void display(String whatHappened, String exceptionMessage) {
		String message;

		if (StringHelper.isNullOrEmpty(whatHappened)) {
			message = exceptionMessage;
		} else {
			message = whatHappened + "   " + exceptionMessage;
		}

		SystemLogger.logCatch(message);
		MessageDialog.openError(Display.getDefault().getActiveShell(), DIALOG_TITLE, message);		
	}
}