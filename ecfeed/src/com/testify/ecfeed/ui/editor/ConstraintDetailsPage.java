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

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.ui.modelif.ConstraintInterface;

public class ConstraintDetailsPage extends BasicDetailsPage {

	private Combo fNameCombo;
	private ConstraintInterface fConstraintIf;
	private ConstraintViewer fConstraintViewer;
	
	private class ConstraintNameListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fConstraintIf.setName(fNameCombo.getText(), ConstraintDetailsPage.this);
			fNameCombo.setText(fConstraintIf.getName());
		}
	}
	
	public ConstraintDetailsPage(ModelMasterDetailsBlock masterDetailsBlock) {
		super(masterDetailsBlock);
		fConstraintIf = new ConstraintInterface();
	}
	
	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		createConstraintNameEdit(getMainComposite());
		addForm(fConstraintViewer = new ConstraintViewer(this, getToolkit()));
	}
	
	private void createConstraintNameEdit(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Constraint name:");
		fNameCombo = new ComboViewer(composite, SWT.NONE).getCombo();
		fNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fNameCombo.addSelectionListener(new ConstraintNameListener());
	}

	@Override
	public void refresh(){
		if(getSelectedElement() instanceof ConstraintNode){
			ConstraintNode constraint = (ConstraintNode)getSelectedElement();

			getMainSection().setText(constraint.toString());
			fNameCombo.setItems(constraint.getMethod().getConstraintsNames().toArray(new String[]{}));
			fNameCombo.setText(constraint.getName());
			fConstraintViewer.setInput(constraint);
			fConstraintIf.setTarget(constraint);
		}
	}


}
