package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelViewerToolbarUpdater implements IToolbarActionsUpdater {

	private TableViewer fTableViewer;
	private TreeViewer fTreeViewer;
	private ISelectionProvider fSelectionProvider;
	private IModelUpdateContext fUpdateContext;
	private IActionBars fActionBars;

	public ModelViewerToolbarUpdater(TableViewer viewer, IModelUpdateContext updateContext, IActionBars actionBars){
		this((ISelectionProvider)viewer, updateContext, actionBars);
		fTableViewer = viewer;
	}
	
	public ModelViewerToolbarUpdater(TreeViewer viewer, IModelUpdateContext updateContext, IActionBars actionBars){
		this((ISelectionProvider)viewer, updateContext, actionBars);
		fTreeViewer = viewer;
	}
	
	private ModelViewerToolbarUpdater(ISelectionProvider selectionProvider, IModelUpdateContext updateContext, IActionBars actionBars){
		fSelectionProvider = selectionProvider;
		fUpdateContext = updateContext;
		fActionBars = actionBars;
	}
	
	@Override
	public void updateActionBars() {
		fActionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), new CopyAction(fSelectionProvider));
		fActionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), new CutAction(fSelectionProvider, fUpdateContext));
		fActionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), new PasteAction(fSelectionProvider, fUpdateContext));
		fActionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), new DeleteAction(fSelectionProvider, fUpdateContext));
		if(fTreeViewer != null){
			fActionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), new SelectAllAction(fTreeViewer, false));
		}
		else if(fTableViewer != null){
			fActionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), new SelectAllAction(fTableViewer));
		}
	}

	@Override
	public void cleanActionBars() {
		fActionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), null);
		fActionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), null);
		fActionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), null);
		fActionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), null);
		fActionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), null);
	}
}
