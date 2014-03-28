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

import com.testify.ecfeed.model.ExpectedCategoryNode;

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
		String valueString = ((ExpectedCategoryNode)element).getDefaultValuePartition().getValueString();
		editor.setValue(valueString);
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return element instanceof ExpectedCategoryNode;
	}

	@Override
	protected Object getValue(Object element) {
		return ((ExpectedCategoryNode)element).getDefaultValuePartition().getValueString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		ExpectedCategoryNode category = (ExpectedCategoryNode)element;
		String valueString = (String)value;
		if(category.validatePartitionStringValue(valueString)){
			Object newValue = category.getPartitionValueFromString(valueString);
			category.setDefaultValue(newValue);
		}
		fSetValueListener.testDataChanged();
	}

}
