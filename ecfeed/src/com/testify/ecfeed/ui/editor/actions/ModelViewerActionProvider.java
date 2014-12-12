package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;


public class ModelViewerActionProvider extends ActionGroups {

	public ModelViewerActionProvider(TreeViewer viewer, IModelUpdateContext context, boolean selectRoot) {
		addEditActions(viewer, context);
		addViewerActions(viewer, context, selectRoot);
		addMoveActions(viewer, context);
	}

	public ModelViewerActionProvider(TableViewer viewer, IModelUpdateContext context) {
		addEditActions(viewer, context);
		addViewerActions(viewer);
		addMoveActions(viewer, context);
	}

	private void addEditActions(ISelectionProvider selectionProvider, IModelUpdateContext context){
		DeleteAction deleteAction = new DeleteAction(selectionProvider, context);
		addAction("edit", new CopyAction(selectionProvider));
		addAction("edit", new CutAction(new CopyAction(selectionProvider), deleteAction));
		addAction("edit", new PasteAction(selectionProvider, context));
		addAction("edit", deleteAction);
	}

	private void addMoveActions(ISelectionProvider selectionProvider, IModelUpdateContext context){
		addAction("move", new MoveUpDownAction(true, selectionProvider, context));
		addAction("move", new MoveUpDownAction(false, selectionProvider, context));
	}

	private void addViewerActions(TreeViewer viewer, IModelUpdateContext context, boolean selectRoot){
		addAction("viewer", new SelectAllAction(viewer, selectRoot));
//		addAction("viewer", new ExpandAction(viewer));
//		addAction("viewer", new CollapseAction(viewer));
		addAction("viewer", new ExpandCollapseAction(viewer));
	}

	private void addViewerActions(TableViewer viewer){
		addAction("viewer", new SelectAllAction(viewer));
	}

}
