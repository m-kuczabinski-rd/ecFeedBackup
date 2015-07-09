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

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.modelif.AbstractNodeInterface;
import com.testify.ecfeed.ui.modelif.NodeInterfaceFactory;

public class GoToImplementationAction extends ModelSelectionAction {

	public GoToImplementationAction(ISelectionProvider selectionProvider) {
		super("goToImpl", "Go to implementation", selectionProvider);
	}

	@Override
	public void run(){
		if(getSelectedNodes().size() != 1){
			return;
		}
		AbstractNode node = getSelectedNodes().get(0);
		AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, null, null);
		nodeIf.goToImplementation();
	}

	@Override
	public boolean isEnabled(){
		if(getSelectedNodes().size() != 1){
			return false;
		}
		AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null, null);
		return nodeIf.goToImplementationEnabled();
	}

}
