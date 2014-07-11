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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.utils.ModelUtils;

public class PartitionValueEditingSupport extends EditingSupport {
	private ComboBoxCellEditor fCellEditor;
	private BasicSection fSection;
	
	public PartitionValueEditingSupport(CategoryChildrenViewer viewer) {
		super(viewer.getTableViewer());
		String[] items = {""};
		fCellEditor = new ComboBoxCellEditor(viewer.getTable(), items);
		fSection = viewer;
	}

	public PartitionValueEditingSupport(PartitionChildrenViewer viewer) {
		super(viewer.getTableViewer());
		String[] items = {""};
		fCellEditor = new ComboBoxCellEditor(viewer.getTable(), items);
		fSection = viewer;
	}
	
	@Override
	protected CellEditor getCellEditor(Object element) {
		PartitionNode node = (PartitionNode)element;
		if (!ModelUtils.getJavaTypes().contains(node.getCategory().getType())
				|| node.getCategory().getType().equals(com.testify.ecfeed.gal.Constants.TYPE_NAME_BOOLEAN)) {
			fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
		} else {
			fCellEditor.setActivationStyle(SWT.NONE);
		}
		return fCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return !((PartitionNode)element).isAbstract();
	}

	@Override
	protected Object getValue(Object element) {
		PartitionNode node = (PartitionNode)element;
		HashMap<String, String> values = ModelUtils.generatePredefinedValues(node.getCategory().getType());
		String [] items = new String[values.size()];
		items = values.values().toArray(items);
		ArrayList<String> newItems = new ArrayList<String>();

		fCellEditor.setItems(items);
		for (int i = 0; i < items.length; ++i) {
			newItems.add(items[i]);
			if (items[i].equals(node.getValueString())) {
				return i;
			}
		}

		newItems.add(node.getValueString());
		fCellEditor.setItems(newItems.toArray(items));
		return (newItems.size() - 1);
	}

	@Override
	protected void setValue(Object element, Object value) {
		String valueString = null;
		int index = (int)value;

		if (index >= 0) {
			valueString = fCellEditor.getItems()[index];
		} else {
			valueString = ((CCombo)fCellEditor.getControl()).getText();
		}

		if(!ModelUtils.validatePartitionStringValue(valueString, getCategory().getType())) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
					Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE);
		} else {
			PartitionNode partition = (PartitionNode)element;
			if (valueString.equals(partition.getValueString()) == false) {
				((PartitionNode)element).setValueString(valueString);
				fSection.modelUpdated();
			}
		}
	}

	private CategoryNode getCategory(){
		if(fSection instanceof CategoryChildrenViewer){
			return ((CategoryChildrenViewer)fSection).getSelectedCategory();
		}
		else if(fSection instanceof PartitionChildrenViewer){
			return ((PartitionChildrenViewer)fSection).getSelectedPartition().getCategory();
		}
		return null;
	}
}
