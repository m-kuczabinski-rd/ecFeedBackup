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

import com.testify.ecfeed.ui.modelif.NodeClipboard;

public class CopyAction extends ModelSelectionAction {

	public CopyAction(ISelectionProvider selectionProvider){
		super(GlobalActions.COPY.getId(), GlobalActions.COPY.getName(), selectionProvider);
	}

	@Override
	public void run() {
		NodeClipboard.setContent(getSelectedNodes());
	}

	@Override
	public boolean isEnabled(){
		return getSelectedNodes().size() > 0 && isSelectionSingleType();
	}
}
