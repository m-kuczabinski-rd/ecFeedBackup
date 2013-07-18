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

import com.testify.ecfeed.dialogs.PartitionSettingsDialog;
import com.testify.ecfeed.editor.outline.EcContentOutlinePage;
import com.testify.ecfeed.editors.EcMultiPageEditor;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;

public class EditPartitionHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		PartitionNode partition = (PartitionNode)selection.getFirstElement();
		CategoryNode parent = (CategoryNode)partition.getParent();
		PartitionSettingsDialog dialog = new PartitionSettingsDialog(Display.getDefault().getActiveShell(), parent, partition);
		dialog.create();
		if (dialog.open() == Window.OK) {
			String name = dialog.getPartitionName();
			partition.setName(name);
			Object value = dialog.getPartitionValue();
			partition.setValue(value);
			
			IWorkbenchPart part = HandlerUtil.getActivePart(event);
			IPage page = ((ContentOutline)part).getCurrentPage();
			EcMultiPageEditor editor = ((EcContentOutlinePage)page).getEditor();
			if(partition.getRoot() instanceof RootNode){
				editor.updateModel((RootNode)partition.getRoot());
			}
		}
		return null;
	}
}