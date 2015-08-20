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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;
import com.testify.ecfeed.utils.SystemLogger;

public class MethodDetailsPage extends BasicDetailsPage {

	private IFileInfoProvider fFileInfoProvider;
	private Text fMethodNameText;
	private Button fTestOnlineButton;
	private Button fBrowseButton;
	private MethodParametersViewer fParemetersSection;
	private ConstraintsListViewer fConstraintsSection;
	private TestCasesViewer fTestCasesSection;

	private MethodInterface fMethodIf;
	private JavaDocCommentsSection fCommentsSection;

	private class OnlineTestAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				fMethodIf.executeOnlineTests(fFileInfoProvider);
			} catch (EcException e) {
				SystemLogger.logCatch(e.getMessage());
			}
		}
	}

	private class ReassignAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.reassignTarget();
		}
	}

	private class RenameMethodAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodIf.setName(fMethodNameText.getText());
			fMethodNameText.setText(fMethodIf.getName());
		}
	}

	public MethodDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		fMethodIf = new MethodInterface(this, fileInfoProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameTextComposite();
		addForm(fCommentsSection = new JavaDocCommentsSection(this, this, fFileInfoProvider));
		addViewerSection(fParemetersSection = new MethodParametersViewer(this, this, fFileInfoProvider));
		addViewerSection(fConstraintsSection = new ConstraintsListViewer(this, this, fFileInfoProvider));
		addViewerSection(fTestCasesSection = new TestCasesViewer(this, this, fFileInfoProvider));

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	private void createNameTextComposite() {
		Composite composite = getToolkit().createComposite(getMainComposite());
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Method name", SWT.NONE);
		fMethodNameText = getToolkit().createText(composite, null, SWT.NONE);
		fMethodNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fMethodNameText.addSelectionListener(new RenameMethodAdapter());

		fBrowseButton = getToolkit().createButton(composite, "Browse...", SWT.NONE);
		fBrowseButton.addSelectionListener(new ReassignAdapter());

		fTestOnlineButton = getToolkit().createButton(composite, "Test online", SWT.NONE);
		fTestOnlineButton.addSelectionListener(new OnlineTestAdapter());

		getToolkit().paintBordersFor(composite);
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof MethodNode){
			MethodNode selectedMethod = (MethodNode)getSelectedElement();
			fMethodIf.setTarget(selectedMethod);

			EImplementationStatus methodStatus = fMethodIf.getImplementationStatus();
			getMainSection().setText(JavaUtils.simplifiedToString(selectedMethod));
			fTestOnlineButton.setEnabled(methodStatus != EImplementationStatus.NOT_IMPLEMENTED);
			fParemetersSection.setInput(selectedMethod);
			fConstraintsSection.setInput(selectedMethod);
			fTestCasesSection.setInput(selectedMethod);
			fCommentsSection.setInput(selectedMethod);
			fMethodNameText.setText(fMethodIf.getName());

			EImplementationStatus parentStatus = fMethodIf.getImplementationStatus(selectedMethod.getClassNode());
			fBrowseButton.setEnabled((parentStatus == EImplementationStatus.IMPLEMENTED ||
					parentStatus == EImplementationStatus.PARTIALLY_IMPLEMENTED) &&
					fMethodIf.getCompatibleMethods().isEmpty() == false);
		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return MethodNode.class;
	}

}
