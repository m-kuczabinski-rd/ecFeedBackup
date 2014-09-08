package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.editor.ModelMasterSection;

public class ModelMenuAdapter extends MenuAdapter {

	private ModelMasterSection fMasterSection;
	
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

	private class AddNodeSelectionAdapter extends MenuSelectionAdapter{
		public AddNodeSelectionAdapter(MenuOperation operation) {
			super(operation);
		}

		@Override
		public void widgetSelected(SelectionEvent e){
			fMasterSection.selectElement((GenericNode)fOperation.execute());
		}
	}

	public ModelMenuAdapter(ModelMasterSection parentSection) {
		fMasterSection = parentSection;
	}
	
	@Override
	public void menuShown(MenuEvent e){
		Menu menu = (Menu)e.getSource();
		
		for(MenuItem item : menu.getItems()){
			item.dispose();
		}
		
		populateMenu(menu, getSelection());
	}

	@SuppressWarnings("unchecked")
	protected void populateMenu(Menu menu, IStructuredSelection selection) {
		List<GenericNode> selected = selection.toList();
		if(selected.size() == 1 && selected.get(0) instanceof GenericNode){
			addNewChildOperations(menu, (GenericNode)selected.get(0));
		}
		addCommonOperations(menu, selected);
		new MenuItem(menu, SWT.SEPARATOR);
		//		addTypeSpecificOperations(selected);
	}

	private void addCommonOperations(Menu menu, List<GenericNode> selected) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuOperation copyOperation = new MenuOperationCopy(selected);
		MenuOperation cutOperation = new MenuOperationCut(selected, fMasterSection.getOperationManager(), fMasterSection, fMasterSection.getUpdateListener());
		MenuOperation pasteOperation = new MenuOperationPaste(selected, fMasterSection.getOperationManager(), fMasterSection, fMasterSection.getUpdateListener());
		MenuOperation deleteOperation = new MenuOperationDelete(selected, fMasterSection.getOperationManager(), fMasterSection, fMasterSection.getUpdateListener());
		addOperation(menu, copyOperation, new MenuSelectionAdapter(copyOperation));
		addOperation(menu, cutOperation, new MenuSelectionAdapter(cutOperation));
		addOperation(menu, pasteOperation, new MenuSelectionAdapter(pasteOperation));
		addOperation(menu, deleteOperation, new MenuSelectionAdapter(deleteOperation));
//		addCutOperation(menu, selected);
//		addPasteOperation(menu, selected);
//		new MenuItem(menu, SWT.SEPARATOR);
//		addDeleteOperation(menu, selected);
//		addSelectAllOperation(menu, selected);
	}

	private void addOperation(Menu menu, MenuOperation operation, SelectionListener listener) {
		MenuItem item = new MenuItem(menu, SWT.NONE);
		item.setText(operation.getName());
		item.setEnabled(operation.isEnabled());
		item.addSelectionListener(listener);
	}

	@SuppressWarnings("unchecked")
	private void addNewChildOperations(Menu menu, GenericNode node) {
		NewChildOperationProvider opProvider = new NewChildOperationProvider(fMasterSection.getOperationManager(), fMasterSection, fMasterSection.getUpdateListener());
		try {
			List<MenuOperation> operations = (List<MenuOperation>)node.accept(opProvider);
			if(operations == null) return;
			for(MenuOperation operation : operations){
				MenuItem item = new MenuItem(menu, SWT.NONE);
				item.setText(operation.getName());
				item.setEnabled(operation.isEnabled());
				item.addSelectionListener(new AddNodeSelectionAdapter(operation));
			}
		} catch (Exception e) {} 
	}

	private IStructuredSelection getSelection(){
		return (IStructuredSelection) fMasterSection.getSelection();
	}

}
