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

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class MethodNameEditingSupport extends EditingSupport{

	private TextCellEditor fNameCellEditor;
	BasicSection fSection;
	private MethodInterface fMethodIf;

	public MethodNameEditingSupport(MethodsViewer viewer, ModelOperationManager operationManager) {
		super(viewer.getTableViewer());
		fMethodIf = new MethodInterface(operationManager);
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
		fMethodIf.setTarget((MethodNode)element);
		return fMethodIf.getName();
	}

	@Override
	protected void setValue(Object element, Object value) {
		String newName = (String)value;
		MethodNode method = (MethodNode)element;
		fMethodIf.setTarget(method);
		fMethodIf.setName(newName, fSection, fSection.getUpdateListener());
	}
}
