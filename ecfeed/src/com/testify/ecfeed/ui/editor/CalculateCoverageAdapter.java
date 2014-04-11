package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.dialogs.CalculateCoverageDialog;

public class CalculateCoverageAdapter extends SelectionAdapter {

	private TestCasesViewer fViewerSection;

	CalculateCoverageAdapter(TestCasesViewer viewerSection) {
		fViewerSection = viewerSection;
	}

	private MethodNode getSelectedMethod() {
		return fViewerSection.getSelectedMethod();
	}

	private Shell getActiveShell() {
		return Display.getCurrent().getActiveShell();
	}

	public void widgetSelected(SelectionEvent e) {
		CalculateCoverageDialog dialog = new CalculateCoverageDialog(getActiveShell(), getSelectedMethod());
		dialog.open();
	}

}
