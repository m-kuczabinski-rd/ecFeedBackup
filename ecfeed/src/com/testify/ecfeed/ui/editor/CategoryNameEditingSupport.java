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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.ui.common.Messages;

public class CategoryNameEditingSupport extends EditingSupport {

	private TextCellEditor fNameCellEditor;
	BasicSection fSection;

	public CategoryNameEditingSupport(ParametersViewer viewer) {
		super(viewer.getTableViewer());
		fSection = viewer;
		fNameCellEditor = new TextCellEditor(viewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return fNameCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((CategoryNode)element).getName();
	}

	@Override
	protected void setValue(Object element, Object value) {
		String newName = (String)value;
		CategoryNode categoryNode = (CategoryNode)element;
		
		if ((newName.length() > 0) && !categoryNode.getName().equals(newName)) {
			if (categoryNode.getMethod().getCategory(newName) == null) {
				categoryNode.setName(newName);
				fSection.modelUpdated();
			} else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_CATEGORY_EXISTS_TITLE,
						Messages.DIALOG_CATEGORY_EXISTS_MESSAGE);
			}
		}
	}
}