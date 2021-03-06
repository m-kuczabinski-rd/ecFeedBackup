/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.RootInterface;

public class ModelDetailsPage extends BasicDetailsPage {

	private ClassViewer fClassesSection;
	private GlobalParametersViewer fParametersSection;
	private Text fModelNameText;
	private RootInterface fRootIf;
	private SingleTextCommentsSection fComments;
	private IFileInfoProvider fFileInfoProvider;

	private class SetNameAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fRootIf.setName(fModelNameText.getText());
			fModelNameText.setText(fRootIf.getName());
		}
	}

	public ModelDetailsPage(
			ModelMasterSection masterSection, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInforProvider) {
		super(masterSection, updateContext, fileInforProvider);
		fFileInfoProvider = fileInforProvider;
		fRootIf = new RootInterface(this, fFileInfoProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		getMainSection().setText("Model details");

		createModelNameEdit(getMainComposite());

		if (fFileInfoProvider.isProjectAvailable()) {
			addForm(fComments = new ExportableSingleTextCommentsSection(this, this, fFileInfoProvider));
		}
		addViewerSection(fClassesSection = new ClassViewer(this, this, fFileInfoProvider));

		fParametersSection = new GlobalParametersViewer(this, this, fFileInfoProvider);
		addViewerSection(fParametersSection);

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	private void createModelNameEdit(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Model name", SWT.NONE);
		fModelNameText = getToolkit().createText(composite, null, SWT.NONE);
		fModelNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fModelNameText.addSelectionListener(new SetNameAdapter());
		getToolkit().paintBordersFor(composite);
	}

	@Override
	public void refresh() {
		super.refresh();
		if(getSelectedElement() instanceof RootNode){
			RootNode selectedRoot = (RootNode)getSelectedElement();
			fRootIf.setTarget(selectedRoot);
			fModelNameText.setText(selectedRoot.getName());
			fClassesSection.setInput(selectedRoot);
			fParametersSection.setInput(selectedRoot);

			if (fFileInfoProvider.isProjectAvailable()) {
				fComments.setInput(selectedRoot);
			}
		}
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if(getSelectedElement() instanceof RootNode){
			fRootIf.setTarget((RootNode)getSelectedElement());
		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return RootNode.class;
	}

}
