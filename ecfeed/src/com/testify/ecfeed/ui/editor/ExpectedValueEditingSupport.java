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
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.ui.common.Messages;

public class ExpectedValueEditingSupport extends EditingSupport {

	private ComboBoxCellEditor fCellEditor;
	BasicSection fSection;

	public ExpectedValueEditingSupport(ParametersViewer viewer) {
		super(viewer.getTableViewer());
		fSection = viewer;
		String[] items = {"Yes", "No"};
		fCellEditor = new ComboBoxCellEditor(viewer.getTable(), items, SWT.READ_ONLY);
		fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return fCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		CategoryNode node = (CategoryNode)element;
		return (node.isExpected() ? 0 : 1);
	}

	@Override
	protected void setValue(Object element, Object value) {
		CategoryNode node = (CategoryNode)element;
		boolean expected = ((int)value == 0) ? true : false;
		if (node.isExpected() != expected) {
			// checking if there is any reason to display warning  - test cases and constraints
			boolean warn  = false;
			if(node.getMethod().getTestCases().isEmpty()){
				for(ConstraintNode constraint : node.getMethod().getConstraintNodes()){
					if(constraint.mentions(node)){
						warn = true;
						break;
					}
				}
			} else{
				warn =  true;
			}
			if(warn){
				if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_DATA_MIGHT_BE_LOST_TITLE,
						Messages.DIALOG_DATA_MIGHT_BE_LOST_MESSAGE)) {
					node.getMethod().changeCategoryExpectedStatus(node, expected);
					fSection.modelUpdated();
				}
			} else {
				node.getMethod().changeCategoryExpectedStatus(node, expected);
				fSection.modelUpdated();
			}
		}
		fCellEditor.setFocus();
	}
}
