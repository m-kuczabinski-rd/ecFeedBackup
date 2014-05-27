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
import org.eclipse.jface.dialogs.MessageDialog;
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
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.TestMethodRenameDialog;
import com.testify.ecfeed.utils.ModelUtils;

public class MethodDetailsPage extends BasicDetailsPage {

	private MethodNode fSelectedMethod;
	private ParametersViewer fParemetersSection;
	private ConstraintsListViewer fConstraintsSection;
	private TestCasesViewer fTestCasesSection;
	private Text fMethodNameText;
	
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
				fSelectedMethod.replaceCategoryOfSameType(index, newCategory);
			}
		}
	}
	
	public MethodDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
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
		Button reassignButton = getToolkit().createButton(composite, "Reassign", SWT.NONE);
		reassignButton.addSelectionListener(new ReassignAdapter());
		Button testOnlineButton = getToolkit().createButton(composite, "Test online", SWT.NONE);
		testOnlineButton.addSelectionListener(new ExecuteOnlineTestAdapter(this));

		getToolkit().paintBordersFor(composite);
	}

	private void changeName() {
		String name = fMethodNameText.getText();
		if ((name != null) && (!fSelectedMethod.getName().equals(name))) {
			if (fSelectedMethod.getClassNode().getMethod(name, fSelectedMethod.getCategoriesTypes()) == null){
				fSelectedMethod.setName(name);
				modelUpdated(null);
			} else {
				MessageDialog.openInformation(getActiveShell(),
					Messages.DIALOG_METHOD_EXISTS_TITLE,
					Messages.DIALOG_METHOD_EXISTS_MESSAGE);
			}
		}
	}

	@Override
	public void refresh(){
		if(getSelectedElement() instanceof MethodNode){
			fSelectedMethod = (MethodNode)getSelectedElement();
		}
		if(fSelectedMethod != null){
			String title = fSelectedMethod.getName();
			if (ModelUtils.isMethodImplemented(fSelectedMethod)) {
				title += " [implemented]";
			}
			getMainSection().setText(title);
			fParemetersSection.setInput(fSelectedMethod);
			fConstraintsSection.setInput(fSelectedMethod);
			fTestCasesSection.setInput(fSelectedMethod);
			fMethodNameText.setText(fSelectedMethod.getName());
		}
	}
}
