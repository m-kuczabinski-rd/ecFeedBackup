package com.testify.ecfeed.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.testify.ecfeed.editor.EcEditor;
import com.testify.ecfeed.editor.outline.EcContentOutlinePage;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.RootNode;

public class RemoveNodeHandler extends AbstractHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		RootNode root = (RootNode)((GenericNode)selection.getFirstElement()).getRoot();
		
		Iterator iterator = selection.iterator();
		while(iterator.hasNext()){
			GenericNode node = (GenericNode)iterator.next();
			if(node != null && node.getParent() != null){
				node.getParent().removeChild(node);
			}
		}
		
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		IPage page = ((ContentOutline)part).getCurrentPage();
		EcEditor editor = ((EcContentOutlinePage)page).getEditor();

		editor.updateModel(root);
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
