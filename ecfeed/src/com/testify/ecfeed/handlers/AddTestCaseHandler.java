package com.testify.ecfeed.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.testify.ecfeed.dialogs.EditTestCaseDialog;
import com.testify.ecfeed.editor.EcEditor;
import com.testify.ecfeed.editor.outline.EcContentOutlinePage;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class AddTestCaseHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		MethodNode method = (MethodNode)selection.getFirstElement();
		EditTestCaseDialog dialog = 
				new EditTestCaseDialog(Display.getDefault().getActiveShell(), method, null);
		dialog.create();
		if (dialog.open() == Window.OK) {
			TestCaseNode testCase = dialog.getTestCase();
			method.addTestCase(testCase);

			IWorkbenchPart part = HandlerUtil.getActivePart(event);
			IPage page = ((ContentOutline)part).getCurrentPage();
			EcEditor editor = ((EcContentOutlinePage)page).getEditor();
			if(method.getRoot() instanceof RootNode){
				editor.updateModel((RootNode)method.getRoot());
			}
		}
		return null;
	}
}
