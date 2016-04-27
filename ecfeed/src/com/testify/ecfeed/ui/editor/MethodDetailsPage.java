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

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.core.adapter.EImplementationStatus;
import com.testify.ecfeed.core.adapter.java.JavaUtils;
import com.testify.ecfeed.core.model.AbstractNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class MethodDetailsPage extends BasicDetailsPage {

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
				fMethodIf.executeOnlineTests(getFileInfoProvider());
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not execute online tests.", e.getMessage());
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

	public MethodDetailsPage(
			ModelMasterSection masterSection, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fMethodIf = new MethodInterface(this, fileInfoProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		
		IFileInfoProvider fileInfoProvider = getFileInfoProvider();
		
		createMethodNameWidgets(fileInfoProvider);
		createTestAndExportButtons(fileInfoProvider);

		if (fileInfoProvider.isProjectAvailable()) {
			addForm(fCommentsSection = new JavaDocCommentsSection(this, this, fileInfoProvider));
		}
		addViewerSection(fParemetersSection = new MethodParametersViewer(this, this, fileInfoProvider));
		addViewerSection(fConstraintsSection = new ConstraintsListViewer(this, this, fileInfoProvider));
		addViewerSection(fTestCasesSection = new TestCasesViewer(this, this, fileInfoProvider));

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	private void createMethodNameWidgets(IFileInfoProvider fileInfoProvider) {
		int gridColumns = 2;
		
		if (fileInfoProvider.isProjectAvailable()) {
			++gridColumns;
		}
		
		Composite gridComposite = getFormObjectFactory().createGridComposite(getMainComposite(), gridColumns);
		
		getFormObjectFactory().createLabel(gridComposite, "Method name ");
		fMethodNameText = getFormObjectFactory().createGridText(gridComposite, new RenameMethodAdapter());

		if (fileInfoProvider.isProjectAvailable()) {
			fBrowseButton 
				= getFormObjectFactory().createButton(gridComposite, "Browse...", new ReassignAdapter());
		}
		
		getFormObjectFactory().paintBorders(gridComposite);
	}
	
	private void createTestAndExportButtons(IFileInfoProvider fileInfoProvider) {
		Composite childComposite = getFormObjectFactory().createRowComposite(getMainComposite());
		
		if (fileInfoProvider.isProjectAvailable()) {
			fTestOnlineButton 
				= getFormObjectFactory().createButton(childComposite, "Test online...", new OnlineTestAdapter());
		}
		
		getFormObjectFactory().createButton(childComposite, "Export online...", new OnlineTestAdapter());
		getFormObjectFactory().paintBorders(childComposite);
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof MethodNode){
			MethodNode selectedMethod = (MethodNode)getSelectedElement();
			fMethodIf.setTarget(selectedMethod);

			IFileInfoProvider fileInfoProvider = getFileInfoProvider();

			EImplementationStatus methodStatus = null;
			if (fileInfoProvider.isProjectAvailable()) {
				methodStatus = fMethodIf.getImplementationStatus();
			}
			getMainSection().setText(JavaUtils.simplifiedToString(selectedMethod));

			if (fileInfoProvider.isProjectAvailable()) {
				fTestOnlineButton.setEnabled(methodStatus != EImplementationStatus.NOT_IMPLEMENTED);
			}
			fParemetersSection.setInput(selectedMethod);
			fConstraintsSection.setInput(selectedMethod);
			fTestCasesSection.setInput(selectedMethod);

			if (fileInfoProvider.isProjectAvailable()) {
				fCommentsSection.setInput(selectedMethod);
			}
			fMethodNameText.setText(fMethodIf.getName());

			if (fileInfoProvider.isProjectAvailable()) {
				EImplementationStatus parentStatus = fMethodIf.getImplementationStatus(selectedMethod.getClassNode());
				fBrowseButton.setEnabled((parentStatus == EImplementationStatus.IMPLEMENTED ||
						parentStatus == EImplementationStatus.PARTIALLY_IMPLEMENTED) &&
						fMethodIf.getCompatibleMethods().isEmpty() == false);
			}
		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return MethodNode.class;
	}

}
