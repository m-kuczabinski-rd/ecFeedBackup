/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.ecfeed.ui.modelif.IModelUpdateContext;

public class DeleteAction extends ModelModifyingAction {

	public DeleteAction(ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(GlobalActions.DELETE.getId(), GlobalActions.DELETE.getName(), selectionProvider, updateContext);
	}
	
	@Override
	public boolean isEnabled(){
		return getSelectionInterface().deleteEnabled();
	}

	@Override
	public void run(){
		getSelectionInterface().delete();
	}
	
}
