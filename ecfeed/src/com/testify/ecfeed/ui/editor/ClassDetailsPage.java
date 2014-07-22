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

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.utils.ModelUtils;
import com.testify.ecfeed.implementor.ModelImplementor;

public class ClassDetailsPage extends BasicDetailsPage {

	private ClassNode fSelectedClass;
	private MethodsViewer fMethodsSection;
	private OtherMethodsViewer fOtherMethodsSection;
	private Text fClassNameText;
	private Button fImplementButton;
	
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
	
	public ClassDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
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
		composite.setLayout(new GridLayout(5, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Class path");
		fClassNameText = getToolkit().createText(composite, null, SWT.NONE);
		fClassNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fClassNameText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					changeName();
				}
			}
		});
		Button changeButton = getToolkit().createButton(composite, "Change", SWT.NONE);
		changeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				changeName();
			}
		});

		Button button = getToolkit().createButton(composite, "Reassign", SWT.NONE);
		button.addSelectionListener(new ReassignClassSelectionAdapter());

		fImplementButton = getToolkit().createButton(composite, "Implement", SWT.NONE);
		fImplementButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				ModelImplementor implementor = new ModelImplementor();
				implementor.implement(fSelectedClass);
			}
		});

		getToolkit().paintBordersFor(composite);
	}

	private void changeName() {
		String name = fClassNameText.getText();
		boolean validName = ModelUtils.isClassQualifiedNameValid(name);

		if (!validName) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_CLASS_NAME_PROBLEM_TITLE,
					Messages.DIALOG_CLASS_NAME_PROBLEM_MESSAGE);
			fClassNameText.setText(fSelectedClass.getQualifiedName());
			fClassNameText.setSelection(fSelectedClass.getQualifiedName().length());
		}

		if (validName && (!fSelectedClass.getName().equals(name))) {
			if (fSelectedClass.getRoot().getClassModel(name) == null) {
				fSelectedClass.setName(name);
				modelUpdated(null);
			} else {
				MessageDialog.openInformation(getActiveShell(),
					Messages.DIALOG_CLASS_EXISTS_TITLE,
					Messages.DIALOG_CLASS_EXISTS_MESSAGE);
			}
		}
	}

	@Override
	public void refresh(){
		if(getSelectedElement() instanceof ClassNode){
			fSelectedClass = (ClassNode)getSelectedElement();
		}
		if(fSelectedClass != null){
			String title = fSelectedClass.getLocalName();
			if (ModelUtils.isClassImplemented(fSelectedClass)) {
				title += " [implemented]";
			}
			getMainSection().setText(title);
			fClassNameText.setText(fSelectedClass.getQualifiedName());
			fMethodsSection.setInput(fSelectedClass);
			fOtherMethodsSection.setInput(fSelectedClass);
			getMainSection().layout();
			fImplementButton.setEnabled(!ModelUtils.isClassImplemented(fSelectedClass) || ModelUtils.isClassPartiallyImplemented(fSelectedClass));
		}
	}

}
