/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.editor.ModelContentProvider.IModelWrapper;
import com.testify.ecfeed.ui.modelif.GenericNodeInterface;
import com.testify.ecfeed.ui.modelif.NodeInterfaceFactory;

public class ModelMasterSection extends TreeViewerSection{
	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private static final int AUTO_EXPAND_LEVEL = 3;

	private List<IModelSelectionListener> fModelSelectionListeners;
	private Button fMoveUpButton;
	private Button fMoveDownButton;
	private RootNode fModel;
	private MenuOperationManager fMenuManager;
	private Menu fMenu;
	private ModelOperationManager fOperationManager;
	
	protected class MenuSelectionAdapter extends SelectionAdapter{
		MenuOperation fOperation;
		
		@Override
		public void widgetSelected(SelectionEvent e){
			fOperation.execute();
		}
		
		public MenuSelectionAdapter(MenuOperation operation, GenericNode target){
			super();
			fOperation = operation;			
		}
		
	}
	
	private static class UpdateListener implements IModelUpdateListener{
		@Override
		public void modelUpdated(AbstractFormPart source) {
			source.markDirty();
		}
	}

	private class MoveUpAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			moveSelectedItem(true);
		}
	}

	private class MoveDownAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			moveSelectedItem(false);
		}
	}
	
	private class ModelSelectionListener implements ISelectionChangedListener{
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			GenericNode selectedNode = (GenericNode)selection.getFirstElement();
			enableSortButtons(selectedNode);
			notifyModelSelectionListeners(selection);
		}

		private void enableSortButtons(GenericNode selectedElement) {
			boolean enabled = true;
			if (selectedElement instanceof RootNode) {
				enabled = false;
			}
			fMoveUpButton.setEnabled(enabled);
			fMoveDownButton.setEnabled(enabled);
		}
	}

	public ModelMasterSection(Composite parent, FormToolkit toolkit, ModelOperationManager operationManager) {
		super(parent, toolkit, STYLE, new UpdateListener());
		fModelSelectionListeners = new ArrayList<IModelSelectionListener>();
		fMenuManager = new MenuOperationManager(this);
		fOperationManager = operationManager;
	}
	
	public void addModelSelectionChangedListener(IModelSelectionListener listener){
		fModelSelectionListeners.add(listener);
	}
		
	public void setModel(RootNode model){
		fModel = model;
		setInput(new IModelWrapper() {
			@Override
			public RootNode getModel() {
				return fModel;
			}
		});
	}
	
	public RootNode getModel(){
		return fModel;
	}
	
	public ModelOperationManager getOperationManager(){
		return fOperationManager;
	}
	
	@Override
	protected void createContent(){
		super.createContent();
		getSection().setText("Structure");
		fMoveUpButton = addButton("Move Up", new MoveUpAdapter());
		fMoveDownButton = addButton("Move Down", new MoveDownAdapter());
		getTreeViewer().setAutoExpandLevel(AUTO_EXPAND_LEVEL);
		addSelectionChangedListener(new ModelSelectionListener());
		createMenu();
	}

	@Override
	protected IContentProvider viewerContentProvider() {
		return new ModelContentProvider();
	}
	
	@Override 
	protected IBaseLabelProvider viewerLabelProvider() {
		return new DecoratingLabelProvider(new ModelLabelProvider(), new ModelLabelDecorator());
	}
	
	protected void createMenu(){
		fMenu = new Menu(getTreeViewer().getTree());
		Tree tree = getTreeViewer().getTree();
		tree.setMenu(fMenu);

		fMenu.addMenuListener(new MenuAdapter(){
			@Override
			public void menuShown(MenuEvent e){
				Tree tree = getTreeViewer().getTree();
				Menu menu = (Menu)e.getSource();
				MenuItem[] items = menu.getItems();
				for(int i = 0; i < items.length; i++){
					items[i].dispose();
				}

				if(tree.getSelection()[0].getData() instanceof GenericNode){
					GenericNode target = (GenericNode)tree.getSelection()[0].getData();
					for(MenuOperation operation : fMenuManager.getOperations(target)){
						MenuItem item = new MenuItem(fMenu, SWT.NONE);
						item.setText(operation.getOperationName());
						item.addSelectionListener(new MenuSelectionAdapter(operation, target));
						item.setEnabled(operation.isEnabled());
					}
				}
			}
		});
	}
	private void notifyModelSelectionListeners(ISelection newSelection) {
		for(IModelSelectionListener listener : fModelSelectionListeners){
			listener.modelSelectionChanged(newSelection);
		}
	}

	private void moveSelectedItem(boolean moveUp){
		GenericNodeInterface nodeIf = new NodeInterfaceFactory(fOperationManager).getNodeInterface(selectedNode());
		nodeIf.moveUpDown(moveUp, this, this.getUpdateListener());
		refresh();
	}

	private GenericNode selectedNode() {
		Object selectedElement = getSelectedElement();
		if(selectedElement instanceof GenericNode){
			return (GenericNode)selectedElement;
		}
		return null;
	}

	
}
