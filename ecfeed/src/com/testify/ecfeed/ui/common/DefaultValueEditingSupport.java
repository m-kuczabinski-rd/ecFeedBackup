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

package com.testify.ecfeed.ui.common;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.utils.ModelUtils;

public class DefaultValueEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	private TestDataEditorListener fSetValueListener;
	private ComboBoxViewerCellEditor fComboCellEditor = null;
	

	public DefaultValueEditingSupport(TableViewer viewer, TestDataEditorListener setValueListener) {
		super(viewer);
		fViewer = viewer;
		fSetValueListener = setValueListener;
	}
	
	@Override
	protected CellEditor getCellEditor(Object element) {
		PartitionNode partition = ((CategoryNode)element).getDefaultValuePartition();
		return getComboCellEditor(partition);
	}

	@Override
	protected boolean canEdit(Object element) {
		return (element instanceof CategoryNode && ((CategoryNode)element).isExpected());
	}

	@Override
	protected Object getValue(Object element) {
		return ((CategoryNode)element).getDefaultValuePartition().getValueString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		CategoryNode category = (CategoryNode)element;
		String valueString = null;
		if(value instanceof String){
			valueString = (String)value;
		} else if(value == null){
			valueString = fComboCellEditor.getViewer().getCCombo().getText();
		}
		if(!valueString.equals(category.getDefaultValueString())){
			if(!ModelUtils.validatePartitionStringValue(valueString, category.getType())){
				MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE,
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE);
			} else{
				category.setDefaultValueString(valueString);
				fSetValueListener.testDataChanged();
			}
		}
	}
	
	private CellEditor getComboCellEditor(PartitionNode partition) {
		if(fComboCellEditor == null){
			fComboCellEditor = new ComboBoxViewerCellEditor(fViewer.getTable(), SWT.TRAIL);
			fComboCellEditor.setLabelProvider(new LabelProvider());
			fComboCellEditor.setContentProvider(new ArrayContentProvider());
		}
		ArrayList<String> expectedValues = new ArrayList<String>();
		for(PartitionNode node : ModelUtils.generateDefaultPartitions(partition.getCategory().getType())){
			expectedValues.add(node.getValueString());
		}
		if(!expectedValues.contains(partition.getValueString())){
			expectedValues.add(partition.getValueString());
		}
		for(PartitionNode leaf : partition.getCategory().getLeafPartitions()){
			if(!expectedValues.contains(leaf.getValueString())){
				expectedValues.add(leaf.getValueString());
			}
		}

		fComboCellEditor.setInput(expectedValues);
		fComboCellEditor.setValue(partition.getValueString());

		if(ModelUtils.getJavaTypes().contains(partition.getCategory().getType())
				&& !partition.getCategory().getType().equals(com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN)){
			fComboCellEditor.getViewer().getCCombo().setEditable(true);
		} else{
			fComboCellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_KEY_ACTIVATION
					| ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
			fComboCellEditor.getViewer().getCCombo().setEditable(false);
		}
		return fComboCellEditor;
	}

}
