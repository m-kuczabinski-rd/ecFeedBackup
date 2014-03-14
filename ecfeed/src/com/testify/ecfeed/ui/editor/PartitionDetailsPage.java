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

import com.testify.ecfeed.model.PartitionNode;

public class PartitionDetailsPage extends BasicDetailsPage {

	private class PartitionNameTextListener extends ApplyChangesSelectionAdapter implements Listener{
		@Override
		public void handleEvent(Event event) {
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				if(applyNewPartitionName(fSelectedPartition, fPartitionNameText)){
					modelUpdated(null);
				}
			}
		}
	}
	
	private class PartitionValueTextListener extends ApplyChangesSelectionAdapter implements Listener{
		@Override
		public void handleEvent(Event event) {
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				if(applyNewPartitionValue(fSelectedPartition, fPartitionValueText)){
					modelUpdated(null);
				}
			}
		}
	}
	
	private class ApplyChangesSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			boolean updated = false;
			updated |= applyNewPartitionName(fSelectedPartition, fPartitionNameText);
			updated |= applyNewPartitionValue(fSelectedPartition, fPartitionValueText);
			if(updated){
				modelUpdated(null);
			}
		}
		
		protected boolean applyNewPartitionName(PartitionNode partition, Text nameText) {
			String newName = nameText.getText(); 
			if(newName.equals(partition.getName()) == false){
				if(partition.getCategory().validatePartitionName(newName)){
					partition.setName(newName);
					return true;
				}
				else{
					nameText.setText(partition.getName());
				}
			}
			return false;
		}

		protected boolean applyNewPartitionValue(PartitionNode partition, Text valueText) {
			String newValue = valueText.getText(); 
			if(newValue.equals(partition.getValueString()) == false){
				if(partition.getCategory().validatePartitionStringValue(newValue)){
					Object value = partition.getCategory().getPartitionValueFromString(newValue);
					partition.setValue(value);
					return true;
				}
				else{
					valueText.setText(partition.getValueString());
				}
			}
			return false;
		}
	}
	
	private PartitionNode fSelectedPartition;
	private PartitionChildrenViewer fPartitionChildren;
	private PartitionLabelsViewer fLabelsViewer;
	private Text fPartitionNameText;
	private Text fPartitionValueText;

	public PartitionDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}
	
	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameValueEdit(getMainComposite());
		addForm(fPartitionChildren = new PartitionChildrenViewer(this, getToolkit()));
		addForm(fLabelsViewer = new PartitionLabelsViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}
	
	private void createNameValueEdit(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		createNameEdit(composite);
		createValueEdit(composite);
	}

	private void createNameEdit(Composite parent) {
		getToolkit().createLabel(parent, "Name");
		fPartitionNameText = getToolkit().createText(parent, "", SWT.NONE);
		fPartitionNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionNameText.addListener(SWT.KeyDown, new PartitionNameTextListener());
		Composite buttonComposite = getToolkit().createComposite(parent);
		buttonComposite.setLayout(new GridLayout(1, false));
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2));
		Button applyButton = getToolkit().createButton(buttonComposite, "Change", SWT.CENTER);
		applyButton.addSelectionListener(new ApplyChangesSelectionAdapter());
		getToolkit().paintBordersFor(parent);

	}

	private void createValueEdit(Composite parent) {
		getToolkit().createLabel(parent, "Value");
		fPartitionValueText = getToolkit().createText(parent, "", SWT.NONE);
		fPartitionValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionValueText.addListener(SWT.KeyDown, new PartitionValueTextListener());
		getToolkit().paintBordersFor(parent);
	}

	@Override
	public void refresh(){
		if(getSelectedElement() instanceof PartitionNode){
			fSelectedPartition = (PartitionNode)getSelectedElement();
		}
		if(fSelectedPartition != null){
			getMainSection().setText(fSelectedPartition.toString());
			fPartitionChildren.setInput(fSelectedPartition);
			fLabelsViewer.setInput(fSelectedPartition);
			fPartitionNameText.setText(fSelectedPartition.getName());
			if(fSelectedPartition.isAbstract()){
				fPartitionValueText.setEnabled(false);
				fPartitionValueText.setText("");
			}
			else{
				fPartitionValueText.setEnabled(true);
				fPartitionValueText.setText(fSelectedPartition.getValueString());
			}
		}
	}
}
