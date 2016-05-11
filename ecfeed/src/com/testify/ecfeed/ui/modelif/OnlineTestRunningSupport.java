package com.testify.ecfeed.ui.modelif;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.android.external.ApkInstallerExt;
import com.testify.ecfeed.android.external.DeviceCheckerExt;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.core.runner.RunnerException;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.SetupDialogExecuteOnline;
import com.testify.ecfeed.ui.dialogs.SetupDialogOnline;

public class OnlineTestRunningSupport extends AbstractOnlineSupport {

	boolean fRunOnAndroid;
	IFileInfoProvider fFileInfoProvider;

	public OnlineTestRunningSupport(ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, boolean runOnAndroid) {
		super(testMethodInvoker, fileInfoProvider, null,
				getRunMode(runOnAndroid));

		fRunOnAndroid = runOnAndroid;
		fFileInfoProvider = fileInfoProvider;
	}

	private static RunMode getRunMode(boolean runOnAndroid) {
		if (runOnAndroid) {
			return RunMode.TEST_ON_ANDROID;
		}

		return RunMode.TEST_LOCALLY;
	}

	@Override
	protected SetupDialogOnline createSetupDialogOnline(Shell activeShell,
			MethodNode methodNode, IFileInfoProvider fileInfoProvider,
			String initialExportTemplate) {
		return new SetupDialogExecuteOnline(activeShell, methodNode,
				fileInfoProvider);
	}

	@Override
	protected void onDisplayTestSummary() {
		displayTestStatusDialog();
	}	

	@Override
	protected void prepareRun() throws InvocationTargetException {
		if (getRunMode(fRunOnAndroid) != RunMode.TEST_ON_ANDROID) {
			return;
		}
		DeviceCheckerExt.checkIfOneDeviceAttached();
		EclipseProjectHelper projectHelper = new EclipseProjectHelper(fFileInfoProvider);
		new ApkInstallerExt(projectHelper).installApplicationsIfModified();
	}

	@Override
	protected void processTestCase(List<ChoiceNode> testData) throws RunnerException {
		getRunner().runTestCase(testData);
	}

	@Override
	protected void setTargetMethod() throws RunnerException {
		getRunner().setTargetForTest(getTargetMethod());
	}	

}
