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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.adapter.ITypeAdapter;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class TestDataValueEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	private ComboBoxViewerCellEditor fComboCellEditor;
	private ITestDataEditorListener fSetValueListener;

	public TestDataValueEditingSupport(TableViewer viewer, List<PartitionNode> testData, ITestDataEditorListener setValueListener) {
		super(viewer);
		fViewer = viewer;
		fSetValueListener = setValueListener;

		fComboCellEditor = new ComboBoxViewerCellEditor(fViewer.getTable(), SWT.TRAIL);
		fComboCellEditor.setLabelProvider(new LabelProvider());
		fComboCellEditor.setContentProvider(new ArrayContentProvider());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		PartitionNode partition = (PartitionNode)element;
		return getComboCellEditor(partition);
	}

	private CellEditor getComboCellEditor(PartitionNode partition) {
		String type = partition.getCategory().getType();
		EclipseModelBuilder builder = new EclipseModelBuilder();
		
		if (partition.getCategory().isExpected()) {
			Set<String> expectedValues = new HashSet<String>();
			for (String specialValue : builder.getSpecialValues(type)) {
				expectedValues.add(specialValue);
			}
			if (expectedValues.contains(partition.getValueString()) == false) {
				expectedValues.add(partition.getValueString());
			}
			fComboCellEditor.setInput(expectedValues);

			if (JavaUtils.hasLimitedValuesSet(type) == false) {
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
		PartitionNode current = (PartitionNode)element;
		CategoryNode category = current.getCategory();
		int index = category.getIndex();
		PartitionNode newValue = null;
		if(category.isExpected()){
			String valueString = fComboCellEditor.getViewer().getCCombo().getText();
			String type = category.getType(); 
			ITypeAdapter adapter = new EclipseTypeAdapterProvider().getAdapter(type);
			if(adapter.convert(valueString) == null){
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE,
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE(valueString));
				return;
			}
			else if(valueString.equals(current.getValueString()) == false){
				newValue = current.getCopy();
				newValue.setValueString(valueString);
			}
		}
		else if(value instanceof PartitionNode){
			if((PartitionNode)value != current){
				newValue = (PartitionNode)value;
			}
		}
		if(newValue != null){
			fSetValueListener.testDataChanged(index, newValue);
		}
	}
}
