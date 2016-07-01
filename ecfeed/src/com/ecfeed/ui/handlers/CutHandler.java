/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.ui.handlers;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.actions.ActionFactory;


public class CutHandler {

	public static void execute() throws ExecutionException {
		HandlerHelper.executeGlobalAction(ActionFactory.CUT.getId());
	}
}
