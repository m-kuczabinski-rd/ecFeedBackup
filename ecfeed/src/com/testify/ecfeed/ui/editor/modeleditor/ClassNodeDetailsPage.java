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

import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.ui.common.ModelUtils;

public class ClassNodeDetailsPage extends GenericNodeDetailsPage{
	
	private ClassNode fSelectedClass;
	private Section fMainSection;
	private Label fQualifiedNameLabel;
	private ColorManager fColorManager;
	private CheckboxTableViewer fOtherMethodsViewer;
	private CheckboxTableViewer fMethodsViewer;
	private Section fOtherMethodsSection;
	private Composite fMainComposite;
	private boolean fOtherMethodsSectionCreated;
	
	private class ChangeNameButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			IType selectedClass = selectClass();

			if(selectedClass != null){
				String qualifiedName = selectedClass.getFullyQualifiedName();
				if(fSelectedClass.getRoot().getClassModel(qualifiedName) == null){
					fSelectedClass.setName(qualifiedName);
					updateModel((RootNode)fSelectedClass.getRoot());
				}
				else{
					MessageDialog.openInformation(getActiveShell(), 
							Messages.DIALOG_CLASS_EXISTS_TITLE, 
							Messages.DIALOG_CLASS_EXISTS_MESSAGE);
				}
			}
		}

		private IType selectClass() {
			TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getDefault().getActiveShell());
			
			if (dialog.open() == IDialogConstants.OK_ID) {
				return (IType)dialog.getFirstResult();
			}
			return null;
		}
	}

	private class RemoveMethodsButtonSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(MessageDialog.openConfirm(getActiveShell(), 
					Messages.DIALOG_REMOVE_METHODS_TITLE, 
					Messages.DIALOG_REMOVE_METHODS_MESSAGE)){
				removeMethods(fMethodsViewer.getCheckedElements());
				updateModel((RootNode)fSelectedClass.getRoot());
			}
		}

		private void removeMethods(Object[] checkedElements) {
			for(Object method : checkedElements){
				fSelectedClass.removeMethod((MethodNode)method);
			}
		}
	}
	
	/**
	 * Create the details page.
	 */
	public ClassNodeDetailsPage(ModelMasterDetailsBlock parentBlock) {
		super(parentBlock);
		fColorManager = new ColorManager();
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		
		fMainSection = getToolkit().createSection(parent, Section.TITLE_BAR);
		getToolkit().paintBordersFor(fMainSection);

		createTextClient(fMainSection);
		
		fMainComposite = new Composite(fMainSection, SWT.NONE);
		getToolkit().adapt(fMainComposite);
		getToolkit().paintBordersFor(fMainComposite);
		fMainSection.setClient(fMainComposite);
		fMainComposite.setLayout(new GridLayout(1, false));
		
		createQualifiedNameComposite(fMainComposite);
		createMethodsSection(fMainComposite);
		
	}

	private void createTextClient(Section section) {
		Composite textComposite = new Composite(section, SWT.NONE);
		getToolkit().adapt(textComposite);
		getToolkit().paintBordersFor(textComposite);
		section.setTextClient(textComposite);
		textComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		createButton(textComposite, "Refresh", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				refresh();
			}
		});
	}

	private void createQualifiedNameComposite(Composite mainComposite) {
		Composite qualifiedNameComposite = new Composite(mainComposite, SWT.NONE);
		qualifiedNameComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		getToolkit().adapt(qualifiedNameComposite);
		getToolkit().paintBordersFor(qualifiedNameComposite);
		qualifiedNameComposite.setLayout(new GridLayout(2, false));
		
		fQualifiedNameLabel = new Label(qualifiedNameComposite, SWT.NONE);
		fQualifiedNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getToolkit().adapt(fQualifiedNameLabel, true, true);
		fQualifiedNameLabel.setText("Qualified name: ");
		
		Button changeButton = new Button(qualifiedNameComposite, SWT.NONE);
		changeButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		getToolkit().adapt(changeButton, true, true);
		changeButton.setText("Change");
		changeButton.addSelectionListener(new ChangeNameButtonSelectionAdapter());
	}

	private void createMethodsSection(Composite composite) {
		Section methodsSection = getToolkit().createSection(composite, Section.TITLE_BAR);
		methodsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		getToolkit().paintBordersFor(methodsSection);
		methodsSection.setText("Methods");
		methodsSection.setExpanded(true);
		
		Composite methodsComposite = getToolkit().createComposite(methodsSection, SWT.NONE);
		getToolkit().paintBordersFor(methodsComposite);
		methodsSection.setClient(methodsComposite);
		methodsComposite.setLayout(new GridLayout(1, false));
		
		fMethodsViewer = CheckboxTableViewer.newCheckList(methodsComposite, SWT.BORDER | SWT.FULL_SELECTION);
		fMethodsViewer.setContentProvider(new ArrayContentProvider());
		fMethodsViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		
		fMethodsViewer.getTable().setLayoutData(VIEWERS_GRID_DATA);
		
		createTableViewerColumn(fMethodsViewer, "Method", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				MethodNode method = (MethodNode)element;
				String result = method.toString();
				if(methodObsolete(method)){
					result += " [obsolete]";
				}
				return result;
			}
	
			@Override
			public Color getForeground(Object element){
				MethodNode method = (MethodNode)element;
				if(methodObsolete(method)){
					return fColorManager.getColor(ColorConstants.OBSOLETE_METHOD);
				}
				return null;
			}
		});
		
		Button removeSelectedButton = new Button(methodsComposite, SWT.NONE);
		getToolkit().adapt(removeSelectedButton, true, true);
		removeSelectedButton.setText("Remove selected");
		removeSelectedButton.addSelectionListener(new RemoveMethodsButtonSelectionAdapter());
	}

	private void createOtherMethodsSection(Composite composite) {
		fOtherMethodsSection = getToolkit().createSection(composite, Section.TITLE_BAR);
		fOtherMethodsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		getToolkit().paintBordersFor(fOtherMethodsSection);
		fOtherMethodsSection.setText("Other methods");
		fOtherMethodsSection.setExpanded(true);
		
		Composite otherMathodsComposite = new Composite(fOtherMethodsSection, SWT.NONE);
		getToolkit().adapt(otherMathodsComposite);
		getToolkit().paintBordersFor(otherMathodsComposite);
		fOtherMethodsSection.setClient(otherMathodsComposite);
		otherMathodsComposite.setLayout(new GridLayout(1, false));
		
		fOtherMethodsViewer = CheckboxTableViewer.newCheckList(otherMathodsComposite, SWT.BORDER | SWT.FULL_SELECTION);
		fOtherMethodsViewer.setContentProvider(new ArrayContentProvider());
		fOtherMethodsViewer.getTable().setLayoutData(VIEWERS_GRID_DATA);
		
		Button addSelectedButton = new Button(otherMathodsComposite, SWT.NONE);
		getToolkit().adapt(addSelectedButton, true, true);
		addSelectedButton.setText("Add selected");
		addSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				for(Object method : fOtherMethodsViewer.getCheckedElements()){
					fSelectedClass.addMethod((MethodNode)method);
					updateModel((RootNode)fSelectedClass.getRoot());
				}
			}
		});
		
		fOtherMethodsSectionCreated = true;
	}

	@Override
	public void dispose(){
		fColorManager.dispose();
		super.dispose();
	}

	private boolean methodObsolete(MethodNode method) {
		List<MethodNode> obsoleteMethods = getObsoleteMethods();
		for(MethodNode obsoleteMethod : obsoleteMethods){
			if(obsoleteMethod.toString().equals(method.toString())){
				return true;
			}
		}
		return false;
	}
	
	private List<MethodNode> getObsoleteMethods(){
		return ModelUtils.getObsoleteMethods(fSelectedClass, fSelectedClass.getQualifiedName());
	}
	
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fSelectedClass = (ClassNode)fSelectedNode;
		refresh();
	}
	
	public void refresh(){
		if(fSelectedClass == null){
			return;
		}
		List<MethodNode> notContainedMethods = ModelUtils.getNotContainedMethods(fSelectedClass, fSelectedClass.getQualifiedName());
		if(notContainedMethods.size() == 0){
			if(fOtherMethodsSectionCreated){
				fOtherMethodsSection.dispose();
				fOtherMethodsSectionCreated = false;
			}
		}
		else {
			if(notContainedMethods.size() > 0){
				if(!fOtherMethodsSectionCreated){
					createOtherMethodsSection(fMainComposite);
				}
				fOtherMethodsSection.setText("Other test methods in " + fSelectedClass.getLocalName());
				fOtherMethodsViewer.setInput(notContainedMethods);
			}
		}
		fMainSection.setText("Class " + fSelectedClass.getLocalName());
		fQualifiedNameLabel.setText("Qualified name: " + fSelectedClass.getName());
		fMethodsViewer.setInput(fSelectedClass.getMethods());
	}
}
