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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.adapter.IModelImplementer;
import com.testify.ecfeed.adapter.ModelOperationManager;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.common.EclipseModelImplementer;
import com.testify.ecfeed.ui.common.IProjectNameProvider;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;

public abstract class BasicDetailsPage implements IDetailsPage, IModelUpdateListener, ISectionContext, IModelUpdateContext, IProjectNameProvider{

	private Section fMainSection;
	private Composite fMainComposite;
	private IManagedForm fManagedForm;

	private List<IFormPart> fForms;
	private List<ViewerSection> fViewerSections;
	private ModelMasterSection fMasterSection;
	private IModelUpdateContext fModelUpdateContext;
	private GenericNode fSelectedNode;
	private IModelImplementer fImplementer;
	private Button fImplementButton;
	
	private static final int MAIN_SECTION_STYLE = Section.EXPANDED | Section.TITLE_BAR;

	public BasicDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext){
		fMasterSection = masterSection;
		fForms = new ArrayList<IFormPart>();
		fViewerSections = new ArrayList<ViewerSection>();
		fModelUpdateContext = updateContext;
		fImplementer = new EclipseModelImplementer(this);
	}
	
	@Override
	public void initialize(IManagedForm form) {
		fManagedForm = form;
	}
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		fSelectedNode = (GenericNode)fMasterSection.getSelectedElement();
	}

	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = getToolkit().createSection(parent, MAIN_SECTION_STYLE);
		Composite textClient = createTextClientComposite();
		fMainSection.setTextClient(textClient);

		getToolkit().adapt(getMainSection());
		
		fMainComposite = getToolkit().createComposite(getMainSection(), SWT.NONE);
		fMainComposite.setLayout(new GridLayout(1, false));
		getToolkit().adapt(fMainComposite);
		getMainSection().setClient(fMainComposite);
	}
	
	protected Composite createTextClientComposite() {
		Composite client = getToolkit().createComposite(fMainSection);
		client.setLayout(new FillLayout());
		return client;
	}

	protected Button createImplementerButton(Composite parent){
		fImplementButton = getToolkit().createButton(parent, "Implement", SWT.NONE);
		fImplementButton.addSelectionListener(new AbstractSelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fImplementer.implement(fSelectedNode);
				getMasterSection().refresh();
			}
		});
		return fImplementButton;
	}
	
	@Override
	public void refresh(){
//		fSelectedNode = (GenericNode)fMasterSection.getSelectedElement();
//		for(IFormPart form : fForms){
//			form.refresh();
//		}
		refreshTextClient();
	}

	protected void refreshTextClient(){
		if(fImplementButton != null && fImplementButton.isDisposed() == false){
//			EImplementationStatus status = fImplementer.getImplementationStatus(fSelectedNode);
//			boolean implementable = fImplementer.implementable(fSelectedNode);
//			if(status != EImplementationStatus.IMPLEMENTED && implementable){
//				fImplementButton.setEnabled(true);
//			}
//			else{
//				fImplementButton.setEnabled(false);
//			}
		}
	}

	@Override
	public void dispose() {
		for(IFormPart form : fForms){
			form.dispose();
		}
	}

	@Override
	public boolean isDirty() {
		for(IFormPart form : fForms){
			if(form.isDirty()){
				return true;
			};
		}
		return false;
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		for(IFormPart form : fForms){
			form.commit(onSave);
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean isStale() {
		for(IFormPart form : fForms){
			if(form.isStale()){
				return true;
			};
		}
		return false;
	}

	public FormToolkit getToolkit(){
		return fManagedForm.getToolkit();
	}
	
	protected Section getMainSection(){
		return fMainSection;
	}

	public ModelMasterSection getMasterSection(){
		return fMasterSection;
	}

	protected void addForm(IFormPart form){
		fForms.add(form);
		form.initialize(getManagedForm());
	}
	
	protected void addViewerSection(ViewerSection section){
		addForm(section);
		fViewerSections.add(section);
	}
	
	protected Object getSelectedElement(){
		return getMasterSection().getSelectedElement();
	}
	
	protected IManagedForm getManagedForm(){
		return fManagedForm;
	}

	protected Composite getMainComposite(){
		return fMainComposite;
	}
	
	public void modelUpdated(AbstractFormPart source){
		if(source != null){
			source.markDirty();
		}
		if(getMasterSection() != null){
			getMasterSection().markDirty();
		}
		if(getMasterSection() != null){
			getMasterSection().refresh();
		}
		refresh();
	}
	
	protected Shell getActiveShell(){
		return Display.getCurrent().getActiveShell();
	}
	
	public BasicSection getFocusedViewerSection(){
		for(ViewerSection section : fViewerSections){
			if(section.getViewer().getControl().isFocusControl()){
				return section;
			}
		}
		return null;
	}
	
	public Composite getSectionComposite(){
		return fMainComposite;
	}
	
	public ModelOperationManager getOperationManager(){
		return fModelUpdateContext.getOperationManager();
	}
	
	public AbstractFormPart getSourceForm(){
		return null;
	}

	public List<IModelUpdateListener> getUpdateListeners(){
		return Arrays.asList(new IModelUpdateListener[]{this});
	}
	
	public IUndoContext getUndoContext(){
		return fModelUpdateContext.getUndoContext();
	}
	
	public String getProjectName(){
		return fMasterSection.getProjectName();
	}
}
