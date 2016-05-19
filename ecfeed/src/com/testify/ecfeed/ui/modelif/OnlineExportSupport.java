package com.testify.ecfeed.ui.modelif;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.core.runner.RunnerException;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.SetupDialogExportOnline;
import com.testify.ecfeed.ui.dialogs.SetupDialogOnline;

public class OnlineExportSupport extends AbstractOnlineSupport {

	public OnlineExportSupport(ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, String initialExportTemplate) {
		super(testMethodInvoker, fileInfoProvider, initialExportTemplate);
	}

	@Override
	protected SetupDialogOnline createSetupDialog(Shell activeShell,
			MethodNode methodNode, IFileInfoProvider fileInfoProvider,
			String initialExportTemplate) {
		return new SetupDialogExportOnline(activeShell, methodNode,
				fileInfoProvider, initialExportTemplate);
	}

	@Override
	public Result proceedInternal() {
		if (getTargetMethod().getParametersCount() == 0) {
			return Result.CANCELED;
		}

		return displayParametersDialogAndRunTests();
	}

	@Override
	protected void onDisplayTestSummary() {
	}

	@Override
	protected void prepareRun() throws InvocationTargetException {
	}

	@Override
	protected void processTestCase(List<ChoiceNode> testData)
			throws RunnerException {
		getRunner().prepareTestCaseForExport(testData);
	}

	@Override
	protected void setRunMethod() throws RunnerException {
		getRunner().setTargetForExport(getTargetMethod());
	}	

	@Override
	protected void setRunnerTarget(MethodNode target) throws RunnerException {
		getRunner().setTargetForExport(target);
	}
}
