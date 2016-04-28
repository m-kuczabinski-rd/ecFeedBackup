package com.testify.ecfeed.ui.modelif;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.SetupDialogExecuteOnline;
import com.testify.ecfeed.ui.dialogs.SetupDialogOnline;

public class OnlineTestRunningSupport extends AbstractOnlineSupport{

	public OnlineTestRunningSupport(MethodNode target,
			ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, boolean runOnAndroid) {
		super(target, testMethodInvoker, fileInfoProvider, runOnAndroid);
	}

	public OnlineTestRunningSupport(ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, boolean runOnAndroid) {
		super(testMethodInvoker, fileInfoProvider, runOnAndroid);
	}

	@Override
	protected SetupDialogOnline createSetupDialogOnline(Shell activeShell,
			MethodNode methodNode, IFileInfoProvider fileInfoProvider) {
		return new SetupDialogExecuteOnline(activeShell, methodNode, fileInfoProvider);
	}
}
