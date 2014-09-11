package com.testify.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.editor.actions.AbstractAddChildAction;
import com.testify.ecfeed.ui.editor.actions.AddChildActionFactory;
import com.testify.ecfeed.ui.editor.actions.CopyAction;
import com.testify.ecfeed.ui.editor.actions.CutAction;
import com.testify.ecfeed.ui.editor.actions.DeleteAction;
import com.testify.ecfeed.ui.editor.actions.ExpandAction;
import com.testify.ecfeed.ui.editor.actions.MoveUpDownAction;
import com.testify.ecfeed.ui.editor.actions.PasteAction;
import com.testify.ecfeed.ui.editor.actions.RedoAction;
import com.testify.ecfeed.ui.editor.actions.SelectAllAction;
import com.testify.ecfeed.ui.editor.actions.UndoAction;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelViewerMenuAdapter implements MenuListener{

	private TreeViewer fViewer;
	private IModelUpdateContext fContext;

	public class MenuOperation{
		private String fName;
		private Action fAction;

		public MenuOperation(String name, Action action){
			fName = name;
			fAction = action;
		}

		public void execute(){
			fAction.run();
		}

		public boolean isEnabled(){
			return fAction.isEnabled();
		}

		public String getName(){
			return fName;
		}
	}

	private class MenuSelectionAdapter extends SelectionAdapter{
		
		private MenuOperation fOperation;

		public MenuSelectionAdapter(MenuOperation operation){
			fOperation = operation;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e){
			fOperation.execute();
		}
	}

	public ModelViewerMenuAdapter(TreeViewer viewer, IModelUpdateContext context){
		fViewer = viewer;
		fContext = context;
	}

	@Override
	public void menuHidden(MenuEvent e) {
	}

	@Override
	public void menuShown(MenuEvent e) {
		for(MenuItem item : ((Menu)e.getSource()).getItems()){
			item.dispose();
		}
		populate((Menu)e.getSource(), (IStructuredSelection)fViewer.getSelection());
	}

	protected void populate(Menu menu, IStructuredSelection selection){
		if(selection.isEmpty() == false){
			populateMenu(menu, selection);
		}
	}

	@SuppressWarnings("unchecked")
	protected void populateMenu(Menu menu, IStructuredSelection selection) {
		List<GenericNode> selected = selection.toList();
		if(selected.size() == 1 && selected.get(0) instanceof GenericNode){
			addNewChildOperations(menu, (GenericNode)selected.get(0));
		}
		addCommonOperations(menu, selected);
		addTreeOperations(menu, selected);
		addMoveOperations(menu, selected);
	}

	protected void addOperation(Menu menu, MenuOperation operation){
		MenuItem item = new MenuItem(menu, SWT.NONE);
		item.setText(operation.getName());
		item.setEnabled(operation.isEnabled());
		item.addSelectionListener(new MenuSelectionAdapter(operation));
	}
	
	private void addNewChildOperations(Menu menu, GenericNode node) {
		List<AbstractAddChildAction> actions = new AddChildActionFactory(fViewer, fContext).getPossibleActions(node);
		for(AbstractAddChildAction action : actions){
			addOperation(menu, new MenuOperation(action.getName(), action));
		}
	}

	private void addCommonOperations(Menu menu, List<GenericNode> selected) {
		new MenuItem(menu, SWT.SEPARATOR);
		addOperation(menu, new MenuOperation("Undo\tCtrl+Z", new UndoAction(fContext)));
		addOperation(menu, new MenuOperation("Redo\tCtrl+Shift+Z", new RedoAction(fContext)));
		new MenuItem(menu, SWT.SEPARATOR);
		addOperation(menu, new MenuOperation("Copy\tCtrl+C", new CopyAction(fViewer)));
		addOperation(menu, new MenuOperation("Cut\tCtrl+X", new CutAction(fViewer, fContext)));
		addOperation(menu, new MenuOperation("Paste\tCtrl+V", new PasteAction(fViewer, fContext)));
		addOperation(menu, new MenuOperation("Delete\tDEL", new DeleteAction(fViewer, fContext)));
	}

	private void addTreeOperations(Menu menu, List<GenericNode> selected) {
		new MenuItem(menu, SWT.SEPARATOR);
		addOperation(menu, new MenuOperation("Select All\tCtrl+A", new SelectAllAction(fViewer, false)));
		addOperation(menu, new MenuOperation("Expand all\tCTRL+ALT+E", new ExpandAction(fViewer)));
		addOperation(menu, new MenuOperation("Collapse\tCTRL+ALT+W", new ExpandAction(fViewer)));
	}

	private void addMoveOperations(Menu menu, List<GenericNode> selected) {
		new MenuItem(menu, SWT.SEPARATOR);
		addOperation(menu, new MenuOperation("Move Up\tCtrl+UP", new MoveUpDownAction(true, fViewer, fContext)));
		addOperation(menu, new MenuOperation("Move Down\tCtrl+DOWN", new MoveUpDownAction(false, fViewer, fContext)));
	}
}

