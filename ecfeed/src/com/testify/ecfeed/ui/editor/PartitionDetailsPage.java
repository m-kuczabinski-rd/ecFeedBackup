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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.implementor.ModelImplementor;
import com.testify.ecfeed.model.Constants;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.PartitionNodeAbstractLayer;
import com.testify.ecfeed.ui.dialogs.ProjectSelectionDialog;
import com.testify.ecfeed.utils.ModelUtils;

public class PartitionDetailsPage extends BasicDetailsPage {
	
	private PartitionNode fSelectedPartition;
	private PartitionChildrenViewer fPartitionChildren;
	private PartitionLabelsViewer fLabelsViewer;
	private Text fPartitionNameText;
	private Combo fPartitionValueCombo;
	private StackLayout fComboLayout;
	private Combo fBooleanValueCombo;
	private Button fImplementButton;

	private class PartitionNameTextListener extends ApplyChangesSelectionAdapter implements Listener{
		@Override
		public void handleEvent(Event event) {
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				if(PartitionNodeAbstractLayer.changePartitionName(fSelectedPartition, fPartitionNameText.getText())){
					modelUpdated(null);
				}
				fPartitionNameText.setText(fSelectedPartition.getName());
			}
		}
	}
	
	private class ApplyChangesSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			boolean update = false;
			if(PartitionNodeAbstractLayer.changePartitionName(fSelectedPartition, fPartitionNameText.getText())){
				update = true;
			}
			if(PartitionNodeAbstractLayer.changePartitionValue(fSelectedPartition, fPartitionValueCombo.getText())){
				update = true;
			}
			if(update){
				modelUpdated(null);
			}
			fPartitionNameText.setText(fSelectedPartition.getName());
			fPartitionValueCombo.setText(fSelectedPartition.getValueString());
		}
	}
	
	private class booleanValueComboSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e){
			if(PartitionNodeAbstractLayer.changePartitionValue(fSelectedPartition, fBooleanValueCombo.getText())){
				modelUpdated(null);
			}
			fBooleanValueCombo.setText(fSelectedPartition.getValueString());
		}
	}
	
	private class valueComboSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e){
			if(PartitionNodeAbstractLayer.changePartitionValue(fSelectedPartition, fPartitionValueCombo.getText())){
				modelUpdated(null);
			}
			fPartitionValueCombo.setText(fSelectedPartition.getValueString());
		}
	}
	
	private class valueTextListener implements Listener{
		@Override
		public void handleEvent(Event event){
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				if(PartitionNodeAbstractLayer.changePartitionValue(fSelectedPartition, fPartitionValueCombo.getText())){
					modelUpdated(null);
				}
				fPartitionValueCombo.setText(fSelectedPartition.getValueString());
			}
		}
	}
	
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
	
	@Override
	public void refresh(){
		if(getSelectedElement() != null && getSelectedElement() instanceof PartitionNode) {
			fSelectedPartition = (PartitionNode)getSelectedElement();
		} else {
			return;
		}
		String title = fSelectedPartition.toString();
		boolean implemented = ModelUtils.isPartitionImplemented(fSelectedPartition);
		if(implemented){
			title += " [implemented]";
		}
		getMainSection().setText(title);
		fPartitionChildren.setInput(fSelectedPartition);
		fLabelsViewer.setInput(fSelectedPartition);
		fPartitionNameText.setText(fSelectedPartition.getName());
		if(fSelectedPartition.isAbstract()){
			fPartitionValueCombo.setEnabled(false);
			fBooleanValueCombo.setEnabled(false);
			fPartitionValueCombo.setText("");
			fBooleanValueCombo.setText("");
		} else{
			if(fSelectedPartition.getCategory().getType().equals(Constants.TYPE_NAME_BOOLEAN)){
				fPartitionValueCombo.setVisible(false);
				fBooleanValueCombo.setVisible(true);
				fBooleanValueCombo.setEnabled(true);
				prepareDefaultValues(fSelectedPartition, fBooleanValueCombo);
				fBooleanValueCombo.setText(fSelectedPartition.getValueString());
				fComboLayout.topControl = fBooleanValueCombo;
			} else{
				fBooleanValueCombo.setVisible(false);
				fPartitionValueCombo.setEnabled(true);
				fPartitionValueCombo.setVisible(true);
				prepareDefaultValues(fSelectedPartition, fPartitionValueCombo);
				fPartitionValueCombo.setText(fSelectedPartition.getValueString());
				fComboLayout.topControl = fPartitionValueCombo;
			}
		}
		fImplementButton.setEnabled(!ModelUtils.isPartitionImplemented(fSelectedPartition) || ModelUtils.isPartitionPartiallyImplemented(fSelectedPartition));
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
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2));
		Button applyButton = getToolkit().createButton(buttonComposite, "Change", SWT.CENTER);
		applyButton.addSelectionListener(new ApplyChangesSelectionAdapter());
		fImplementButton = getToolkit().createButton(buttonComposite, "Implement", SWT.NONE);
		fImplementButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				ModelImplementor implementor = new ModelImplementor();
				if (!implementor.compilationUnitExists(fSelectedPartition.getCategory().getType())) {
					ProjectSelectionDialog dialog = new ProjectSelectionDialog(Display.getCurrent().getActiveShell());
					if (dialog.open() == IDialogConstants.OK_ID) {
						IJavaProject selectedProject = (IJavaProject)dialog.getFirstResult();
						implementor.setProjectName(selectedProject.getProject().getName());
						implementor.implement(fSelectedPartition);
					}
				} else {
					implementor.implement(fSelectedPartition);
				}
				try {
					ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (CoreException f) {
				}
			}
		});
		
		getToolkit().paintBordersFor(parent);

	}

	private void createValueEdit(Composite parent) {
		getToolkit().createLabel(parent, "Value");
		Composite valueComposite = getToolkit().createComposite(parent);
		fComboLayout = new StackLayout();
		valueComposite.setLayout(fComboLayout);
		valueComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fPartitionValueCombo = new Combo(valueComposite,SWT.DROP_DOWN);
		fPartitionValueCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fPartitionValueCombo.addListener(SWT.KeyDown, new valueTextListener());
		fPartitionValueCombo.addSelectionListener(new valueComboSelectionAdapter());
		// boolean value combo
		fBooleanValueCombo = new Combo(valueComposite,SWT.READ_ONLY);
		fBooleanValueCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fBooleanValueCombo.addSelectionListener(new booleanValueComboSelectionAdapter());	
		
		getToolkit().paintBordersFor(parent);	
	}
	
	private void prepareDefaultValues(PartitionNode node, Combo valueText){
		HashMap<String, String> values = ModelUtils.generatePredefinedValues(node.getCategory().getType());
		String [] items = new String[values.values().size()];
		items = values.values().toArray(items);
		ArrayList<String> newItems = new ArrayList<String>();

		valueText.setItems(items);
		for (int i = 0; i < items.length; ++i) {
			newItems.add(items[i]);
			if (items[i].equals(node.getValueString())) {
				return;
			}
		}
		newItems.add(node.getValueString());
		valueText.setItems(newItems.toArray(items));
	}
	
}
