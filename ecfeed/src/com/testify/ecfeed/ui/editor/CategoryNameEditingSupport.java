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
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.CategoryInterface;

public class CategoryNameEditingSupport extends EditingSupport {

	private TextCellEditor fNameCellEditor;
	BasicSection fSection;
	CategoryInterface fCategoryIf;

	public CategoryNameEditingSupport(ParametersViewer viewer, ModelOperationManager operationManager) {
		super(viewer.getTableViewer());
		fCategoryIf = new CategoryInterface(operationManager);
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
		fCategoryIf.setTarget((CategoryNode)element);
		fCategoryIf.setName((String)value, fSection, fSection.getUpdateListener());
	}
}
