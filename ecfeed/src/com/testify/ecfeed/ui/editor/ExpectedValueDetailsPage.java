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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.ExpectedCategoryNode;
import com.testify.ecfeed.utils.ModelUtils;;

public class ExpectedValueDetailsPage extends BasicDetailsPage {

	private Text fDefaultValueText;
	private ExpectedCategoryNode fSelectedCategory;

	private class ApplyButtonAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			if(applyNewDefaultValue(fSelectedCategory, fDefaultValueText)){
				modelUpdated(null);
			}
		}

		protected boolean applyNewDefaultValue(ExpectedCategoryNode category, Text valueText) {
			String newValue = valueText.getText();
			if(ModelUtils.validatePartitionStringValue(newValue, category.getType())){
				category.setDefaultValue(ModelUtils.getPartitionValueFromString(newValue, category.getType()));
				return true;
			}
			valueText.setText(category.getDefaultValuePartition().getValueString());
			return false;
		}
	}
	
	private class DefaultValueKeydownListener extends ApplyButtonAdapter implements Listener{
		@Override
		public void handleEvent(Event event) {
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				if(applyNewDefaultValue(fSelectedCategory, fDefaultValueText)){
					modelUpdated(null);
				}
			}
		}
	}
	
	public ExpectedValueDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}
	
	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		createDefaultValueEdit();
	}

	private void createDefaultValueEdit() {
		Composite composite = getToolkit().createComposite(getMainComposite());
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Default value: ", SWT.NONE);
		fDefaultValueText = getToolkit().createText(composite, "",SWT.NONE);
		fDefaultValueText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fDefaultValueText.addListener(SWT.KeyDown, new DefaultValueKeydownListener());
		Button applyButton = getToolkit().createButton(composite, "Apply", SWT.NONE);
		getToolkit().paintBordersFor(composite);
		applyButton.addSelectionListener(new ApplyButtonAdapter());
	}
	
	@Override
	public void refresh(){
		if(getSelectedElement() instanceof ExpectedCategoryNode){
			fSelectedCategory = (ExpectedCategoryNode)getSelectedElement();
		}
		getMainSection().setText(fSelectedCategory.toString());
		fDefaultValueText.setText(fSelectedCategory.getDefaultValuePartition().getValueString());
	}
}
