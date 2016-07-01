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

package com.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.plugin.Activator;

public abstract class TestExecutionSupport {

	IProgressMonitor fProgressMonitor;

	int fTotalWork;
	private int fExecutedTestCases = 0;
	private List<Status> fUnsuccesfullExecutionStatuses;

	public TestExecutionSupport(){
		fUnsuccesfullExecutionStatuses = new ArrayList<>();
	}

	protected void setProgressMonitor(IProgressMonitor progressMonitor) {
		fProgressMonitor = progressMonitor;
	}

	protected void beginTestExecution(int totalWork) {
		fTotalWork = totalWork;
		fProgressMonitor.beginTask(Messages.EXECUTING_TEST_WITH_PARAMETERS, totalWork);
	}

	protected void setTestProgressMessage() {
		String message = "Total: " + fTotalWork + "  Executed: " + fExecutedTestCases + "  Failed: " + fUnsuccesfullExecutionStatuses.size();
		fProgressMonitor.subTask(message);
	}

	protected void addExecutedTest(int worked){
		fProgressMonitor.worked(worked);
		fExecutedTestCases++;
	}

	protected void addFailedTest(RunnerException e){
		fUnsuccesfullExecutionStatuses.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
	}

	public boolean anyTestFailed() {
		if (fUnsuccesfullExecutionStatuses.size() > 0 ) {
			return true;
		}
		return false;
	}

	protected void clearFailedTests(){
		fUnsuccesfullExecutionStatuses.clear();
	}

	protected void displayTestStatusDialog(){
		if(fUnsuccesfullExecutionStatuses.size() > 0){
			String msg = Messages.DIALOG_UNSUCCESFUL_TEST_EXECUTION(fExecutedTestCases, fUnsuccesfullExecutionStatuses.size());
			MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, fUnsuccesfullExecutionStatuses.toArray(new Status[]{}), "Open details to see more", new RunnerException("Problematic test cases"));
			ErrorDialog.openError(null, Messages.DIALOG_TEST_EXECUTION_REPORT_TITLE, msg, ms);
			return;
		}
		if (fExecutedTestCases > 0) {
			String msg = Messages.DIALOG_SUCCESFUL_TEST_EXECUTION(fExecutedTestCases);
			MessageDialog.openInformation(null, Messages.DIALOG_TEST_EXECUTION_REPORT_TITLE, msg);
		}
	}
}
