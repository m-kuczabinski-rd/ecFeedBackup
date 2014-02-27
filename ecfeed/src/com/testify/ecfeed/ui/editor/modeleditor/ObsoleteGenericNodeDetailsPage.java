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

package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.ui.editor.EcMultiPageEditor;

public class ObsoleteGenericNodeDetailsPage implements IDetailsPage, ObsoleteIModelUpdateListener {

	private IManagedForm fManagedForm;
	private EcMultiPageEditor fEditor;
	private ObsoleteModelMasterDetailsBlock fParentBlock;
	protected FormToolkit fToolkit;
	protected IGenericNode fSelectedNode;
	protected final GridData VIEWERS_GRID_DATA;

	protected class ChildrenViewerDoubleClickListener implements
	IDoubleClickListener {
		@Override
		public void doubleClick(DoubleClickEvent event) {
			if(event.getSource() instanceof StructuredViewer){
				StructuredViewer sourceViewer = (StructuredViewer)event.getSource();
				IStructuredSelection selection = (IStructuredSelection) sourceViewer.getSelection();
				Object selectedElement = selection.getFirstElement();
				if(selectedElement instanceof IGenericNode){
					fParentBlock.selectNode((IGenericNode)selection.getFirstElement());
				}
			}
		}
	}
	
	/**
	 * Create the details page.
	 */
	public ObsoleteGenericNodeDetailsPage(ObsoleteModelMasterDetailsBlock parentBlock) {
		fParentBlock = parentBlock;
		fEditor = parentBlock.getEditor();;
		if(fEditor != null){
			fEditor.registerModelUpdateListener(this);
		}
		VIEWERS_GRID_DATA = new GridData(SWT.FILL, SWT.FILL, true, true);
		VIEWERS_GRID_DATA.heightHint = 100;
		VIEWERS_GRID_DATA.widthHint = 100;
	}

	/**
	 * Initialize the details page.
	 * @param form
	 */
	public void initialize(IManagedForm form) {
		fManagedForm = form;
		fToolkit = fManagedForm.getToolkit();
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent){
	}
	
	protected Section createMainSection(Composite parent){
		parent.setLayout(new FillLayout());
		Section section = fToolkit.createSection(parent,
				Section.TITLE_BAR);
		return section;

	}
	
	protected Shell getActiveShell(){
		return Display.getDefault().getActiveShell();
	}
	
	protected Composite createMainComposite(Section section){
		Composite composite = fToolkit.createComposite(section, SWT.NONE);
		fToolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(1, true));
		return composite;
	}

	public void dispose() {
	}

	public void setFocus() {
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection){
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if(structuredSelection.getFirstElement() instanceof IGenericNode){
			fSelectedNode = (IGenericNode)structuredSelection.getFirstElement();
		}
	}

	public void commit(boolean onSave) {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
	}

	@Override
	public void modelUpdated() {
		if(getParentBlock().getSelectedNode() == fSelectedNode){
			refresh();
		}
	}

	public IManagedForm getManagedForm() {
		return fManagedForm;
	}
	
	public EcMultiPageEditor getEditor(){
		return fEditor;
	}
	
	public ObsoleteModelMasterDetailsBlock getParentBlock(){
		return fParentBlock;
	}
	
	public FormToolkit getToolkit(){
		return fToolkit;
	}
	
	protected void updateModel(IGenericNode node){
//		getEditor().updateModel();
	}
	
	protected TableViewerColumn createTableViewerColumn(TableViewer viewer, 
			String name, int width, ColumnLabelProvider labelProvider){
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setWidth(width);
		column.setText(name);
		column.setResizable(true);
		column.setMoveable(true);
		viewerColumn.setLabelProvider(labelProvider);
		return viewerColumn;
	}
	
	protected Button createButton(Composite parent, String label, SelectionAdapter adapter){
		Button button = new Button(parent, SWT.NONE);
		fToolkit.adapt(button, true, true);
		button.setText(label);
		button.addSelectionListener(adapter);
		
		return button;
	}
}
