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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.PartitionInterface;

public class PartitionNameEditingSupport extends EditingSupport{

	private TextCellEditor fNameCellEditor;
	private BasicSection fSection;
	private PartitionInterface fPartitionIf;

	public PartitionNameEditingSupport(CheckboxTableViewerSection viewer, ModelOperationManager operationManager) {
		super(viewer.getTableViewer());
		fPartitionIf = new PartitionInterface(operationManager);
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
		return ((PartitionNode)element).getName();
	}

	@Override
	protected void setValue(Object element, Object value) {
		String newName = (String)value;
		PartitionNode partition = (PartitionNode)element;
		
		if(newName.equals(partition.getName()) == false){
			fPartitionIf.setTarget(partition);
			fPartitionIf.setName(newName, fSection, fSection.getUpdateListener());
		}
	}
}
