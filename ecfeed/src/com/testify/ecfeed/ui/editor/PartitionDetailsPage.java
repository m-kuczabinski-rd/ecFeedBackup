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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.PartitionInterface;

public class PartitionDetailsPage extends BasicDetailsPage {
	
	private PartitionsViewer fChildrenViewer;
	private PartitionLabelsViewer fLabelsViewer;
	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fValueCombo;

	private PartitionInterface fPartitionIf;
	private ModelOperationManager fOperationManager;
	

	private class NameTextListener extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fPartitionIf.setName(fNameText.getText(), null, PartitionDetailsPage.this);
			fNameText.setText(fPartitionIf.getName());
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e){
			widgetSelected(e);
		}
	}
	
	private class ValueComboListener extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fPartitionIf.setValue(fValueCombo.getText(), null, PartitionDetailsPage.this);
			fValueCombo.setText(fPartitionIf.getValue());
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e){
			widgetSelected(e);
		}
	}
	
	public PartitionDetailsPage(ModelMasterSection masterSection, ModelOperationManager operationManager) {
		super(masterSection);
		fOperationManager = operationManager;
		fPartitionIf = new PartitionInterface(fOperationManager);
	}
	
	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameValueEditor(getMainComposite());
		addForm(fChildrenViewer = new PartitionsViewer(this, getToolkit(), fOperationManager));
		addForm(fLabelsViewer = new PartitionLabelsViewer(this, getToolkit(), fOperationManager));
		
		getToolkit().paintBordersFor(getMainComposite());
	}
	
	@Override
	public void refresh(){
		PartitionNode selectedPartition = getSelectedPartition();
		fPartitionIf.setTarget(selectedPartition);
		
		String title = getSelectedPartition().toString() + " [" + fPartitionIf.implementationStatus().toString() + "]";
		getMainSection().setText(title);
		
		fChildrenViewer.setInput(selectedPartition);
		fLabelsViewer.setInput(selectedPartition);
		fNameText.setText(selectedPartition.getName());
		refreshValueEditor();
	}
	
	private void refreshValueEditor() {
		String type = fPartitionIf.getCategory().getType();
		if(fValueCombo != null && fValueCombo.isDisposed() == false){
			fValueCombo.dispose();
		}
		int style = SWT.DROP_DOWN;
		if(CategoryInterface.isBoolean(type)){
			style |= SWT.READ_ONLY;
		}
		fValueCombo = new ComboViewer(fAttributesComposite, style).getCombo();
		fValueCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Set<String> items = new LinkedHashSet<String>(CategoryInterface.getSpecialValues(type));
		items.add(fPartitionIf.getValue());
		fValueCombo.setItems(items.toArray(new String[]{}));
		fValueCombo.setText(fPartitionIf.getValue());
		fValueCombo.addSelectionListener(new ValueComboListener());
		fAttributesComposite.layout();
	}

	private PartitionNode getSelectedPartition(){
		if(getSelectedElement() != null && getSelectedElement() instanceof PartitionNode) {
			return (PartitionNode)getSelectedElement();
		}
		return null;
	}
	
	private void createNameValueEditor(Composite parent) {
		fAttributesComposite = getToolkit().createComposite(parent);
		fAttributesComposite.setLayout(new GridLayout(2, false));
		fAttributesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		getToolkit().createLabel(fAttributesComposite, "Name");
		fNameText = getToolkit().createText(fAttributesComposite, "", SWT.NONE);
		fNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fNameText.addSelectionListener(new NameTextListener());

		getToolkit().createLabel(fAttributesComposite, "Value");
		getToolkit().paintBordersFor(fAttributesComposite);
	}
}
