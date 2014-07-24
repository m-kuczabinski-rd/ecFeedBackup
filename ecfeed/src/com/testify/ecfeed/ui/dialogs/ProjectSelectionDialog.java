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

package com.testify.ecfeed.ui.dialogs;

import java.util.ArrayList;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.testify.ecfeed.ui.common.Messages;

public class ProjectSelectionDialog extends ElementTreeSelectionDialog {

    private static final IStatus OK = new Status(IStatus.OK, "com.testify.ecfeed", "");
    private static final IStatus ERROR = new Status(IStatus.ERROR, "com.testify.ecfeed", "Select project element");

	public ProjectSelectionDialog(Shell parent) {
		super(parent, new WorkbenchLabelProvider(), new StandardJavaElementContentProvider(true) {
			@Override
			public Object[] getChildren(Object element){
				ArrayList<Object> children = new ArrayList<Object>();
				for (Object child : super.getChildren(element)){
					if (child instanceof IJavaProject == true) {
						children.add(child);
					}
				}
				return children.toArray();
			}
		});

		setTitle(Messages.DIALOG_PROJECT_SELECTION_TITLE);
		setMessage(Messages.DIALOG_PROJECT_SELECTION_MESSAGE);
		setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
		setValidator(fProjectSelectionValidator);
	}

    private ISelectionStatusValidator fProjectSelectionValidator = new ISelectionStatusValidator() {
        public IStatus validate(Object[] selection) {
    		if ((selection.length != 1) || (selection[0] instanceof IJavaProject == false)) {
    			return ERROR;
    		}
    		return OK;
        }
    };
}
