/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Mariusz Strozynski (m.strozynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.custom.CCombo;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.CategoryInterface;

public class CategoryTypeEditingSupport extends EditingSupport {

	private ComboBoxCellEditor fCellEditor;
	private BasicSection fSection;
	private CategoryInterface fCategoryIf;

	public CategoryTypeEditingSupport(ParametersViewer viewer, ModelOperationManager operationManager) {
		super(viewer.getTableViewer());
		fCategoryIf = new CategoryInterface(operationManager);
		fSection = viewer;
		fCellEditor = new ComboBoxCellEditor(viewer.getTable(), CategoryInterface.supportedPrimitiveTypes());
		fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return fCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		CategoryNode node = (CategoryNode)element;
		String [] items = fCellEditor.getItems();
		ArrayList<String> newItems = new ArrayList<String>();

		for (int i = 0; i < items.length; ++i) {
			newItems.add(items[i]);
			if (items[i].equals(node.getType())) {
				return i;
			}
		}

		newItems.add(node.getType());
		fCellEditor.setItems(newItems.toArray(items));
		return (newItems.size() - 1);
	}

	@Override
	protected void setValue(Object element, Object value) {
		CategoryNode node = (CategoryNode)element;
		String newType = null;
		int index = (int)value;

		if (index >= 0) {
			newType = fCellEditor.getItems()[index];
		} else {
			newType = ((CCombo)fCellEditor.getControl()).getText();
		}
		fCategoryIf.setTarget(node);
		fCategoryIf.setType(newType, fSection, fSection.getUpdateListener());

		fCellEditor.setFocus();
	}
}
