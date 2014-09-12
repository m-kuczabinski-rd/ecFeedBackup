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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.modelif.TestCaseInterface;

public class TestCaseDetailsPage extends BasicDetailsPage {

	private Combo fTestSuiteNameCombo;
	private TestDataViewer fTestDataViewer;

	private TestCaseInterface fTestCaseIf;
	
	private class RenameTestCaseAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fTestCaseIf.setName(fTestSuiteNameCombo.getText(), TestCaseDetailsPage.this);
			fTestSuiteNameCombo.setText(fTestCaseIf.getName());
		}
	}
	public TestCaseDetailsPage(ModelMasterDetailsBlock masterDetailsBlock) {
		super(masterDetailsBlock);
		fTestCaseIf = new TestCaseInterface();
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
		createTestSuiteEdit(getMainComposite());
		addViewerSection(fTestDataViewer = new TestDataViewer(this, getToolkit()));
	}
	
	@Override
	public void refresh(){
		if(getSelectedElement() instanceof TestCaseNode){
			TestCaseNode testCase = (TestCaseNode)getSelectedElement();
			fTestCaseIf.setTarget(testCase);

			getMainSection().setText(testCase.toString());
			fTestSuiteNameCombo.setItems(testCase.getMethod().getTestSuites().toArray(new String[]{}));
			fTestSuiteNameCombo.setText(testCase.getName());
			fTestDataViewer.setInput(testCase);
		}
	}

	private void createTestSuiteEdit(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Test suite: ");
		
		fTestSuiteNameCombo = new ComboViewer(composite, SWT.NONE).getCombo();
		fTestSuiteNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fTestSuiteNameCombo.addSelectionListener(new RenameTestCaseAdapter());
		getToolkit().paintBordersFor(fTestSuiteNameCombo);
	}
}
