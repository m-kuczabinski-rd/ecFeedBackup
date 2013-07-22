package com.testify.ecfeed.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.testify.ecfeed.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.editor.EcMultiPageEditor;
import com.testify.ecfeed.editor.outline.EcContentOutlinePage;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.utils.EcModelUtils;

public class AddTestClassHandler extends AbstractHandler implements IHandler {

	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		RootNode model = (RootNode)selection.getFirstElement();
		IType selectedClass = selectClass(selection);

		ClassNode classNode = EcModelUtils.generateClassModel(selectedClass);
		if(EcModelUtils.classExists(model, classNode.getQualifiedName())){
			return null;
		}
		model.addClass(classNode);

		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		IPage page = ((ContentOutline)part).getCurrentPage();
		EcMultiPageEditor editor = ((EcContentOutlinePage)page).getEditor();
		editor.updateModel(model);

		return null;
	}

	private IType selectClass(IStructuredSelection selection) {
		
		TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getDefault().getActiveShell());
		dialog.open();
		
		Object selectedType = dialog.getFirstResult();
		return (IType)selectedType;
	}
}
