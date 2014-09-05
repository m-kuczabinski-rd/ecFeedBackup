package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
		List<Object> selected = selection.toList();
		if(selected.size() == 1 && selected.get(0) instanceof GenericNode){
			addNewChildOperations(menu, (GenericNode)selected.get(0));
		}
//		addCommonOperations(menu, selected);
//		addTypeSpecificOperations(selected);
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
				item.addSelectionListener(new MenuSelectionAdapter(operation));
			}
		} catch (Exception e) {} 
	}

	private IStructuredSelection getSelection(){
		return (IStructuredSelection) fMasterSection.getSelection();
	}

}
