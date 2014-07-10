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
import java.util.List;

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

public class TestDataValueEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	private List<PartitionNode> fTestData;
	private ComboBoxViewerCellEditor fComboCellEditor = null;
	private TestDataEditorListener fSetValueListener;

	public TestDataValueEditingSupport(TableViewer viewer, List<PartitionNode> testData, TestDataEditorListener setValueListener) {
		super(viewer);
		fViewer = viewer;
		fTestData = testData;
		fSetValueListener = setValueListener;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		PartitionNode partition = (PartitionNode)element;
		return getComboCellEditor(partition);
	}

	private CellEditor getComboCellEditor(PartitionNode partition) {
		if(fComboCellEditor == null){
			fComboCellEditor = new ComboBoxViewerCellEditor(fViewer.getTable(), SWT.TRAIL);
			fComboCellEditor.setLabelProvider(new LabelProvider());
			fComboCellEditor.setContentProvider(new ArrayContentProvider());
		}
		if (partition.getCategory().isExpected()) {
			ArrayList<String> expectedValues = new ArrayList<String>();
			for (PartitionNode node : ModelUtils.generateDefaultPartitions(partition.getCategory().getType())) {
				expectedValues.add(node.getValueString());
			}
			if (!expectedValues.contains(partition.getValueString())) {
				expectedValues.add(partition.getValueString());
			}
			for(PartitionNode leaf : partition.getCategory().getLeafPartitions()){
				if(!expectedValues.contains(leaf.getValueString())){
					expectedValues.add(leaf.getValueString());
				}
			}
			
			fComboCellEditor.setInput(expectedValues);
			fComboCellEditor.setValue(partition.getValueString());

			if (ModelUtils.getJavaTypes().contains(partition.getCategory().getType())
					&& !partition.getCategory().getType().equals(com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN)) {
				fComboCellEditor.getViewer().getCCombo().setEditable(true);
			} else {
				fComboCellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_KEY_ACTIVATION |
						ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
				fComboCellEditor.getViewer().getCCombo().setEditable(false);
			}
		} else {
			fComboCellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_KEY_ACTIVATION |
					ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
			fComboCellEditor.setInput(partition.getCategory().getLeafPartitions());
			fComboCellEditor.getViewer().getCCombo().setEditable(false);
			fComboCellEditor.setValue(partition);
		}
		return fComboCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		PartitionNode partition = (PartitionNode)element;
		if(partition.getCategory().isExpected()){
			return partition.getValueString();
		}
		return partition.toString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		PartitionNode partitionElement = (PartitionNode)element;
		CategoryNode category = partitionElement.getCategory();
		String valueString = null;
		if (category.isExpected()) {
			if (value instanceof String) {
				valueString = (String)value;
			} else if (value == null){
				valueString = fComboCellEditor.getViewer().getCCombo().getText();
			}
			if (!valueString.equals(partitionElement.getValueString())) {
				if (!ModelUtils.validatePartitionStringValue(valueString, category.getType())) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE,
							Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE);
				} else {
					((PartitionNode)element).setValueString(valueString);
					fSetValueListener.testDataChanged();
				}
			}
		} else if (value instanceof PartitionNode) {
			PartitionNode partitionValue = (PartitionNode)value;
			int parentIndex = category.getMethod().getCategories().indexOf(category);
			if(parentIndex >= 0 && parentIndex <= fTestData.size()) {
				fTestData.set(parentIndex, partitionValue);
				fSetValueListener.testDataChanged();
			}
		}
	}
}
