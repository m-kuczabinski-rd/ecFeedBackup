/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class InsertAction extends ModelModifyingAction {

	public InsertAction(ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(GlobalActions.INSERT.getId(), GlobalActions.INSERT.getName(), selectionProvider, updateContext);
	}

	@Override
	public boolean isEnabled(){
		return true;
	}

	@Override 
	public void run(){
		// TODO
	}

}
