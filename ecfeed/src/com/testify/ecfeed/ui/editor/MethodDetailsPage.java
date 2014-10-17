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
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class MethodDetailsPage extends BasicDetailsPage {

	private Text fMethodNameText;
	private Button fTestOnlineButton;
	private Button fBrowseButton;
	private ParametersViewer fParemetersSection;
	private ConstraintsListViewer fConstraintsSection;
	private TestCasesViewer fTestCasesSection;
	
	private MethodInterface fMethodIf;
	
	private class OnlineTestAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fMethodIf.executeOnlineTests();
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
	
	public MethodDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInforProvider) {
		super(masterSection, updateContext, fileInforProvider);
		fMethodIf = new MethodInterface(this);
	}

	public MethodNode getSelectedMethod() {
		return fMethodIf.getTarget();
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameTextComposite();
		addViewerSection(fParemetersSection = new ParametersViewer(this, this));
		addViewerSection(fConstraintsSection = new ConstraintsListViewer(this, this));
		addViewerSection(fTestCasesSection = new TestCasesViewer(this, this));
		
		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		createImplementerButton(textClient);
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
			getMainSection().setText(selectedMethod.toString() + " [" + methodStatus + "]");
			fTestOnlineButton.setEnabled(methodStatus != EImplementationStatus.NOT_IMPLEMENTED);
			fParemetersSection.setInput(selectedMethod);
			fConstraintsSection.setInput(selectedMethod);
			fTestCasesSection.setInput(selectedMethod);
			fMethodNameText.setText(fMethodIf.getName());
			
			EImplementationStatus parentStatus = fMethodIf.getImplementationStatus(selectedMethod.getClassNode());
			fBrowseButton.setEnabled((parentStatus == EImplementationStatus.IMPLEMENTED || 
					parentStatus == EImplementationStatus.PARTIALLY_IMPLEMENTED) && 
					fMethodIf.getCompatibleMethods().isEmpty() == false);
		}
	}
}
