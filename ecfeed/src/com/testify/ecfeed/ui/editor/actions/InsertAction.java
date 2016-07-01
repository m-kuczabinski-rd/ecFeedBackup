/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.actions;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class InsertAction extends ModelModifyingAction {

	IFileInfoProvider fFileInfoProvider;
	StructuredViewer fStructuredViewer;
	IModelUpdateContext fUpdateContext;

	public InsertAction(
			ISelectionProvider selectionProvider,
			StructuredViewer structuredViewer,
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(GlobalActions.INSERT.getId(), GlobalActions.INSERT.getName(), selectionProvider, updateContext);
		fFileInfoProvider = fileInfoProvider;
		fStructuredViewer = structuredViewer;
		fUpdateContext = updateContext;
	}

	@Override
	public boolean isEnabled(){
		return true;
	}

	@Override 
	public void run(){
		List<AbstractNode> selectedNodes = getSelectedNodes();

		if (selectedNodes.size() != 1) {
			return;
		}

		AbstractNode abstractNode = selectedNodes.get(0);

		AddChildActionProvider actionProvider = 
				new AddChildActionProvider(fStructuredViewer, fUpdateContext, fFileInfoProvider);

		AbstractAddChildAction insertAction = actionProvider.getMainInsertAction(abstractNode);
		if (insertAction == null) {
			return;
		}

		insertAction.run();
	}

}
