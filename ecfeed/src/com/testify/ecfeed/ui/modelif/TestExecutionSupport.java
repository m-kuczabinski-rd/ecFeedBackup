/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.plugin.Activator;

public abstract class TestExecutionSupport {

	private List<Status> fUnsuccesfullExecutionStatuses;

	public TestExecutionSupport(){
		fUnsuccesfullExecutionStatuses = new ArrayList<>();
	}

	protected void addFailedTest(RunnerException e){
		fUnsuccesfullExecutionStatuses.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
	}

	protected void clearFailedTests(){
		fUnsuccesfullExecutionStatuses.clear();
	}
	
	protected void displayTestStatusDialog(int executedTestCases){
		if(fUnsuccesfullExecutionStatuses.size() > 0){
			String msg = Messages.DIALOG_UNSUCCESFUL_TEST_EXECUTION(executedTestCases, fUnsuccesfullExecutionStatuses.size());
			MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, fUnsuccesfullExecutionStatuses.toArray(new Status[]{}), "Open details to see more", new RunnerException("Problematic test cases"));
			ErrorDialog.openError(null, Messages.DIALOG_TEST_EXECUTION_REPORT_TITLE, msg, ms);
		}
		else{
			String msg = Messages.DIALOG_SUCCESFUL_TEST_EXECUTION(executedTestCases);
			MessageDialog.openInformation(null, Messages.DIALOG_TEST_EXECUTION_REPORT_TITLE, msg);
		}
	}
}
