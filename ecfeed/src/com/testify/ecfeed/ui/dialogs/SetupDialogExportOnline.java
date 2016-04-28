/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;

public class SetupDialogExportOnline extends SetupDialogOnline {

	public SetupDialogExportOnline(Shell parentShell, MethodNode method,
			IFileInfoProvider fileInfoProvider) {
		super(parentShell, method, fileInfoProvider);
	}
	
	@Override
	protected String getDialogTitle() {
		final String DIALOG_EXECUTE_ONLINE_TITLE = "Export online";
		return DIALOG_EXECUTE_ONLINE_TITLE;
	}	
}
