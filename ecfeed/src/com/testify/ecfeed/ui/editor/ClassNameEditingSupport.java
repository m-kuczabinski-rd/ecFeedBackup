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

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.utils.ModelUtils;

public class ClassNameEditingSupport extends EditingSupport{

	private TextCellEditor fNameCellEditor;
	BasicSection fSection;
	boolean qualifiedName;

	public ClassNameEditingSupport(ClassViewer viewer, boolean qualified) {
		super(viewer.getTableViewer());
		fSection = viewer;
		qualifiedName = qualified;
		fNameCellEditor = new TextCellEditor(viewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		boolean doRename = true;
		if (ModelUtils.isClassImplemented((ClassNode)element)) {
			doRename = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
				Messages.DIALOG_CLASS_EXISTS_TITLE,
				Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE);
		}
		return doRename ? fNameCellEditor : null;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		if (qualifiedName) {
			return ((ClassNode)element).getQualifiedName();
		} else {
			return ((ClassNode)element).getLocalName();
		}
	}

	@Override
	protected void setValue(Object element, Object value) {
		String newName = (String)value;
		String newQualifiedName = newName;
		ClassNode classNode = (ClassNode)element;
		
		if (newName.length() > 0) {
			if (!qualifiedName) {
				int lastDotIndex = classNode.getName().lastIndexOf('.');
				newQualifiedName = (lastDotIndex == -1) ? newName : classNode.getName().substring(0, lastDotIndex + 1) + newName;
			}
			
			if (!classNode.getName().equals(newQualifiedName)) {
				if (classNode.getRoot().getClassModel(newQualifiedName) == null) {
					classNode.setName(newQualifiedName);
					fSection.modelUpdated();
				} else {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), 
							Messages.DIALOG_CLASS_EXISTS_TITLE,
							Messages.DIALOG_CLASS_EXISTS_MESSAGE);
				}
			}
		}
	}
}
