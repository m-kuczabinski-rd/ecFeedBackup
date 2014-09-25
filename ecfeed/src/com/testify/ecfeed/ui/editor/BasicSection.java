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

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modeladp.ModelOperationManager;
import com.testify.ecfeed.ui.editor.actions.IActionProvider;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;

public abstract class BasicSection extends SectionPart implements IModelUpdateContext{
	private Composite fClientComposite;
	private Control fTextClient;
	private IActionProvider fActionProvider;
	private IModelUpdateContext fUpdateContext;
	private ISectionContext fSectionContext;

	protected class SelectNodeDoubleClickListener implements IDoubleClickListener {

		private ModelMasterSection fMasterSection;

		public SelectNodeDoubleClickListener(ModelMasterSection masterSection){
			fMasterSection = masterSection;
		}
		
		@Override
		public void doubleClick(DoubleClickEvent event) {
			if(event.getSelection() instanceof IStructuredSelection){
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				if(selection.getFirstElement() instanceof GenericNode){
					fMasterSection.selectElement(selection.getFirstElement());
				}
			}
		}
	}
	
	public BasicSection(ISectionContext sectionContext, IModelUpdateContext updateContext, int style){
		super(sectionContext.getSectionComposite(), sectionContext.getToolkit(), style);
		fSectionContext = sectionContext;
		fUpdateContext = updateContext;
		createContent();
	}

	@Override
	public void refresh(){
		if(fTextClient != null){
			updateTextClient();
		}
	}

	protected FormToolkit getToolkit(){
		return fSectionContext.getToolkit();
	}

	public void setText(String title){
		getSection().setText(title);
	}

	protected Composite getClientComposite(){
		return fClientComposite;
	}
	
	protected void createContent(){
		getSection().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fTextClient = createTextClient();
		fClientComposite = createClientComposite();
	}
	
	protected Composite createClientComposite() {
		Composite client = getToolkit().createComposite(getSection());
		client.setLayout(clientLayout());
		if(clientLayoutData() != null){
			client.setLayoutData(clientLayoutData());
		}
		getSection().setClient(client);
		getToolkit().adapt(client);
		getToolkit().paintBordersFor(client);
		return client;
	}

	protected Control createTextClient() {
		return null;
	}

	protected Layout clientLayout() {
		GridLayout layout = new GridLayout(1, false);
		return layout;
	}

	protected Object clientLayoutData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}

	protected void updateTextClient() {
	}
	
	protected Shell getActiveShell(){
		return Display.getCurrent().getActiveShell();
	}
	
	protected void modelUpdated(){
		fUpdateContext.getUpdateListener().modelUpdated(this);
	}
	
//	protected void setModelUpdateListener(IModelUpdateListener listener){
//		fModelUpdateListener = listener;
//	}
//	
//	protected void setOperationManager(ModelOperationManager operationManager){
//		fOperationManager = operationManager;
//	}
//	
	public Action getAction(String actionId) {
		if(fActionProvider != null){
			return fActionProvider.getAction(actionId);
		}
		return null;
	}
	
	protected void setActionProvider(IActionProvider provider){
		fActionProvider = provider;
	}
	
	protected IActionProvider getActionProvider(){
		return fActionProvider;
	}
	
//	protected IModelUpdateContext getUpdateContext(){
//		return this;
//	}
//	
	@Override
	public IUndoContext getUndoContext(){
		return fUpdateContext.getUndoContext();
	}
	
	@Override
	public ModelOperationManager getOperationManager(){
		return fUpdateContext.getOperationManager();
	}
	
	@Override 
	public IModelUpdateListener getUpdateListener(){
		return fUpdateContext.getUpdateListener();
	}
	
	@Override
	public AbstractFormPart getSourceForm(){
		return this;
	}
}
