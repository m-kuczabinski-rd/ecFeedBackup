/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.handlers;

import org.eclipse.jface.action.IAction;

import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.dialogs.basic.InfoDialog;
import com.ecfeed.utils.EclipseHelper;


public class HandlerHelper {

	private final static  String CAN_NOT_EXECUTE = "Can not execute action.";

	public static void executeGlobalAction(String actionId) {
		IAction action = EclipseHelper.getGlobalAction(actionId);

		if (action == null) {
			InfoDialog.open("No global handler. " + CAN_NOT_EXECUTE);
		}

		try {
			action.run();
		}  catch (Exception e) {
			ExceptionCatchDialog.open(CAN_NOT_EXECUTE, e.getMessage());
		}
	}

}
