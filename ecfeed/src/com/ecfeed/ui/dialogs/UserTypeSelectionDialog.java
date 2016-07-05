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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.JavaModelAnalyser;
import com.ecfeed.ui.common.Messages;

public class UserTypeSelectionDialog extends TestClassSelectionDialog {

    protected static final IStatus ERROR = new Status(IStatus.ERROR, "com.ecfeed",
    		"Select enum element that with no constructor or public, parameterless one");

    protected class UserTypeSelectionValidator implements ISelectionStatusValidator{

		@Override
		public IStatus validate(Object[] selection) {
			try{
				if(selection.length != 1){
					return ERROR;
				}
				if(selection[0] instanceof IType == false){
					return ERROR;
				}
				IType selected = (IType)selection[0];
				if(selected.isEnum() == false){
					return ERROR;
				}
				if(JavaModelAnalyser.hasConstructor(selected) && JavaModelAnalyser.hasParameterlessConstructor(selected) == false){
					return ERROR;
				}
				return OK;
			}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
			return ERROR;
		}
    }

	public UserTypeSelectionDialog(Shell parent) {
		super(parent);
	}

	@Override
	protected ISelectionStatusValidator getSelectionValidator(){
		return new UserTypeSelectionValidator();
	}

	@Override
	protected String getDialogTitle() {
		return Messages.DIALOG_USER_TYPE_SELECTION_TITLE;
	}

	@Override
	protected String getDialogMessage(){
		return Messages.DIALOG_USER_TYPE_SELECTION_MESSAGE;
	}

}
