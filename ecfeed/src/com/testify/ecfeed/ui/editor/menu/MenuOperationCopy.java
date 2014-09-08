/*******************************************************************************
 * Copyright (c) 2014 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michal Gluszko (m.gluszko(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.modelif.NodeClipboard;

public class MenuOperationCopy extends MenuOperation{
	protected List<GenericNode> fNodes;

	@Override
	public Object execute(){
		NodeClipboard.setContent(fNodes);
		return null;
	}

	@Override
	public boolean isEnabled(){
		if(fNodes == null || fNodes.size() == 0) return false;
		
		boolean enabled = true;
		Class<?> type = fNodes.get(0).getClass();
		for(GenericNode node : fNodes){
			if(node.getClass() != type){
				enabled = false;
				break;
			}
		}
		return enabled;
	}
	
	public MenuOperationCopy(List<GenericNode> nodes){
		super("Copy");
		fNodes = nodes;
	}

}
