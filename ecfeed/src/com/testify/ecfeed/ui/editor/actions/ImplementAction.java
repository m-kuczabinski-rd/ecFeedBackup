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

import org.eclipse.jface.viewers.StructuredViewer;

import com.testify.ecfeed.core.adapter.EImplementationStatus;
import com.testify.ecfeed.core.adapter.IModelImplementer;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ImplementAction extends ModelModifyingAction {

	private IModelImplementer fImplementer;

	public ImplementAction(StructuredViewer viewer, IModelUpdateContext context, IModelImplementer implementer) {
		super("implement", "Implement", viewer, context);
		fImplementer = implementer;
	}

	@Override
	public void run(){
		for(AbstractNode node : getSelectedNodes()){
			fImplementer.implement(node);
		}
	}

	@Override
	public boolean isEnabled(){
		return !allImplementableNodesImplemented();
	}

	private boolean allImplementableNodesImplemented() {
		for(AbstractNode node : getSelectedNodes()){
			if(!fImplementer.implementable(node)) {
				continue;
			}
			if (fImplementer.getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
				return false;
			}
		}
		return true;
	}
}
