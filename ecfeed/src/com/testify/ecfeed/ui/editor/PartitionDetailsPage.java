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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.PartitionInterface;

public class PartitionDetailsPage extends BasicDetailsPage {
	
	private PartitionsViewer fChildrenViewer;
	private PartitionLabelsViewer fLabelsViewer;
	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fValueCombo;

	private PartitionInterface fPartitionIf;

	private class NameTextListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fPartitionIf.setName(fNameText.getText());
			fNameText.setText(fPartitionIf.getName());
		}
	}
	
	private class ValueComboListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fPartitionIf.setValue(fValueCombo.getText());
			fValueCombo.setText(fPartitionIf.getValue());
		}
	}
	
	public PartitionDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInforProvider) {
		super(masterSection, updateContext, fileInforProvider);
		fPartitionIf = new PartitionInterface(this);
	}
	
	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameValueEditor(getMainComposite());
		addViewerSection(fChildrenViewer = new PartitionsViewer(this, this));
		addViewerSection(fLabelsViewer = new PartitionLabelsViewer(this, this));
		
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
		PartitionNode selectedPartition = getSelectedPartition();
		if(selectedPartition != null){
			fPartitionIf.setTarget(selectedPartition);

			String title = getSelectedPartition().toString() + " [" + fPartitionIf.getImplementationStatus().toString() + "]";
			getMainSection().setText(title);

			fChildrenViewer.setInput(selectedPartition);
			fLabelsViewer.setInput(selectedPartition);
			fNameText.setText(selectedPartition.getName());
			refreshValueEditor();
		}
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
