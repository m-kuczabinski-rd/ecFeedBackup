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

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.utils.ModelUtils;

public class MethodNameEditingSupport extends EditingSupport{

	private TextCellEditor fNameCellEditor;
	BasicSection fSection;

	public MethodNameEditingSupport(MethodsViewer viewer) {
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
		String name = ((MethodNode)element).toString();
		return name.substring(0, name.indexOf('('));
	}

	@Override
	protected void setValue(Object element, Object value) {
		String newName = (String)value;
		MethodNode methodNode = (MethodNode)element;
		boolean validName = ModelUtils.validateNodeName(newName);
		if (!validName) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_METHOD_NAME_PROBLEM_TITLE,
					Messages.DIALOG_METHOD_NAME_PROBLEM_MESSAGE);
		}
		if (validName && !methodNode.getName().equals(newName)) {
			if (methodNode.getClassNode().getMethod(newName, methodNode.getCategoriesTypes()) == null) {
				methodNode.setName(newName);
				fSection.modelUpdated();
			} else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_METHOD_EXISTS_TITLE,
						Messages.DIALOG_METHOD_WITH_PARAMETERS_EXISTS_MESSAGE);
			}
		}
	}
}
