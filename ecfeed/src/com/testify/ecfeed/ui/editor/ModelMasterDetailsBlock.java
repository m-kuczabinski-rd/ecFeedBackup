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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.editor.actions.RedoAction;
import com.testify.ecfeed.ui.editor.actions.UndoAction;

public class ModelMasterDetailsBlock extends MasterDetailsBlock implements ISelectionChangedListener{

	private ModelMasterSection fMasterSection;
	private ModelPage fPage;
	
	private class GenericToolbarAction extends Action{
		private final String fActionId;
		
		public GenericToolbarAction(String id){
			fActionId = id;
		}
		
		@Override
		public boolean isEnabled(){
			Action action = getFocusedSection().getAction(fActionId);
			if(action  != null){
				return action.isEnabled();
			}
			return false;
		}

		@Override
		public void run(){
			Action action = getFocusedSection().getAction(fActionId);
			if(action != null){
				action.run();
			}
		}
	}

	public ModelMasterDetailsBlock(ModelPage modelPage, ModelOperationManager operationManager) {
		fPage = modelPage;
	}

	public void selectNode(GenericNode node){
		fMasterSection.selectElement(node);
	}

	public ModelMasterSection getMasterSection(){
		return fMasterSection;
	}

	public BasicDetailsPage getCurrentPage(){
		if(detailsPart != null){
			return (BasicDetailsPage)detailsPart.getCurrentPage();
		}
		return null;
	}

	public ModelPage getPage(){
		return fPage;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		detailsPart.selectionChanged(fMasterSection, event.getSelection());
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		IActionBars actionBars = fPage.getEditorSite().getActionBars();

		fMasterSection = new ModelMasterSection(this, parent, toolkit, getPage().getEditor().getModelOperationManager(), actionBars);
		fMasterSection.initialize(managedForm);
		fMasterSection.addSelectionChangedListener(this);
		fMasterSection.setInput(getModel());
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(RootNode.class, new ModelDetailsPage(this));
		detailsPart.registerPage(ClassNode.class, new ClassDetailsPage(this));
		detailsPart.registerPage(MethodNode.class, new MethodDetailsPage(this));
		detailsPart.registerPage(CategoryNode.class, new CategoryDetailsPage(this));
		detailsPart.registerPage(TestCaseNode.class, new TestCaseDetailsPage(this));
		detailsPart.registerPage(ConstraintNode.class, new ConstraintDetailsPage(this));
		detailsPart.registerPage(PartitionNode.class, new PartitionDetailsPage(this));

		selectNode(getModel());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		IActionBars actionBars = fPage.getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), new UndoAction(fMasterSection));
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), new RedoAction(fMasterSection));
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), new GenericToolbarAction(ActionFactory.COPY.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), new GenericToolbarAction(ActionFactory.CUT.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), new GenericToolbarAction(ActionFactory.PASTE.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), new GenericToolbarAction(ActionFactory.DELETE.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), new GenericToolbarAction(ActionFactory.SELECT_ALL.getId()));
	}
	
	protected BasicSection getFocusedSection(){
		if(fMasterSection.getViewer().getControl().isFocusControl()){
			return fMasterSection;
		}
		else{
			return getCurrentPage().getFocusedViewerSection();
		}
	}

	private RootNode getModel() {
		return fPage.getModel();
	}
}
