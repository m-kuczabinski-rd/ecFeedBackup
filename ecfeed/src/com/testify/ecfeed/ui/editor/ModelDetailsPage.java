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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.modelif.RootInterface;

public class ModelDetailsPage extends BasicDetailsPage {

	private ClassViewer fClassesSection;
	private Text fModelNameText;
	private RootInterface fRootIf;
	
	private class SetNameAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fRootIf.setName(fModelNameText.getText(), ModelDetailsPage.this);
			fModelNameText.setText(fRootIf.getName());
		}
	}
	
	public ModelDetailsPage(ModelMasterDetailsBlock masterDetailsBlock) {
		super(masterDetailsBlock);
		fRootIf = new RootInterface();
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		getMainSection().setText("Model details");

		createModelNameEdit(getMainComposite());
		addForm(fClassesSection = new ClassViewer(this, getToolkit()));

		getToolkit().paintBordersFor(getMainComposite());
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
		if(getSelectedElement() instanceof RootNode){
			fRootIf.setTarget((RootNode)getSelectedElement());
			fModelNameText.setText(fRootIf.getName());
			fClassesSection.setInput(fRootIf.getTarget());
		}
	}
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if(getSelectedElement() instanceof RootNode){
			fRootIf.setTarget((RootNode)getSelectedElement());
		}
	}

}
