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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class CategoryDetailsPage extends BasicDetailsPage{

	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fTypeCombo;
	private Button fExpectedCheckbox;
	private Combo fDefaultValueCombo;

	private PartitionsViewer fPartitionsViewer;
	
	private CategoryInterface fCategoryIf;
	
	private class SetNameListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fCategoryIf.setName(fNameText.getText());
			fNameText.setText(fCategoryIf.getName());
		}
	}
	
	private class SetTypeListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fCategoryIf.setType(fTypeCombo.getText());
			fTypeCombo.setText(fCategoryIf.getType());
		}
	}
	
	private class SetDefaultValueListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fCategoryIf.setDefaultValue(fDefaultValueCombo.getText());
			fDefaultValueCombo.setText(fCategoryIf.getDefaultValue());
		}
	}
	
	private class SetExpectedListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fCategoryIf.setExpected(fExpectedCheckbox.getSelection());
			fExpectedCheckbox.setSelection(fCategoryIf.isExpected());
		}
	}
	
	public CategoryDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInforProvider) {
		super(masterSection, updateContext, fileInforProvider);
		fCategoryIf = new CategoryInterface(this);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		
		createAttributesComposite();
		addForm(fPartitionsViewer = new PartitionsViewer(this, this));

		getToolkit().paintBordersFor(getMainComposite());
	}
	
	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		createImplementerButton(textClient);
		return textClient;
	}
	
	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof CategoryNode){
			CategoryNode category = (CategoryNode)getSelectedElement();
			fCategoryIf.setTarget(category);
			
			getMainSection().setText(category.toString());
			fNameText.setText(category.getName());
			fTypeCombo.setItems(CategoryInterface.supportedPrimitiveTypes());
			fTypeCombo.setText(category.getType());
			recreateDefaultValueCombo(category);
			fExpectedCheckbox.setSelection(category.isExpected());
			if(fCategoryIf.isExpected() && fCategoryIf.isPrimitive()){
				fPartitionsViewer.setVisible(false);
			}
			else{
				fPartitionsViewer.setVisible(true);
			}
			
			fPartitionsViewer.setInput(category);
		}
	}
	
	private void createAttributesComposite(){
		fAttributesComposite = getToolkit().createComposite(getMainComposite());
		fAttributesComposite.setLayout(new GridLayout(2, false));
		fAttributesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		getToolkit().createLabel(fAttributesComposite, "Parameter name: ", SWT.NONE);
		fNameText = getToolkit().createText(fAttributesComposite, "",SWT.NONE);
		fNameText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		SelectionListener nameListener = new SetNameListener();
		fNameText.addSelectionListener(nameListener);
		
		getToolkit().createLabel(fAttributesComposite, "Parameter type: ", SWT.NONE);
		fTypeCombo = new Combo(fAttributesComposite,SWT.DROP_DOWN);
		fTypeCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fTypeCombo.addSelectionListener(new SetTypeListener());
		
		getToolkit().paintBordersFor(fAttributesComposite);
	
		getToolkit().createLabel(fAttributesComposite, "Default value: ", SWT.NONE);
	
		fExpectedCheckbox = getToolkit().createButton(getMainComposite(), "Expected", SWT.CHECK);
		fExpectedCheckbox.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fExpectedCheckbox.addSelectionListener(new SetExpectedListener());
	}

	private void recreateDefaultValueCombo(CategoryNode category) {
		if(fDefaultValueCombo != null && fDefaultValueCombo.isDisposed() == false){
			fDefaultValueCombo.dispose();
		}
		if(fCategoryIf.hasLimitedValuesSet()){
			fDefaultValueCombo = new Combo(fAttributesComposite,SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		else{
			fDefaultValueCombo = new Combo(fAttributesComposite,SWT.DROP_DOWN);
		}
		fDefaultValueCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false));
		fDefaultValueCombo.setItems(fCategoryIf.defaultValueSuggestions());
		fDefaultValueCombo.setText(category.getDefaultValue());
		fDefaultValueCombo.addSelectionListener(new SetDefaultValueListener());
		
		fDefaultValueCombo.setEnabled(category.isExpected());
		
		fAttributesComposite.layout();
	}
}
