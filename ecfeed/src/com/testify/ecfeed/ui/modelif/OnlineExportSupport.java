package com.testify.ecfeed.ui.modelif;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.SetupDialogExportOnline;
import com.testify.ecfeed.ui.dialogs.SetupDialogOnline;

public class OnlineExportSupport extends AbstractOnlineSupport{

	public OnlineExportSupport(MethodNode target,
			ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, boolean runOnAndroid) {
		super(target, testMethodInvoker, fileInfoProvider, runOnAndroid);
	}

	public OnlineExportSupport(ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, boolean runOnAndroid) {
		super(testMethodInvoker, fileInfoProvider, runOnAndroid);
	}

	@Override
	protected SetupDialogOnline createSetupDialogOnline(Shell activeShell,
			MethodNode methodNode, IFileInfoProvider fileInfoProvider) {
		return new SetupDialogExportOnline(activeShell, methodNode, fileInfoProvider);
	}
}
