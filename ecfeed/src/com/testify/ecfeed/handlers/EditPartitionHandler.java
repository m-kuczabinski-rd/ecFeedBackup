package com.testify.ecfeed.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.testify.ecfeed.dialogs.PartitionSettingsDialog;
import com.testify.ecfeed.editors.EcEditor;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.outline.EcContentOutlinePage;

public class EditPartitionHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		if(selection.getFirstElement() instanceof PartitionNode){
			PartitionNode partition = (PartitionNode)selection.getFirstElement();
			PartitionSettingsDialog dialog = new PartitionSettingsDialog(Display.getDefault().getActiveShell(), partition);
			dialog.create();
			if (dialog.open() == Window.OK) {
				partition.setName(dialog.getPartitionName());
				partition.setValueFromString(dialog.getPartitionValueString());

				IWorkbenchPart part = HandlerUtil.getActivePart(event);
				if(part instanceof ContentOutline){
					ContentOutline outline = (ContentOutline)part;
					IPage page = outline.getCurrentPage();
					if (page instanceof EcContentOutlinePage){
						EcEditor editor = ((EcContentOutlinePage) page).getEditor();
						if(partition.getRoot() instanceof RootNode){
							editor.updateModel((RootNode)partition.getRoot());
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
