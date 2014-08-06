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

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.dialogs.TestMethodRenameDialog;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class MethodDetailsPage extends BasicDetailsPage {

	private MethodNode fSelectedMethod;
	private ParametersViewer fParemetersSection;
	private ConstraintsListViewer fConstraintsSection;
	private TestCasesViewer fTestCasesSection;
	private Text fMethodNameText;
	private Button fTestOnlineButton;
	private Button fReassignButton;
	private MethodInterface fMethodIf;
	
	private class ReassignAdapter extends SelectionAdapter{

		@Override
		public void widgetSelected(SelectionEvent e){
			TestMethodRenameDialog dialog = new TestMethodRenameDialog(getActiveShell(), fSelectedMethod);
			if(dialog.open() == IDialogConstants.OK_ID){
				MethodNode selectedMethod = dialog.getSelectedMethod();
				fSelectedMethod.setName(selectedMethod.getName());
				updateParemeters(selectedMethod);
				modelUpdated(null);
			}
		}

		private void updateParemeters(MethodNode newMethod) {
			List<CategoryNode> srcParameters = newMethod.getCategories();
			for(int i = 0; i < srcParameters.size(); i++){
				updateParameter(i, srcParameters.get(i));
			}
		}
		
		private void updateParameter(int index, CategoryNode newCategory){
			boolean isOriginalCategoryExpected = fSelectedMethod.getCategories().get(index).isExpected();
			boolean isNewCategoryExpected = newCategory.isExpected();
			if(isOriginalCategoryExpected == isNewCategoryExpected){
				fSelectedMethod.getCategories().get(index).setName(newCategory.getName());
			} else{
				fSelectedMethod.replaceCategory(index, newCategory);
			}
		}
	}
	
	public MethodDetailsPage(ModelMasterSection masterSection, ModelOperationManager operationManager) {
		super(masterSection);
		fMethodIf = new MethodInterface(operationManager);
	}

	public MethodNode getSelectedMethod() {
		return fSelectedMethod;
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createTextClient();
		addForm(fParemetersSection = new ParametersViewer(this, getToolkit()));
		addForm(fConstraintsSection = new ConstraintsListViewer(this, getToolkit()));
		addForm(fTestCasesSection = new TestCasesViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}

	private void createTextClient() {
		Composite composite = getToolkit().createComposite(getMainComposite());
		composite.setLayout(new GridLayout(5, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Method name", SWT.NONE);
		fMethodNameText = getToolkit().createText(composite, null, SWT.NONE);
		fMethodNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fMethodNameText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					applyNewName();
				}
			}
		});

		Button changeButton = getToolkit().createButton(composite, "Change", SWT.NONE);
		changeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				applyNewName();
			}
		});
		fReassignButton = getToolkit().createButton(composite, "Reassign", SWT.NONE);
		fReassignButton.addSelectionListener(new ReassignAdapter());
		fTestOnlineButton = getToolkit().createButton(composite, "Test online", SWT.NONE);
		fTestOnlineButton.addSelectionListener(new ExecuteOnlineTestAdapter(this));

		getToolkit().paintBordersFor(composite);
	}
	
	@Override
	public void refresh(){
		if(getSelectedElement() instanceof MethodNode){
			MethodNode selectedMethod = (MethodNode)getSelectedElement(); 
			fMethodIf.setTarget(selectedMethod);
			ImplementationStatus methodStatus = fMethodIf.implementationStatus();
			getMainSection().setText(selectedMethod.toString() + methodStatus);
			fTestOnlineButton.setEnabled(methodStatus == ImplementationStatus.IMPLEMENTED || 
					methodStatus == ImplementationStatus.PARTIALLY_IMPLEMENTED);
			fParemetersSection.setInput(selectedMethod);
			fConstraintsSection.setInput(selectedMethod);
			fTestCasesSection.setInput(selectedMethod);
			fMethodNameText.setText(fMethodIf.getName());
			
			ImplementationStatus parentStatus = fMethodIf.implementationStatus(selectedMethod.getClassNode());
			fReassignButton.setEnabled(parentStatus == ImplementationStatus.IMPLEMENTED || 
					parentStatus == ImplementationStatus.PARTIALLY_IMPLEMENTED);
					
			fSelectedMethod = (MethodNode)getSelectedElement();
		}
	}

	private void applyNewName(){
		fMethodIf.setName(fMethodNameText.getText(), null, this);
		fMethodNameText.setText(fMethodIf.getName());
	}
}
