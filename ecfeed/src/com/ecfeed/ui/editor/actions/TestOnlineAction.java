/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.ecfeed.core.utils.EcException;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.MethodInterface;

public class TestOnlineAction extends ModelSelectionAction {

	private IFileInfoProvider fFileInfoProvider;
	private MethodInterface fMethodInterface;

	public TestOnlineAction(IFileInfoProvider fileInfoProvider, ISelectionProvider selectionProvider, MethodInterface methodInterface) {
		super("testOnline", "Test online", selectionProvider);
		fFileInfoProvider = fileInfoProvider;
		fMethodInterface = methodInterface;
	}

	@Override
	public void run() {
		try {
			fMethodInterface.executeOnlineTests(fFileInfoProvider);
		} catch (EcException e) {
			final String MSG = "Can not execute online tests.";
			ExceptionCatchDialog.open(MSG, e.getMessage());
		}
	}

	@Override
	public boolean isEnabled(){
		return true;
	}

}
