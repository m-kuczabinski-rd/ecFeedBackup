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
import com.ecfeed.ui.modelif.SelectionInterface;

public class ModelModifyingAction extends ModelSelectionAction {

	private IModelUpdateContext fUPdateContext;

	public ModelModifyingAction(String id, String name, ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(id, name, selectionProvider);
		fUPdateContext = updateContext;
	}

	protected IModelUpdateContext getUpdateContext(){
		return fUPdateContext;
	}
	
	protected SelectionInterface getSelectionInterface(){
		return getSelectionUtils().getSelectionInterface(fUPdateContext);
	}
}
