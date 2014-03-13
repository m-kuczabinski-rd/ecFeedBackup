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

package com.testify.ecfeed.ui.editor.modeleditor.obsolete;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.editor.ModelEditor;

import org.eclipse.swt.layout.GridData;

public class ObsoleteModelMasterDetailsBlock extends MasterDetailsBlock implements ObsoleteIModelUpdateListener{

	private FormToolkit fToolkit;
	private RootNode fModel;
	private ModelEditor fEditor;
	private SectionPart fMasterSectionPart;
	private Section fMasterSection;
	private TreeViewer fTreeViewer;
	private Composite fMainComposite;
	private Button fMoveUpButton;
	private Button fMoveDownButton;
	private IGenericNode fSelectedNode;
	
	/**
	 * Create the master details block.
	 */
	public ObsoleteModelMasterDetailsBlock(ModelEditor editor, RootNode model) {
		fEditor = editor;
		fModel = model;
		fEditor.registerModelUpdateListener(this);
	}

	/**
	 * Create contents of the master details block.
	 * @param managedForm
	 * @param parent
	 */
	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		fToolkit = managedForm.getToolkit();

		fMasterSection = fToolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		fMasterSection.setText("Structure");
		fMasterSectionPart = new SectionPart(fMasterSection, fToolkit, 0);
		
		GridData masterSectionGridData = new GridData(SWT.RIGHT, SWT.FILL, false, true);
		masterSectionGridData.widthHint = 250;
		fMasterSection.setLayoutData(masterSectionGridData);
		
		createTreeEditorBlock();
	}

	/**
	 * Register the pages.
	 * @param part
	 */
	@Override
	protected void registerPages(DetailsPart part) {
		part.registerPage(RootNode.class, new ObsoleteRootNodeDetailsPage(this));
		part.registerPage(ClassNode.class, new ObsoleteClassNodeDetailsPage(this));
		part.registerPage(MethodNode.class, new ObsoleteMethodNodeDetailsPage(this));
		part.registerPage(CategoryNode.class, new ObsoleteCategoryNodeDetailsPage(this));
		part.registerPage(ExpectedValueCategoryNode.class, new ObsoleteExpectedValueDetailsPage(this));
		part.registerPage(TestCaseNode.class, new ObsoleteTestCaseNodeDetailsPage(this));
		part.registerPage(ConstraintNode.class, new ObsoleteConstraintsNodeDetailsPage(this));
		part.registerPage(PartitionNode.class, new ObsoletePartitionNodeDetailsPage(this));
	
		selectNode(fModel);
	}

	/**
	 * Create the toolbar actions.
	 * @param managedForm
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	private void createTreeEditorBlock() {
		fMainComposite = new Composite(fMasterSection, SWT.RIGHT);
		fMainComposite.setLayout(new GridLayout(1, false));
		fToolkit.adapt(fMainComposite);
		fToolkit.paintBordersFor(fMainComposite);
		fMasterSection.setClient(fMainComposite);
		
		createTreeViewer(fMainComposite);
		createSortButtons(fMainComposite);
	}

	private void createSortButtons(Composite composite) {
		Composite sortButtonsComposite = fToolkit.createComposite(composite);
		sortButtonsComposite.setLayout(new FillLayout());
		fMoveUpButton = fToolkit.createButton(sortButtonsComposite, "Move Up", SWT.NONE);
		fMoveDownButton = fToolkit.createButton(sortButtonsComposite, "Move Down", SWT.NONE);
		fMoveUpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				moveSelectedItem(true);
//				fEditor.updateModel();
			}
		});
		fMoveDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				moveSelectedItem(false);
//				fEditor.updateModel();
			}
		});
		
	}

	private void moveSelectedItem(boolean moveUp) {
		IGenericNode parent = fSelectedNode.getParent(); 
		if(parent != null){
			parent.moveChild(fSelectedNode, moveUp);
		}
	}

	private void createTreeViewer(Composite composite) {
		FilteredTree filteredTree = new FilteredTree(composite, SWT.BORDER, new PatternFilter(), true);
		fTreeViewer = filteredTree.getViewer();
		fTreeViewer.setAutoExpandLevel(2);
		fTreeViewer.setContentProvider(new ObsoleteModelContentProvider());
		fTreeViewer.setLabelProvider(new ObsoleteModelLabelProvider());

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 100;
		gd.heightHint = 100;
		fTreeViewer.getTree().setLayoutData(gd);
		
		fTreeViewer.setInput(fEditor);

		fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
				fSelectedNode = (IGenericNode)selectedElement;
				if((selectedElement instanceof RootNode) || (selectedElement instanceof CategoryNode))
				{
					fMoveUpButton.setEnabled(false);
					fMoveDownButton.setEnabled(false);
				}
				else{
					fMoveUpButton.setEnabled(true);
					fMoveDownButton.setEnabled(true);
				}
				detailsPart.selectionChanged(fMasterSectionPart, event.getSelection());
			}
		});
	}

	@Override
	public void modelUpdated() {
		fMasterSection.setText(fSelectedNode.getRoot().getName());
		fTreeViewer.refresh();
		fMasterSection.layout();
	}
	
	void selectNode(IGenericNode node){
		fTreeViewer.setSelection(new StructuredSelection(node), true);
		fSelectedNode = node;
	}

	public ModelEditor getEditor() {
		return fEditor;
	}
	
	public IGenericNode getSelectedNode(){
		return fSelectedNode;
	}
}
