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
	boolean fPackageName;

	public ClassNameEditingSupport(ClassViewer viewer, boolean packageName) {
		super(viewer.getTableViewer());
		fSection = viewer;
		fPackageName = packageName;
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
		if (fPackageName) {
			String qualifiedName = ((ClassNode)element).getQualifiedName();
			return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
		} else {
			return ((ClassNode)element).getLocalName();
		}
	}

	@Override
	protected void setValue(Object element, Object value) {
		String newName = (String)value;
		boolean doRename = true;

		if(!fPackageName){
			if(!ModelUtils.validateNodeName(newName)){
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_CLASS_NAME_PROBLEM_TITLE,
						Messages.DIALOG_CLASS_NAME_PROBLEM_MESSAGE);
				return;
			}	
		}
		
		ClassNode classNode = (ClassNode)element;
		int lastDotIndex = classNode.getQualifiedName().lastIndexOf('.');
		String newQualifiedName = classNode.getQualifiedName().substring(0, lastDotIndex + 1) + newName;
		
		if (fPackageName) {
			newQualifiedName = newName + classNode.getQualifiedName().substring(lastDotIndex);
		}

		boolean validName = ModelUtils.isClassQualifiedNameValid(newQualifiedName);
		if (!validName) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_PACKAGE_NAME_PROBLEM_TITLE,
					Messages.DIALOG_PACKAGE_NAME_PROBLEM_MESSAGE);
		}

		if (validName && !classNode.getName().equals(newQualifiedName)) {
			if (classNode.getRoot().getClassModel(newQualifiedName) == null) {
				if (ModelUtils.isClassImplemented((ClassNode)element)) {
					doRename = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_CLASS_EXISTS_TITLE,
						Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE);
				}
				if (doRename) {
					classNode.setName(newQualifiedName);
					fSection.modelUpdated();
				}
			} else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_CLASS_EXISTS_TITLE,
						Messages.DIALOG_CLASS_EXISTS_MESSAGE);
			}
		}
	}
}
