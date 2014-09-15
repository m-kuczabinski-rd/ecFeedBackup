package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.ActionFactory;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;


public class ModelViewerActionFactory extends AbstractActionFactory {
	
	public ModelViewerActionFactory(TreeViewer viewer, IModelUpdateContext context, boolean selectRoot) {
		addEditActions(viewer, context);
		addViewerActions(viewer, context, selectRoot);
		addMoveActions(viewer, context);
	}
	
	public ModelViewerActionFactory(TableViewer viewer, IModelUpdateContext context) {
		addEditActions(viewer, context);
		addViewerActions(viewer, context);
		addMoveActions(viewer, context);
	}

	private void addEditActions(ISelectionProvider selectionProvider, IModelUpdateContext context){
		addAction("edit", ActionFactory.COPY.getId(), "Copy\tCtrl+c", new CopyAction(selectionProvider));
		addAction("edit", ActionFactory.CUT.getId(), "Cut\tCtrl+x", new CutAction(new CopyAction(selectionProvider), new DeleteAction(selectionProvider, context)));
		addAction("edit", ActionFactory.PASTE.getId(), "Copy\tCtrl+v", new PasteAction(selectionProvider, context));
		addAction("edit", ActionFactory.DELETE.getId(), "Delete\tDEL", new PasteAction(selectionProvider, context));
	}
	
	private void addMoveActions(ISelectionProvider selectionProvider, IModelUpdateContext context){
		addAction("move", "moveUp", "Move Up\tALT+Up", new MoveUpDownAction(true, selectionProvider, context));
		addAction("move", "moveDown", "Move Down\tALT+Down", new MoveUpDownAction(false, selectionProvider, context));
	}

	private void addViewerActions(TreeViewer viewer, IModelUpdateContext context, boolean selectRoot){
		addAction("viewer", ActionFactory.SELECT_ALL.getId(), "Select All\tCtrl+a", new SelectAllAction(viewer, selectRoot));
		addAction("viewer", "expand", "Expand\tCtrl+Shift+e", new ExpandAction(viewer));
		addAction("viewer", "collapse", "Collapse\tCtrl+Shift+w", new CollapseAction(viewer));
	}

	private void addViewerActions(TableViewer viewer, IModelUpdateContext context){
		addAction("viewer", ActionFactory.SELECT_ALL.getId(), "Select All\tCtrl+a", new SelectAllAction(viewer));
	}

}
