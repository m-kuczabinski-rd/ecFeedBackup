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

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.TestCaseInterface;

public class TestCaseDetailsPage extends BasicDetailsPage {

	private Combo fTestSuiteNameCombo;
	private TestDataViewer fTestDataSection;
	private ModelOperationManager fOperationManager;
	private TestCaseInterface fTestCaseIf;
	
	private class RenameTestCaseAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			applyNewTestCaseName();
		}
	}
	
	private class RenameTestCaseComboListener extends RenameTestCaseAdapter implements Listener{
		@Override
		public void handleEvent(Event event) {
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				applyNewTestCaseName();
			}
		}
	}
	
	public TestCaseDetailsPage(ModelMasterSection masterSection, ModelOperationManager operationManager) {
		super(masterSection);
		fOperationManager = operationManager;
		fTestCaseIf = new TestCaseInterface(operationManager);
	}

	private void applyNewTestCaseName() {
		fTestCaseIf.setName(fTestSuiteNameCombo.getText(), null, TestCaseDetailsPage.this);
		fTestSuiteNameCombo.setText(fTestCaseIf.getName());
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
		createTestSuiteEdit(getMainComposite());
		addForm(fTestDataSection = new TestDataViewer(this, getToolkit(), fOperationManager));
	}
	
	private void createTestSuiteEdit(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Test suite: ");
		
		ComboViewer comboViewer = new ComboViewer(composite, SWT.NONE);
		fTestSuiteNameCombo = comboViewer.getCombo();
		fTestSuiteNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getToolkit().paintBordersFor(fTestSuiteNameCombo);
		fTestSuiteNameCombo.addListener(SWT.KeyDown, new RenameTestCaseComboListener());
		fTestSuiteNameCombo.addSelectionListener(new RenameTestCaseAdapter());

		Button button = getToolkit().createButton(composite, "Change", SWT.NONE);
		button.addSelectionListener(new RenameTestCaseAdapter());
	}

	@Override
	public void refresh(){
		if(getSelectedElement() instanceof TestCaseNode){
			TestCaseNode testCase = (TestCaseNode)getSelectedElement();
			fTestCaseIf.setTarget(testCase);

			getMainSection().setText(testCase.toString());
			fTestSuiteNameCombo.setItems(testCase.getMethod().getTestSuites().toArray(new String[]{}));
			fTestSuiteNameCombo.setText(testCase.getName());
			fTestDataSection.setInput(testCase);
		}
	}
}
