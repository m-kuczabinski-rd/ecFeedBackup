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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.CategoryNodeAbstractLayer;
import com.testify.ecfeed.utils.ModelUtils;

public class CategoryDetailsPage extends BasicDetailsPage {

	private Combo fDefaultValueCombo;
	private Text fNameText;
	private Combo fTypeCombo;
	private Button fExpectedCheckbox;
	private CategoryNode fSelectedCategory;
	private CategoryChildrenViewer fPartitionsViewer;
	
	public CategoryDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		
		createCommonParametersEdit();
		createDefaultValueEdit();
		addForm(fPartitionsViewer = new CategoryChildrenViewer(this, getToolkit()));

		getToolkit().paintBordersFor(getMainComposite());
	}
	
	@Override
	public void refresh(){
		if(getSelectedElement() instanceof CategoryNode){
			fSelectedCategory = (CategoryNode)getSelectedElement();
			getMainSection().setText(fSelectedCategory.toString());
			fPartitionsViewer.setInput(fSelectedCategory);
			
			fNameText.setText(fSelectedCategory.getName());
			fNameText.setEnabled(true);
			fTypeCombo.setEnabled(true);
			fTypeCombo.setItems(ModelUtils.getJavaTypes().toArray(new String[0]));
			fTypeCombo.setText(fSelectedCategory.getType());
			
			fExpectedCheckbox.setEnabled(true);
			fExpectedCheckbox.setSelection(fSelectedCategory.isExpected());
			
			if(fSelectedCategory.isExpected()){
				prepareDefaultValues(fSelectedCategory, fDefaultValueCombo);
				fDefaultValueCombo.setText(fSelectedCategory.getDefaultValueString());
				fDefaultValueCombo.setEnabled(true);
				fPartitionsViewer.setVisible(false);
			} else{
				fDefaultValueCombo.setText("");
				fDefaultValueCombo.setEnabled(false);
				fPartitionsViewer.setVisible(true);
			}

		} else{
			fExpectedCheckbox.setEnabled(false);
			fDefaultValueCombo.setText("");
			fDefaultValueCombo.setEnabled(false);
			fNameText.setText("");
			fNameText.setEnabled(false);
			fTypeCombo.setText("");
			fTypeCombo.setEnabled(false);
			fPartitionsViewer.setVisible(false);
		}
	}
	
	public void createCommonParametersEdit(){
		Composite composite = getToolkit().createComposite(getMainComposite());
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Category name: ", SWT.NONE);
		fNameText = getToolkit().createText(composite, "",SWT.NONE);
		fNameText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fNameText.addListener(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event event){
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					if(applyNewCategoryName(fSelectedCategory, fNameText)){
						modelUpdated(null);
					}
				}
			}
		});
		getToolkit().createLabel(composite, "Category type: ", SWT.NONE);
		fTypeCombo = new Combo(composite,SWT.DROP_DOWN);
		fTypeCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false));
		fTypeCombo.addListener(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event event){
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					if(applyNewCategoryType(fSelectedCategory, fTypeCombo)){
						modelUpdated(null);
					}
					fTypeText.setText(fSelectedCategory.getType());
				}
			}
		});
		fTypeCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				if(applyNewCategoryType(fSelectedCategory, fTypeCombo)){
					modelUpdated(null);
				}
				fTypeText.setText(fSelectedCategory.getType());
			}
		});
		getToolkit().paintBordersFor(composite);
	}
	
	public void createDefaultValueEdit(){
		Composite composite = getToolkit().createComposite(getMainComposite());
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Default value: ", SWT.NONE);
		fDefaultValueCombo = new Combo(composite,SWT.DROP_DOWN);
		fDefaultValueCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fDefaultValueCombo.addListener(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event event){
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					if(applyNewDefaultValue(fSelectedCategory, fDefaultValueCombo)){
						modelUpdated(null);
					}
				}
			}
		});
		fDefaultValueCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				if(applyNewDefaultValue(fSelectedCategory, fDefaultValueCombo)){
					modelUpdated(null);
				}
			}
		});
		
		fExpectedCheckbox = getToolkit().createButton(composite, "Expected", SWT.CHECK);
		fExpectedCheckbox.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false));
		fExpectedCheckbox.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				if(CategoryNodeAbstractLayer.changeCategoryExpectedStatus(fSelectedCategory, fExpectedCheckbox.getSelection())){
					modelUpdated(null);
				}
				else fExpectedCheckbox.setSelection(!fExpectedCheckbox.getSelection());
			}
		});
		
		getToolkit().paintBordersFor(composite);
	}
	
	protected boolean applyNewDefaultValue(CategoryNode category, Combo valueText) {
		String newValue = valueText.getText();
		if(newValue.equals(fSelectedCategory.getDefaultValueString())) return false;
		if(ModelUtils.validatePartitionStringValue(newValue, category.getType())){
			category.setDefaultValueString(newValue);
			return true;
		}
		valueText.setText(category.getDefaultValuePartition().getValueString());
		return false;
	}
	
	protected boolean applyNewCategoryName(CategoryNode category, Text valueText) {
		String newValue = valueText.getText();
		if(newValue.equals(fSelectedCategory.getName()) || fSelectedCategory.getSibling(newValue) != null){
			return false;
		}
		if(ModelUtils.validateNodeName(newValue)){
			category.setName(newValue);
			return true;
		}
		valueText.setText(category.getName());
		return false;
	}
	
	protected boolean applyNewCategoryType(CategoryNode category, Combo valueText) {
		String newValue = valueText.getText();
		return CategoryNodeAbstractLayer.changeCategoryType(category, newValue);
	}
	
	private void prepareDefaultValues(CategoryNode node, Combo valueText){
		HashMap<String, String> values = ModelUtils.generatePredefinedValues(node.getType());
		HashSet<String> itemset = new HashSet<>();
		itemset.addAll(values.values());
		for(PartitionNode partition: node.getLeafPartitions()){
			itemset.add(partition.getValueString());
		}
		String [] items = new String[itemset.size()];
		items = itemset.toArray(items);
		ArrayList<String> newItems = new ArrayList<String>();

		valueText.setItems(items);
		for (int i = 0; i < items.length; ++i) {
			newItems.add(items[i]);
			if (items[i].equals(node.getDefaultValueString())) {
				return;
			}
		}

		newItems.add(node.getDefaultValueString());
		valueText.setItems(newItems.toArray(items));
	}
}
