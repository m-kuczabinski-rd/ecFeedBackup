package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.ActionFactory;

import com.testify.ecfeed.ui.editor.actions.CopyAction;
import com.testify.ecfeed.ui.editor.actions.CutAction;
import com.testify.ecfeed.ui.editor.actions.DeleteAction;
import com.testify.ecfeed.ui.editor.actions.IActionProvider;
import com.testify.ecfeed.ui.editor.actions.SelectAllAction;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelViewerGlobalActionProvider implements IActionProvider {

	private ISelectionProvider fSelectionProvider;
	private IModelUpdateContext fUpdateContext;
	private TreeViewer fTreeViewer;
	private TableViewer fTableViewer;

	public ModelViewerGlobalActionProvider(TreeViewer viewer, IModelUpdateContext context){
		this((ISelectionProvider)viewer, context);
		fTreeViewer = viewer;
	}
	
	public ModelViewerGlobalActionProvider(TableViewer viewer, IModelUpdateContext context){
		this((ISelectionProvider)viewer, context);
		fTableViewer = viewer;
	}
	
	private ModelViewerGlobalActionProvider(ISelectionProvider selectionProvider, IModelUpdateContext context){
		fSelectionProvider = selectionProvider;
		fUpdateContext = context;
	}
	
	@Override
	public Action getAction(String actionId) {
		if(actionId.equals(ActionFactory.COPY.getId())){
			return new CopyAction(fSelectionProvider);
		}
		if(actionId.equals(ActionFactory.CUT.getId())){
			return new CutAction(new CopyAction(fSelectionProvider), new DeleteAction(fSelectionProvider, fUpdateContext));
		}
		if(actionId.equals(ActionFactory.DELETE.getId())){
			return new DeleteAction(fSelectionProvider, fUpdateContext);
		}
		if(actionId.equals(ActionFactory.SELECT_ALL.getId())){
			if(fTableViewer != null){
				return new SelectAllAction(fTableViewer);
			}
			return new SelectAllAction(fTreeViewer, false);
		}

		return null;
	}
}
