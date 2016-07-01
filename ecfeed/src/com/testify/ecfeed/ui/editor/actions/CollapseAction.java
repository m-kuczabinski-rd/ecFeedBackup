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

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

import com.ecfeed.core.model.AbstractNode;


public class CollapseAction extends ModelSelectionAction {
	
	private TreeViewer fViewer;

	public CollapseAction(TreeViewer viewer) {
		super(GlobalActions.COLLAPSE.getId(), GlobalActions.COLLAPSE.getName(), viewer);
		fViewer = viewer;
	}

	@Override
	public boolean isEnabled(){
		boolean enabled = false;
		List<AbstractNode> nodes = getSelectedNodes();
		for(AbstractNode node : nodes){
			if(fViewer.getExpandedState(node)){
				enabled = true;
			}
		}
		return enabled;
	}
	
	@Override
	public void run(){
		for(AbstractNode node : getSelectedNodes()){
			fViewer.collapseToLevel(node, TreeViewer.ALL_LEVELS);
		}
	}

}
