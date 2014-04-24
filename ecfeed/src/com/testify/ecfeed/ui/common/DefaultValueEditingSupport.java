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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.utils.ModelUtils;

public class DefaultValueEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	private TestDataEditorListener fSetValueListener;

	public DefaultValueEditingSupport(TableViewer viewer, TestDataEditorListener setValueListener) {
		super(viewer);
		fViewer = viewer;
		fSetValueListener = setValueListener;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		TextCellEditor editor = new TextCellEditor(fViewer.getTable(), SWT.LEFT);
		String valueString = ((ExpectedValueCategoryNode)element).getDefaultValuePartition().getValueString();
		editor.setValue(valueString);
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return element instanceof ExpectedValueCategoryNode;
	}

	@Override
	protected Object getValue(Object element) {
		return ((ExpectedValueCategoryNode)element).getDefaultValuePartition().getValueString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		ExpectedValueCategoryNode category = (ExpectedValueCategoryNode)element;
		String valueString = (String)value;
		if(ModelUtils.validatePartitionStringValue(valueString, category.getType())){
			Object newValue = ModelUtils.getPartitionValueFromString(valueString, category.getType());
			category.setDefaultValue(newValue);
		}
		fSetValueListener.testDataChanged();
	}

}
