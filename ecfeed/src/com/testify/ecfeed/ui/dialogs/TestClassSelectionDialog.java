/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs;

import java.util.ArrayList;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.testify.ecfeed.constants.DialogStrings;

public class TestClassSelectionDialog extends ElementTreeSelectionDialog {

    private static final IStatus OK = new Status(IStatus.OK, "com.testify.ecfeed", "");
    private static final IStatus ERROR = new Status(IStatus.ERROR, "com.testify.ecfeed", 
    		"Select class with methods annotated with @Test");
    
	public TestClassSelectionDialog(Shell parent) {
		super(parent, new WorkbenchLabelProvider(), new StandardJavaElementContentProvider(true){
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
		});
		setTitle(DialogStrings.DIALOG_TEST_CLASS_SELECTION_TITLE);
		setMessage(DialogStrings.DIALOG_TEST_CLASS_SELECTION_MESSAGE);
		setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
		
		setValidator(fTestClassSelectionValidator);
	}

    private ISelectionStatusValidator fTestClassSelectionValidator = new ISelectionStatusValidator() {
        public IStatus validate(Object[] selection) {
    		if(selection.length != 1){
    			return ERROR;
    		}
    		if(selection[0] instanceof IType == false){
    			return ERROR;
    		}
    		
    		IType type = (IType)selection[0];

    		try{
    			for(IMethod method : type.getMethods()){
    				for(IAnnotation annotation : method.getAnnotations()){
    					if(annotation.getElementName().equals("Test")){
    						return OK;
    					}
    				}
    			}
    		}catch(JavaModelException e){
    			System.out.println("Class parsing error" + e.getMessage());
    		}
    		return ERROR;
        }
    };
}
