/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import java.util.ArrayList;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.ecfeed.ui.common.Messages;

public class TestClassSelectionDialog extends ElementTreeSelectionDialog {
    protected static final IStatus OK = new Status(IStatus.OK, "com.ecfeed", "");
    protected static final IStatus ERROR = new Status(IStatus.ERROR, "com.ecfeed",
    		"Select class element");

    protected class TypeSelectionValidator implements ISelectionStatusValidator{

		@Override
		public IStatus validate(Object[] selection) {
    		if(selection.length != 1){
    			return ERROR;
    		}
    		if(selection[0] instanceof IType == false){
    			return ERROR;
    		}
    		return OK;
		}
    }

    protected static class TypeContentProvider extends StandardJavaElementContentProvider{

    	public TypeContentProvider() {
    		super(true);
		}

    	@Override
		public Object[] getChildren(Object element){
			ArrayList<Object> children = new ArrayList<Object>();

			//Filter unwanted elements
			for(Object child : super.getChildren(element)){
				if((child instanceof IType == false) && (hasChildren(child) == false)){
					continue;
				}
				children.add(child);
			}
			return children.toArray();
		}
    }

	public TestClassSelectionDialog(Shell parent) {
		super(parent, new WorkbenchLabelProvider(), new TypeContentProvider());

		setTitle(getDialogTitle());
		setMessage(getDialogMessage());
		setInput(getViewerInput());

		setValidator(getSelectionValidator());
	}

	protected ISelectionStatusValidator getSelectionValidator() {
		return new TypeSelectionValidator();
	}

	protected String getDialogTitle() {
		return Messages.DIALOG_TEST_CLASS_SELECTION_TITLE;
	}

	protected Object getViewerInput() {
		return JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
	}

	protected String getDialogMessage(){
		return Messages.DIALOG_TEST_CLASS_SELECTION_MESSAGE;
	}
}
