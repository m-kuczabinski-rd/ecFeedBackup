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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.implementor.ModelImplementor;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.CategoryNodeAbstractLayer;
import com.testify.ecfeed.ui.common.PartitionNodeAbstractLayer;
import com.testify.ecfeed.utils.ModelUtils;

public class CategoryDetailsPage extends BasicDetailsPage {

	private Combo fDefaultEditableValueCombo;
	private Text fNameText;
	private Combo fTypeCombo;
	private Button fExpectedCheckbox;
	private CategoryNode fSelectedCategory;
	private CategoryChildrenViewer fPartitionsViewer;
	private StackLayout fComboLayout;
	private Combo fDefaultValueCombo;
	private Button fImplementButton;
	
	private class valueComboSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			if(e.widget instanceof Combo){
					Combo source = (Combo)e.widget;
				if(PartitionNodeAbstractLayer.changePartitionValue(fSelectedCategory.getDefaultValuePartition(),
						source.getText())){
					modelUpdated(null);
				}
				source.setText(fSelectedCategory.getDefaultValueString());
			}
		}
	}
	
	private class valueComboKeyListener implements Listener{
		@Override
		public void handleEvent(Event event){
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				if(event.widget instanceof Combo){
					Combo source = (Combo)event.widget;

					if(PartitionNodeAbstractLayer.changePartitionValue(fSelectedCategory.getDefaultValuePartition(), source.getText())){
						modelUpdated(null);
					}
					source.setText(fSelectedCategory.getDefaultValueString());
				}
			}
		}
	}
	
	private class expectedCheckboxSelectionListener extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			if(CategoryNodeAbstractLayer.changeCategoryExpectedStatus(fSelectedCategory, fExpectedCheckbox.getSelection())){
				modelUpdated(null);
			} else
				fExpectedCheckbox.setSelection(!fExpectedCheckbox.getSelection());
		}
	}
	
	private class nameTextListener implements Listener{
		@Override
		public void handleEvent(Event event){
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				changeName();
			}
		}
	}
	
	private class nameChangeButtonListener extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			changeName();
		}
	}
	
	private class typeTextListener implements Listener{
		@Override
		public void handleEvent(Event event){
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				if(CategoryNodeAbstractLayer.changeCategoryType(fSelectedCategory, fTypeCombo.getText())){
					modelUpdated(null);
				}
				fTypeCombo.setText(fSelectedCategory.getType());
			}
		}
	}
	
	private class typeComboSelectionListener extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			if(CategoryNodeAbstractLayer.changeCategoryType(fSelectedCategory, fTypeCombo.getText())){
				modelUpdated(null);
			}
			fTypeCombo.setText(fSelectedCategory.getType());
		}
	}
	
	
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
				refreshForExpected();
			} else{
				refreshForPartitioned();
			}
			fImplementButton.setEnabled(!ModelUtils.isCategoryImplemented(fSelectedCategory));
		} else{
			refreshForInvalidInput();
		}
	}
	
	private void createCommonParametersEdit(){
		Composite textClientComposite = getToolkit().createComposite(getMainSection());
		textClientComposite.setLayout(new RowLayout());
		fImplementButton = getToolkit().createButton(textClientComposite, "Implement", SWT.NONE);
		fImplementButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				ModelImplementor implementor = new ModelImplementor();
				implementor.implement(fSelectedCategory);
				try {
					ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (CoreException f) {
				}
			}
		});
		getMainSection().setTextClient(textClientComposite);

		Composite composite = getToolkit().createComposite(getMainComposite());
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		getToolkit().createLabel(composite, "Category name: ", SWT.NONE);
		Composite nameComposite = getToolkit().createComposite(composite);
		GridLayout nameGrid = new GridLayout(2, false);
		nameGrid.marginWidth = 2;
		nameComposite.setLayout(nameGrid);
		nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fNameText = getToolkit().createText(nameComposite, "",SWT.NONE);
		fNameText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fNameText.addListener(SWT.KeyDown, new nameTextListener());
		Button changeButton = getToolkit().createButton(nameComposite, "Change", SWT.NONE);
		changeButton.addSelectionListener(new nameChangeButtonListener());

		getToolkit().createLabel(composite, "Category type: ", SWT.NONE);
		fTypeCombo = new Combo(composite,SWT.DROP_DOWN);
		fTypeCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false));
		fTypeCombo.addListener(SWT.KeyDown, new typeTextListener());
		fTypeCombo.addSelectionListener(new typeComboSelectionListener());

		getToolkit().paintBordersFor(composite);
		getToolkit().paintBordersFor(nameComposite);
	}

	private void createDefaultValueEdit(){
		Composite composite = getToolkit().createComposite(getMainComposite());		
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Default value: ", SWT.NONE);
		
		// Stack layout for editable/noneditable comboboxes
		Composite valueComposite = getToolkit().createComposite(composite);
		fComboLayout = new StackLayout();
		valueComposite.setLayout(fComboLayout);
		valueComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fDefaultEditableValueCombo = new Combo(valueComposite,SWT.DROP_DOWN);
		fDefaultEditableValueCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fDefaultEditableValueCombo.addListener(SWT.KeyDown, new valueComboKeyListener());
		fDefaultEditableValueCombo.addSelectionListener(new valueComboSelectionAdapter());
		
		fDefaultValueCombo = new Combo(valueComposite,SWT.READ_ONLY);
		fDefaultValueCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fDefaultValueCombo.addSelectionListener(new valueComboSelectionAdapter());		
		//------------------------
		fExpectedCheckbox = getToolkit().createButton(composite, "Expected", SWT.CHECK);
		fExpectedCheckbox.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false));
		fExpectedCheckbox.addSelectionListener(new expectedCheckboxSelectionListener());
		
		getToolkit().paintBordersFor(valueComposite);
		getToolkit().paintBordersFor(composite);
	}
	
	private void refreshForExpected(){
		if(ModelUtils.getJavaTypes().contains(fSelectedCategory.getType())){
			fPartitionsViewer.setVisible(false);
			if(fSelectedCategory.getType().equals(com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN)){
				fDefaultEditableValueCombo.setVisible(false);
				fDefaultValueCombo.setVisible(true);
				fDefaultValueCombo.setEnabled(true);
				prepareDefaultValues(fSelectedCategory, fDefaultValueCombo);
				fDefaultValueCombo.setText(fSelectedCategory.getDefaultValueString());
				fComboLayout.topControl = fDefaultValueCombo;
			} else {
				fDefaultEditableValueCombo.setVisible(true);
				fDefaultValueCombo.setVisible(false);
				fDefaultEditableValueCombo.setEnabled(true);
				prepareDefaultValues(fSelectedCategory, fDefaultEditableValueCombo);
				fDefaultEditableValueCombo.setText(fSelectedCategory.getDefaultValueString());
				fComboLayout.topControl = fDefaultEditableValueCombo;
			}
		} else {
			fDefaultEditableValueCombo.setVisible(false);
			fDefaultValueCombo.setVisible(true);
			fPartitionsViewer.setVisible(true);
			fDefaultValueCombo.setEnabled(true);
			prepareDefaultValues(fSelectedCategory, fDefaultValueCombo);
			fDefaultValueCombo.setText(fSelectedCategory.getDefaultValueString());
			fComboLayout.topControl = fDefaultValueCombo;
		}
	}
	
	private void refreshForPartitioned(){
		fComboLayout.topControl = fDefaultEditableValueCombo;
		fDefaultValueCombo.setVisible(false);
		fDefaultEditableValueCombo.setVisible(true);
		fDefaultEditableValueCombo.setText("");
		fDefaultEditableValueCombo.setEnabled(false);
		fPartitionsViewer.setVisible(true);
	}
	
	private void refreshForInvalidInput(){
		fExpectedCheckbox.setEnabled(false);
		fDefaultEditableValueCombo.setText("");
		fDefaultEditableValueCombo.setEnabled(false);
		fNameText.setText("");
		fNameText.setEnabled(false);
		fTypeCombo.setText("");
		fTypeCombo.setEnabled(false);
		fPartitionsViewer.setVisible(false);
	}
	
	private void changeName() {
		if (CategoryNodeAbstractLayer.changeCategoryName(fSelectedCategory, fNameText.getText())){
			modelUpdated(null);
		}
		fNameText.setText(fSelectedCategory.getName());
		fNameText.setSelection(fSelectedCategory.getName().length());
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
