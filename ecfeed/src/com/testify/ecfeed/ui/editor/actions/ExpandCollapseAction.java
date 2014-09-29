package com.testify.ecfeed.ui.editor.actions;

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
