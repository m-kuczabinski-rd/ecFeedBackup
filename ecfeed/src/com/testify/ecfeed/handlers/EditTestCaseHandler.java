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

import com.testify.ecfeed.dialogs.AddTestCaseDialog;
import com.testify.ecfeed.editor.EcMultiPageEditor;
//import com.testify.ecfeed.editor.outline.EcContentOutlinePage;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class EditTestCaseHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		TestCaseNode testCase = (TestCaseNode)selection.getFirstElement();
		MethodNode method = (MethodNode)testCase.getParent();
		AddTestCaseDialog dialog = 
				new AddTestCaseDialog(Display.getDefault().getActiveShell(), method);
		dialog.create();
		if (dialog.open() == Window.OK) {
			IWorkbenchPart part = HandlerUtil.getActivePart(event);
			IPage page = ((ContentOutline)part).getCurrentPage();
//			EcMultiPageEditor editor = ((EcContentOutlinePage)page).getEditor();
//			if(method.getRoot() instanceof RootNode){
//				editor.updateModel((RootNode)method.getRoot());
//			}
		}
		return null;
	}

}
