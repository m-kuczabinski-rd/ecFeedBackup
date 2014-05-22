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

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.utils.ModelUtils;

public class TestDataValueEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	private List<PartitionNode> fTestData;
	private ComboBoxViewerCellEditor fComboCellEditor = null;
	private TextCellEditor fTextCellEditor;
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
		if(partition.getCategory().isExpected()){
			return getTextCellEditor(partition);
		}
		else{
			return getComboCellEditor(partition);
		}
	}

	private CellEditor getComboCellEditor(PartitionNode partition) {
		if(fComboCellEditor == null){
			fComboCellEditor = new ComboBoxViewerCellEditor(fViewer.getTable(), SWT.TRAIL);
			fComboCellEditor.setLabelProvider(new LabelProvider());
			fComboCellEditor.setContentProvider(new ArrayContentProvider());
			fComboCellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_KEY_ACTIVATION | 
					ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
		}
		fComboCellEditor.setInput(partition.getCategory().getLeafPartitions());
		fComboCellEditor.setValue(partition);
		return fComboCellEditor;
	}

	private CellEditor getTextCellEditor(PartitionNode partition) {
		if(fTextCellEditor == null){
			fTextCellEditor = new TextCellEditor(fViewer.getTable(), SWT.LEFT); 
		}
		String valueString = partition.getValueString();
		fTextCellEditor.setValue(valueString);
		return fTextCellEditor;
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
		if(value instanceof String && category.isExpected()){
			String valueString = (String)value;
			if(ModelUtils.validatePartitionStringValue(valueString, category.getType())){
				Object newValue = ModelUtils.getPartitionValueFromString(valueString, category.getType());
				if(newValue.equals(partitionElement.getValue()) == false){
					((PartitionNode)element).setValue(newValue);
					fSetValueListener.testDataChanged();
				}
			}
		}
		else if(value instanceof PartitionNode){
			PartitionNode partitionValue = (PartitionNode)value;
			int parentIndex = category.getMethod().getCategories().indexOf(category);
			if(parentIndex >= 0 && parentIndex <= fTestData.size()){
				fTestData.set(parentIndex, partitionValue);
				fSetValueListener.testDataChanged();
			}
		}
	}
}
