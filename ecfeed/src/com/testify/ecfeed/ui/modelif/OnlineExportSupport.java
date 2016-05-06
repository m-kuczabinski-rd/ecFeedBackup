package com.testify.ecfeed.ui.modelif;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.SetupDialogExportOnline;
import com.testify.ecfeed.ui.dialogs.SetupDialogOnline;

public class OnlineExportSupport extends AbstractOnlineSupport {

	public OnlineExportSupport(ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, String initialExportTemplate) {
		super(testMethodInvoker, fileInfoProvider, initialExportTemplate,
				RunMode.EXPORT);
	}

	@Override
	protected SetupDialogOnline createSetupDialogOnline(Shell activeShell,
			MethodNode methodNode, IFileInfoProvider fileInfoProvider,
			String initialExportTemplate) {
		return new SetupDialogExportOnline(activeShell, methodNode,
				fileInfoProvider, initialExportTemplate);
	}
}
