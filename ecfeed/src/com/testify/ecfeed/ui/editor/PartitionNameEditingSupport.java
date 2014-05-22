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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.Messages;

public class PartitionNameEditingSupport extends EditingSupport{

	private TextCellEditor fNameCellEditor;
	BasicSection fSection;

	public PartitionNameEditingSupport(CategoryChildrenViewer viewer) {
		super(viewer.getTableViewer());
		fSection = viewer;
		fNameCellEditor = new TextCellEditor(viewer.getTable());
	}

	public PartitionNameEditingSupport(PartitionChildrenViewer viewer) {
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
		return ((PartitionNode)element).getName();
	}

	@Override
	protected void setValue(Object element, Object value) {
		String newName = (String)value;
		PartitionNode partition = (PartitionNode)element;
		if(partition.getName().equals(newName)) return;
		if(!getCategory().validatePartitionName(newName) || 
				partition.hasSibling(newName)){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_PARTITION_NAME_PROBLEM_TITLE, 
					Messages.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE);
		}
		else{
			((PartitionNode)element).setName((String)value);
			fSection.modelUpdated();
		}
	}

	private CategoryNode getCategory() {
		if(fSection instanceof CategoryChildrenViewer){
			return ((CategoryChildrenViewer)fSection).getSelectedCategory();
		}
		else if(fSection instanceof PartitionChildrenViewer){
			return ((PartitionChildrenViewer)fSection).getSelectedPartition().getCategory();
		}
		return null;
	}

}
