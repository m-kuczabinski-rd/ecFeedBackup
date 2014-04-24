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

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wb.swt.TableViewerColumnSorter;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.dialogs.RenameModelDialog;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.utils.ModelUtils;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.layout.GridData;

public class ObsoleteRootNodeDetailsPage extends ObsoleteGenericNodeDetailsPage{

	private RootNode fSelectedRoot;
	private CheckboxTableViewer fClassesViewer;
	private Section fMainSection;

	private class AddTestClassButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			IType selectedClass = selectClass();

			if(selectedClass != null){
				ClassNode classNode = ModelUtils.generateClassModel(selectedClass);
				if(fSelectedRoot.getClassModel(classNode.getQualifiedName()) == null){
					fSelectedRoot.addClass(classNode);
					updateModel(fSelectedRoot);
				}
				else{
					MessageDialog.openError(getActiveShell(), 
							Messages.DIALOG_CLASS_EXISTS_TITLE,
							Messages.DIALOG_CLASS_EXISTS_MESSAGE);
				}
			}
		}

		private IType selectClass() {
			TestClassSelectionDialog dialog = new TestClassSelectionDialog(getActiveShell());
			
			if (dialog.open() == IDialogConstants.OK_ID) {
				return (IType)dialog.getFirstResult();
			}
			return null;
		}
	}

	private class RemoveClassesButtonSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(MessageDialog.openConfirm(getActiveShell(), 
					Messages.DIALOG_REMOVE_CLASSES_TITLE, 
					Messages.DIALOG_REMOVE_CLASSES_MESSAGE)){
				removeClasses(fClassesViewer.getCheckedElements());
			}
		}

		private void removeClasses(Object[] checkedElements) {
			for(Object element : checkedElements){
				if(element instanceof ClassNode){
					fSelectedRoot.removeClass((ClassNode)element);
				}
			}
			updateModel(fSelectedRoot);
		}
	}


	public ObsoleteRootNodeDetailsPage(ObsoleteModelMasterDetailsBlock parentBlock){
		super(parentBlock);
	}
	
	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = fToolkit.createSection(parent, Section.TITLE_BAR);

		Composite mainComposite = fToolkit.createComposite(fMainSection, SWT.NONE);
		fToolkit.paintBordersFor(mainComposite);
		fMainSection.setClient(mainComposite);
		mainComposite.setLayout(new GridLayout(1, true));
		
		createClassListViewer(mainComposite);
		createBottomButtons(mainComposite);
		createTextClientComposite(fMainSection);
	}

	private void createClassListViewer(Composite composite) {
		Label classesLabel = new Label(composite, SWT.BOLD);
		classesLabel.setText("Test classes");

		fClassesViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION |SWT.FILL);
		fClassesViewer.setContentProvider(new ArrayContentProvider());
		fClassesViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		
		fClassesViewer.getTable().setHeaderVisible(true);
		fClassesViewer.getTable().setLinesVisible(true);
		fClassesViewer.getTable().setLayoutData(VIEWERS_GRID_DATA);

		TableViewerColumn classViewerColumn = 
				createTableViewerColumn(fClassesViewer, "Class", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ClassNode)element).getLocalName();
			}
		});
		new TableViewerColumnSorter(classViewerColumn) {
			protected Object getValue(Object o) {
				return ((ClassNode)o).getLocalName();
			}
		};

		TableViewerColumn qualifiedNameViewerColumn = 
				createTableViewerColumn(fClassesViewer, "Qualified name", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ClassNode)element).getQualifiedName();
			}
		});
		new TableViewerColumnSorter(qualifiedNameViewerColumn) {
			protected Object getValue(Object o) {
				return ((ClassNode)o).getLocalName();
			}
		};
	}

	private void createBottomButtons(Composite composite) {
		Composite bottomButtonsComposite = fToolkit.createComposite(composite, SWT.FILL);
		bottomButtonsComposite.setLayout(new GridLayout(2, false));
		bottomButtonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		
		createButton(bottomButtonsComposite, "Add Test Class...", new AddTestClassButtonSelectionAdapter());
		createButton(bottomButtonsComposite, "Remove selected classes", new RemoveClassesButtonSelectionAdapter());
	}

	private void createTextClientComposite(Section parentSection) {
		Composite textClient = new Composite(parentSection, SWT.NONE);
		textClient.setLayout(new FillLayout());
		createButton(textClient, "Rename...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				RenameModelDialog dialog = new RenameModelDialog(Display.getDefault().getActiveShell(), fSelectedRoot);
				if(dialog.open() == IDialogConstants.OK_ID){
					fSelectedRoot.setName(dialog.getNewName());
					updateModel(fSelectedRoot);
				}
			}
		});
		parentSection.setTextClient(textClient);
	}

	@Override
	public void refresh() {
		if(fSelectedRoot == null){
			return;
		}
		fClassesViewer.setInput(fSelectedRoot.getClasses());
		fMainSection.setText(fSelectedRoot.toString());
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fSelectedRoot = (RootNode)fSelectedNode;
		refresh();
	}

	@Override
	public void modelUpdated() {
		refresh();
	}

}
