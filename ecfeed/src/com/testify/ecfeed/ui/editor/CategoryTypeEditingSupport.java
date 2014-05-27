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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

import com.testify.ecfeed.model.CategoryNode;

public class CategoryTypeEditingSupport extends EditingSupport {

	private TextCellEditor fNameCellEditor;
	BasicSection fSection;

	public CategoryTypeEditingSupport(ParametersViewer viewer) {
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
		return ((CategoryNode)element).getType();
	}

	@Override
	protected void setValue(Object element, Object value) {
		String newType = (String)value;
		CategoryNode categoryNode = (CategoryNode)element;
		
		if ((newType.length() > 0) && !categoryNode.getType().equals(newType)) {
			categoryNode.setType(newType);
			fSection.modelUpdated();
		}
	}
}