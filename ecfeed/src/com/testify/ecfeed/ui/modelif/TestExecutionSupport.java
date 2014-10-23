package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;

import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.plugin.Activator;

public abstract class TestExecutionSupport {

	private List<Status> fStatuses;

	public TestExecutionSupport(){
		fStatuses = new ArrayList<>();
	}

	protected void addFailedTest(RunnerException e){
		fStatuses.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
	}

	protected void clearFailedTests(){
		fStatuses.clear();
	}
	
	protected void displayTestStatusDialog(int executedTestCases){
		String msg = String.valueOf(fStatuses.size()) + " of " + String.valueOf(executedTestCases) + " test cases did not executed successfully";
		MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, fStatuses.toArray(new Status[]{}), "Open details to see report", new RunnerException("Execution report"));
		ErrorDialog.openError(null, Messages.DIALOG_TEST_EXECUTION_REPORT_TITLE, msg, ms);  
	}
}
