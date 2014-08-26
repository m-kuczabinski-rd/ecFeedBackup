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

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.PartitionInterface;

public class PartitionValueEditingSupport extends EditingSupport {
	private ComboBoxViewerCellEditor fCellEditor;
	private CheckboxTableViewerSection fSection;
	private PartitionInterface fPartitionIf;
	
	public PartitionValueEditingSupport(CheckboxTableViewerSection viewer, ModelOperationManager operationManager) {
		super(viewer.getTableViewer());
		fSection = viewer;

		fCellEditor = new ComboBoxViewerCellEditor(viewer.getTable(), SWT.TRAIL);
		fCellEditor.setLabelProvider(new LabelProvider());
		fCellEditor.setContentProvider(new ArrayContentProvider());

		fPartitionIf = new PartitionInterface(operationManager);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		PartitionNode node = (PartitionNode)element;
		if(CategoryInterface.hasLimitedValuesSet(node.getCategory())){
			fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
		} else {
			fCellEditor.setActivationStyle(SWT.NONE);
		}
		List<String> items = CategoryInterface.getSpecialValues(node.getCategory().getType());
		if(items.contains(node.getValueString()) == false){
			items.add(node.getValueString());
		}
		fCellEditor.setInput(items);
		fCellEditor.getViewer().getCCombo().setEditable(CategoryInterface.isBoolean(node.getCategory().getType()) == false);
		return fCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return ((PartitionNode)element).isAbstract() == false;
	}

	@Override
	protected Object getValue(Object element) {
		return ((PartitionNode)element).getValueString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		String valueString = null;
		if(value instanceof String){
			valueString = (String)value;
		} else if(value == null){
			valueString = fCellEditor.getViewer().getCCombo().getText();
		}
		fPartitionIf.setTarget((PartitionNode)element);
		fPartitionIf.setValue(valueString, fSection, fSection.getUpdateListener());
	}
}
