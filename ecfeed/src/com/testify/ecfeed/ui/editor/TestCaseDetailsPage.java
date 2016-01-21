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

import com.testify.ecfeed.core.model.AbstractNode;
import com.testify.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.utils.EcException;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.editor.utils.ExceptionCatchDialog;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.TestCaseInterface;

public class TestCaseDetailsPage extends BasicDetailsPage {

	private IFileInfoProvider fFileInfoProvider;
	private Combo fTestSuiteNameCombo;
	private TestDataViewer fTestDataViewer;
	private Button fExecuteButton;

	private TestCaseInterface fTestCaseIf;
	private SingleTextCommentsSection fCommentsSection;

	private class RenameTestCaseAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fTestCaseIf.setName(fTestSuiteNameCombo.getText());
			fTestSuiteNameCombo.setText(fTestCaseIf.getName());
		}
	}
	public TestCaseDetailsPage(
			ModelMasterSection masterSection, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		fTestCaseIf = new TestCaseInterface(this, fileInfoProvider);
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
		createTestSuiteEdit(getMainComposite());

		if (fFileInfoProvider.isProjectAvailable()) {
			addForm(fCommentsSection = new SingleTextCommentsSection(this, this, fFileInfoProvider));
		}
		addViewerSection(fTestDataViewer = new TestDataViewer(this, this, fFileInfoProvider));
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof TestCaseNode){
			TestCaseNode testCase = (TestCaseNode)getSelectedElement();
			fTestCaseIf.setTarget(testCase);

			if (fFileInfoProvider.isProjectAvailable()) {
				fCommentsSection.setInput(testCase);
			}

			getMainSection().setText(testCase.toString());
			fTestSuiteNameCombo.setItems(testCase.getMethod().getTestSuites().toArray(new String[]{}));
			fTestSuiteNameCombo.setText(testCase.getName());
			fTestDataViewer.setInput(testCase);

			if (fFileInfoProvider.isProjectAvailable()) {
				fExecuteButton.setEnabled(fTestCaseIf.isExecutable());
			}
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

		if (fFileInfoProvider.isProjectAvailable()) {
			fExecuteButton = getToolkit().createButton(composite, "Execute", SWT.NONE);
			fExecuteButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent ev){
					try {
						fTestCaseIf.executeStaticTest();
					} catch (EcException e) {
						ExceptionCatchDialog.display("Can not execute static tests.", e.getMessage());
					}
				}
			});
		}

		getToolkit().paintBordersFor(fTestSuiteNameCombo);
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return TestCaseNode.class;
	}
}
