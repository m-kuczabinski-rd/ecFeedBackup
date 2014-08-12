/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.ConstraintInterface;

public class ConstraintNameEditingSupport extends EditingSupport{

	private TextCellEditor fNameCellEditor;
	BasicSection fSection;
	private ConstraintInterface fConstraintIf;

	public ConstraintNameEditingSupport(ConstraintsListViewer viewer, ModelOperationManager operationManager){
		super(viewer.getTableViewer());
		fSection = viewer;
		fNameCellEditor = new TextCellEditor(viewer.getTable());
		fConstraintIf = new ConstraintInterface(operationManager);
	}

	@Override
	protected CellEditor getCellEditor(Object element){
		return fNameCellEditor;
	}

	@Override
	protected boolean canEdit(Object element){
		return true;
	}

	@Override
	protected Object getValue(Object element){
		return ((ConstraintNode)element).getName();
	}

	@Override
	protected void setValue(Object element, Object value){
		String newName = (String)value;
		ConstraintNode constraint = (ConstraintNode)element;
		if(newName.equals(constraint.getName()) == false){
			fConstraintIf.setTarget(constraint);
			fConstraintIf.setName(newName, fSection, fSection.getUpdateListener());
		}
	}
	
}
