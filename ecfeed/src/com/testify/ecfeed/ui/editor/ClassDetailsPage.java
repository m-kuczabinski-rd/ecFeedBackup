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

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.ui.modelif.ClassInterface;

public class ClassDetailsPage extends BasicDetailsPage {

	private ClassNode fSelectedClass;
	private MethodsViewer fMethodsSection;
	private OtherMethodsViewer fOtherMethodsSection;
	private Text fClassNameText;
	private ModelOperationManager fOperationManager;
	private ClassInterface fClassIf;
	private Text fPackageNameText;
	
	private class ReassignClassSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			IType selectedClass = selectClass();

			if(selectedClass != null){
				String qualifiedName = selectedClass.getFullyQualifiedName();
				if(fSelectedClass.getRoot().getClassModel(qualifiedName) == null){
					fSelectedClass.setName(qualifiedName);
					modelUpdated(null);
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
	
	public ClassDetailsPage(ModelMasterSection masterSection, ModelOperationManager operationManager) {
		super(masterSection);
		fOperationManager = operationManager;
		fClassIf = new ClassInterface(fOperationManager);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		Composite textClientComposite = getToolkit().createComposite(getMainSection());
		textClientComposite.setLayout(new RowLayout());
		
		getMainSection().setTextClient(textClientComposite);

		createQualifiedNameComposite(getMainComposite());
		addForm(fMethodsSection = new MethodsViewer(this, getToolkit()));
		addForm(fOtherMethodsSection = new OtherMethodsViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}

	
	
	private void createQualifiedNameComposite(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Composite textComposite = getToolkit().createComposite(composite);
		
		textComposite.setLayout(new GridLayout(2, false));
		textComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Composite buttonsComposite = getToolkit().createComposite(composite);
		buttonsComposite.setLayout(new GridLayout(1, false));
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		createPackageNameText(textComposite);
		createClassNameText(textComposite);

		createApplyButton(buttonsComposite);
		createBrowseButton(buttonsComposite);

		getToolkit().paintBordersFor(textComposite);
		getToolkit().paintBordersFor(buttonsComposite);
	}

	private void createClassNameText(Composite textComposite) {
		getToolkit().createLabel(textComposite, "Class name");
		fClassNameText = getToolkit().createText(textComposite, null, SWT.NONE);
		fClassNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fClassNameText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					fClassIf.setLocalName(fClassNameText.getText(), null, ClassDetailsPage.this);
					fClassNameText.setText(fClassIf.getLocalName());
				}
			}
		});
	}

	private void createPackageNameText(Composite textComposite) {
		getToolkit().createLabel(textComposite, "Package name");
		fPackageNameText = getToolkit().createText(textComposite, null, SWT.NONE);
		fPackageNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPackageNameText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					fClassIf.setPackageName(fPackageNameText.getText(), null, ClassDetailsPage.this);
					fPackageNameText.setText(fClassIf.getPackageName());
				}
			}
		});
	}

	private void createBrowseButton(Composite buttonsComposite) {
		Button browseButton = getToolkit().createButton(buttonsComposite, "Browse...", SWT.NONE);
		browseButton.addSelectionListener(new ReassignClassSelectionAdapter());
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	private void createApplyButton(Composite buttonsComposite) {
		Button applyButton = getToolkit().createButton(buttonsComposite, "Apply", SWT.NONE);
		applyButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		applyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				fClassIf.setPackageName(fPackageNameText.getText(), null, ClassDetailsPage.this);
				fClassIf.setLocalName(fClassNameText.getText(), null, ClassDetailsPage.this);
				fClassNameText.setText(fClassIf.getLocalName());
				fPackageNameText.setText(fClassIf.getPackageName());
			}
		});
	}

	@Override
	public void refresh(){
		if(getSelectedElement() instanceof ClassNode){
			fClassIf.setTarget((ClassNode)getSelectedElement());
			String title = fClassIf.getQualifiedName() + " [" + fClassIf.implementationStatus() + "]";
			getMainSection().setText(title);
			fClassNameText.setText(fClassIf.getLocalName());
			fPackageNameText.setText(fClassIf.getPackageName());

			fMethodsSection.setInput(fClassIf.getTarget());
			fOtherMethodsSection.setInput(fClassIf.getTarget());
			fOtherMethodsSection.setVisible(fOtherMethodsSection.getItemsCount() > 0);

			getMainSection().layout();
		}
	}
	
}
