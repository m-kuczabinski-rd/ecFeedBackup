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

import org.eclipse.jface.viewers.ISelectionProvider;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.modelif.NodeSelectionUtils;

public abstract class ModelSelectionAction extends NamedAction {
	
	private NodeSelectionUtils fSelectionUtils;

	public ModelSelectionAction(String id, String name, ISelectionProvider selectionProvider){
		super(id, name);
		fSelectionUtils = new NodeSelectionUtils(selectionProvider);
	}
	
	protected List<AbstractNode> getSelectedNodes(){
		return getSelectionUtils().getSelectedNodes();
	}

	protected boolean isSelectionSibling(){
		return getSelectionUtils().isSelectionSibling();
	}
	
	protected boolean isSelectionSingleType(){
		return getSelectionUtils().isSelectionSingleType();
	}
	
	protected NodeSelectionUtils getSelectionUtils(){
		return fSelectionUtils;
	}

}
