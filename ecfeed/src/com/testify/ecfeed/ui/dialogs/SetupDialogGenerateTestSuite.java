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

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;

public class SetupDialogGenerateTestSuite extends SetupDialogGenerator {

	public SetupDialogGenerateTestSuite(Shell parentShell, MethodNode method,
			IFileInfoProvider fileInfoProvider) {
		super(parentShell, method, false, fileInfoProvider, null, null);
	}

	@Override
	protected String getDialogTitle() {
		final String DIALOG_GENERATE_TEST_SUITE_TITLE = "Generate test suite";
		return DIALOG_GENERATE_TEST_SUITE_TITLE;
	}

	@Override
	protected int getContent() {
		return CONSTRAINTS_COMPOSITE | CHOICES_COMPOSITE
				| TEST_SUITE_NAME_COMPOSITE | GENERATOR_SELECTION_COMPOSITE;
	}
}
