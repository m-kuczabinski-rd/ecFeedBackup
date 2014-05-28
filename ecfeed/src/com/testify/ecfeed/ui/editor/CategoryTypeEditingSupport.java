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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.utils.AdaptTypeSupport;
import com.testify.ecfeed.utils.ModelUtils;

public class CategoryTypeEditingSupport extends EditingSupport {

	private ComboBoxCellEditor fCellEditor;
	private BasicSection fSection;

	public CategoryTypeEditingSupport(ParametersViewer viewer) {
		super(viewer.getTableViewer());
		fSection = viewer;
		String[] items = {"boolean", "byte", "char", "short", "int", "long", "float", "double", "String"};
		fCellEditor = new ComboBoxCellEditor(viewer.getTable(), items);
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
		boolean validName = true;

		if (index >= 0) {
			newType = fCellEditor.getItems()[index];
		} else {
			newType = ((CCombo)fCellEditor.getControl()).getText();
			validName = ModelUtils.validateNodeName(newType);
		}

		if (!validName) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_PARAMETER_TYPE_PROBLEM_TITLE,
					Messages.DIALOG_PARAMETER_TYPE_PROBLEM_MESSAGE);
		}

		if (validName && !node.getType().equals(newType)) {
			AdaptTypeSupport.changeCategoryType(node, newType);
			fSection.modelUpdated();
		}

		fCellEditor.setFocus();
	}
}
