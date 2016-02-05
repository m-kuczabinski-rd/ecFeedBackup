/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.rcp3.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;

import com.testify.ecfeed.utils.EclipseHelper;


public class AboutHandler extends org.eclipse.core.commands.AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("About handler executed");

		MessageDialog.openInformation(
				EclipseHelper.getActiveShell(), 
				"About ecFeed", 
				"EcFeed is a tool which allows to design and model tests.");

		return null;
	}
}
