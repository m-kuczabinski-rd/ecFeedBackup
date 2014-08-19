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

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.ConstraintInterface;

public class ConstraintDetailsPage extends BasicDetailsPage {

	private Combo fNameCombo;
	private ConstraintViewer fConstraintViewer;
	private ConstraintInterface fConstraintIf;
	private ModelOperationManager fOperationManager;
	
	private class RenameConstraintAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			applyConstraintName(fNameCombo.getText());
		}
	}

	private class ConstraintNameListener implements Listener{
		@Override
		public void handleEvent(Event event) {
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				applyConstraintName(fNameCombo.getText());
			}
		}
	}
	
	private void applyConstraintName(String newName) {
		fConstraintIf.setName(newName, null, this);
		fNameCombo.setText(fConstraintIf.getName());
	}

	public ConstraintDetailsPage(ModelMasterSection masterSection, ModelOperationManager operationManager) {
		super(masterSection);
		fOperationManager = operationManager;
		fConstraintIf = new ConstraintInterface(operationManager);
	}
	
	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		createConstraintNameEdit(getMainComposite());
		addForm(fConstraintViewer = new ConstraintViewer(this, getToolkit(), fOperationManager));
	}
	
	private void createConstraintNameEdit(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Constraint name:");
		ComboViewer nameComboViewer = new ComboViewer(composite, SWT.NONE);
		fNameCombo = nameComboViewer.getCombo();
		fNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fNameCombo.addListener(SWT.KeyDown, new ConstraintNameListener());
		fNameCombo.addSelectionListener(new RenameConstraintAdapter());
		Button button = getToolkit().createButton(composite, "Change", SWT.NONE);
		button.addSelectionListener(new RenameConstraintAdapter());
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
