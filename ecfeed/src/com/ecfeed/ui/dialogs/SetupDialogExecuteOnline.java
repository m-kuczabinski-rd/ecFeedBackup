/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;

public class SetupDialogExecuteOnline extends SetupDialogOnline {

	public SetupDialogExecuteOnline(Shell parentShell, MethodNode method,
			IFileInfoProvider fileInfoProvider, String targetFile) {
		super(parentShell, method, true, fileInfoProvider, null, targetFile);
	}

	@Override
	protected String getDialogTitle() {
		final String DIALOG_EXECUTE_ONLINE_TITLE = "Execute online test";
		return DIALOG_EXECUTE_ONLINE_TITLE;
	}

	@Override
	protected int getContent() {
		return CONSTRAINTS_COMPOSITE | CHOICES_COMPOSITE
				| GENERATOR_SELECTION_COMPOSITE;
	}

}
