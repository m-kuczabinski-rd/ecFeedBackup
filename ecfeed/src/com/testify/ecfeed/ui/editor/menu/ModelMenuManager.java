package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;

public class ModelMenuManager/* extends MenuAdapter */{

	private Menu fMenu;
	private TreeViewer fViewer;
	private ModelOperationManager fOperationManager;
	private AbstractFormPart fSourcePart;
	private IModelUpdateListener fUpdateListener;

	private class MenuSelectionAdapter extends SelectionAdapter{
		MenuOperation fOperation;
		public MenuSelectionAdapter(MenuOperation operation) {
			fOperation = operation;
		}
		@Override
		public void widgetSelected(SelectionEvent e){
			fOperation.execute();
		}
	}

	private class SelectNodeOperationAdapter extends MenuSelectionAdapter{
		public SelectNodeOperationAdapter(MenuOperation operation) {
			super(operation);
		}
		@Override
		public void widgetSelected(SelectionEvent e){
			GenericNode element = (GenericNode)fOperation.execute();
			if(element != null){
				selectElement(element);
			}
		}
		private void selectElement(GenericNode element) {
			fViewer.setSelection(new StructuredSelection(element));
		}
	}

	public ModelMenuManager(Menu menu, TreeViewer viewer, ModelOperationManager operationManager, AbstractFormPart sourcePart, IModelUpdateListener updateListener){
		fMenu = menu;
		fViewer = viewer;
		fOperationManager = operationManager;
		fSourcePart = sourcePart;
		fUpdateListener = updateListener;
	}

	public void refresh(IStructuredSelection selection){
		for(MenuItem item : fMenu.getItems()){
			item.dispose();
		}
		if(selection.isEmpty() == false){
			populateMenu(selection);
		}
	}

	@SuppressWarnings("unchecked")
	protected void populateMenu(IStructuredSelection selection) {
		List<GenericNode> selected = selection.toList();
		if(selected.size() == 1 && selected.get(0) instanceof GenericNode){
			addNewChildOperations(fMenu, (GenericNode)selected.get(0));
		}
		addCommonOperations(fMenu, selected);
		addTreeOperations(fMenu, selected);
		addMoveOperations(fMenu, selected);
		//		addTypeSpecificOperations(selected);
	}

	private void addMoveOperations(Menu menu, List<GenericNode> selected) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuOperation moveUpOperation = new MenuOperationMoveUpDown(selected, true, fOperationManager, fSourcePart, fUpdateListener);
		MenuOperation moveDownOperation = new MenuOperationMoveUpDown(selected, false, fOperationManager, fSourcePart, fUpdateListener);
		addOperation(menu, moveUpOperation, new MenuSelectionAdapter(moveUpOperation), SWT.ALT + SWT.UP);
		addOperation(menu, moveDownOperation, new MenuSelectionAdapter(moveDownOperation), SWT.ALT + SWT.DOWN);
	}

	private void addCommonOperations(Menu menu, List<GenericNode> selected) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuOperation copyOperation = new MenuOperationCopy(selected);
		MenuOperation cutOperation = new MenuOperationCut(selected, fOperationManager, fSourcePart, fUpdateListener);
		MenuOperation pasteOperation = new MenuOperationPaste(selected, fOperationManager, fSourcePart, fUpdateListener);
		MenuOperation deleteOperation = new MenuOperationDelete(selected, fOperationManager, fSourcePart, fUpdateListener);
		addOperation(menu, copyOperation, new MenuSelectionAdapter(copyOperation), SWT.CTRL + 'c');
		addOperation(menu, cutOperation, new MenuSelectionAdapter(cutOperation), SWT.CTRL + 'x');
		addOperation(menu, pasteOperation, new SelectNodeOperationAdapter(pasteOperation), SWT.CTRL + 'v');
		addOperation(menu, deleteOperation, new MenuSelectionAdapter(deleteOperation), SWT.DEL);
	}

	private void addTreeOperations(Menu menu, List<GenericNode> selected) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuOperation selectAllOperation = new MenuOperationSelectAll(fViewer);
		MenuOperation expandOperation = new MenuOperationExpand(fViewer, selected);
		MenuOperation collapseOperation = new MenuOperationCollapse(fViewer, selected);
		addOperation(menu, selectAllOperation, new MenuSelectionAdapter(selectAllOperation), SWT.CTRL + 'a');
		addOperation(menu, expandOperation, new MenuSelectionAdapter(expandOperation));
		addOperation(menu, collapseOperation, new MenuSelectionAdapter(collapseOperation));
	}

	private void addOperation(Menu menu, MenuOperation operation, SelectionListener listener, int accelerator) {
		MenuItem item = new MenuItem(menu, SWT.NONE);
		item.setText(operation.getName());
		item.setEnabled(operation.isEnabled());
		item.addSelectionListener(listener);
		if(accelerator != SWT.NONE){
			item.setAccelerator(accelerator);
		}
	}

	private void addOperation(Menu menu, MenuOperation operation, SelectionListener listener) {
		addOperation(menu, operation, listener, SWT.NONE);
	}

	@SuppressWarnings("unchecked")
	private void addNewChildOperations(Menu menu, GenericNode node) {
		NewChildOperationProvider opProvider = new NewChildOperationProvider(fOperationManager, fSourcePart, fUpdateListener);
		try {
			List<MenuOperation> operations = (List<MenuOperation>)node.accept(opProvider);
			if(operations == null) return;
			for(MenuOperation operation : operations){
				MenuItem item = new MenuItem(menu, SWT.NONE);
				item.setText(operation.getName());
				item.setEnabled(operation.isEnabled());
				item.addSelectionListener(new SelectNodeOperationAdapter(operation));
			}
		} catch (Exception e) {} 
	}
}

