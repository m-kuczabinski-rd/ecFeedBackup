/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.TreeViewer;


public class ExpandCollapseAction extends ModelSelectionAction {

	private ExpandAction fExpandAction;
	private CollapseAction fCollapseAction;

	public ExpandCollapseAction(TreeViewer treeViewer) {
		super(EXPAND_COLLAPSE_ACTION_ID, EXPAND_COLLAPSE_ACTION_NAME, treeViewer);
		fExpandAction = new ExpandAction(treeViewer);
		fCollapseAction = new CollapseAction(treeViewer);
	}

	@Override
	public boolean isEnabled(){
		return fExpandAction.isEnabled() || fCollapseAction.isEnabled();
	}
	
	@Override
	public void run(){
		if(fExpandAction.isEnabled()){
			fExpandAction.run();
		}
		else if(fCollapseAction.isEnabled()){
			fCollapseAction.run();
		}
	}

}
