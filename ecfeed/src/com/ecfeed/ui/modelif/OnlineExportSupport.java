/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.modelif;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.runner.ITestMethodInvoker;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.SetupDialogExportOnline;
import com.ecfeed.ui.dialogs.SetupDialogOnline;

public class OnlineExportSupport extends AbstractOnlineSupport {

	String fTargetFile; 

	public OnlineExportSupport(
			MethodNode methodNode, ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, String initialExportTemplate, String targetFile) {
		super(methodNode, testMethodInvoker, fileInfoProvider, true, initialExportTemplate);

		fTargetFile = targetFile;
	}

	@Override
	protected void setRunnerTarget(MethodNode target) throws RunnerException {
		getRunner().setTargetForExport(target);
	}

	@Override
	protected SetupDialogOnline createSetupDialog(Shell activeShell,
			MethodNode methodNode, IFileInfoProvider fileInfoProvider,
			String initialExportTemplate) {
		return new SetupDialogExportOnline(activeShell, methodNode,
				fileInfoProvider, initialExportTemplate, fTargetFile);
	}

	@Override
	protected void prepareRun() throws InvocationTargetException {
	}

	@Override
	public Result run() {
		if (getTargetMethod().getParametersCount() == 0) {
			return Result.CANCELED;
		}

		return displayParametersDialogAndRunTests();
	}

	@Override
	protected void processTestCase(List<ChoiceNode> testData)
			throws RunnerException {
		getRunner().prepareTestCaseForExport(testData);
	}

	@Override
	protected void displayRunSummary() {
	}
}
