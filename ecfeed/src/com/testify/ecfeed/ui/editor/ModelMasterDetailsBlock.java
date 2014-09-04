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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
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

public class ModelMasterDetailsBlock extends MasterDetailsBlock implements ISelectionChangedListener{

	private ModelMasterSection fMasterSection;
	private ModelPage fPage;

	public ModelMasterDetailsBlock(ModelPage modelPage, ModelOperationManager operationManager) {
		fPage = modelPage;
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		fMasterSection = new ModelMasterSection(parent, toolkit, getPage().getEditor().getModelOperationManager());
		fMasterSection.initialize(managedForm);
		fMasterSection.addSelectionChangedListener(this);
		fMasterSection.setModel(getModel());
	}

	private RootNode getModel() {
		return fPage.getModel();
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(RootNode.class, new ModelDetailsPage(fMasterSection));
		detailsPart.registerPage(ClassNode.class, new ClassDetailsPage(fMasterSection));
		detailsPart.registerPage(MethodNode.class, new MethodDetailsPage(fMasterSection));
		detailsPart.registerPage(CategoryNode.class, new CategoryDetailsPage(fMasterSection));
		detailsPart.registerPage(TestCaseNode.class, new TestCaseDetailsPage(fMasterSection));
		detailsPart.registerPage(ConstraintNode.class, new ConstraintDetailsPage(fMasterSection));
		detailsPart.registerPage(PartitionNode.class, new PartitionDetailsPage(fMasterSection));

		selectNode(getModel());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}
	
	public void selectNode(GenericNode node){
		fMasterSection.selectElement(node);
	}

	public ModelMasterSection getMasterSection(){
		return fMasterSection;
	}

	public IDetailsPage getCurrentPage(){
		return detailsPart.getCurrentPage();
	}
	
	public ModelPage getPage(){
		return fPage;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		detailsPart.selectionChanged(fMasterSection, event.getSelection());
	}
}
