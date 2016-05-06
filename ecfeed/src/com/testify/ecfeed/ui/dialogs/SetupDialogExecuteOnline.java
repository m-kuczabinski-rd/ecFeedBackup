/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;

public class SetupDialogExecuteOnline extends SetupDialogOnline {

	public SetupDialogExecuteOnline(Shell parentShell, MethodNode method,
			IFileInfoProvider fileInfoProvider) {
		super(parentShell, method, fileInfoProvider, null);
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
