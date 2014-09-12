package com.testify.ecfeed.ui.editor.actions;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

import com.testify.ecfeed.model.GenericNode;


public class CollapseAction extends ModelSelectionAction {
	
	private TreeViewer fViewer;

	public CollapseAction(TreeViewer viewer) {
		super(viewer);
		fViewer = viewer;
	}

	@Override
	public boolean isEnabled(){
		boolean enabled = false;
		List<GenericNode> nodes = getSelectedNodes();
		for(GenericNode node : nodes){
			if(fViewer.getExpandedState(node)){
				enabled = true;
			}
		}
		return enabled;
	}
	
	@Override
	public void run(){
		for(GenericNode node : getSelectedNodes()){
			fViewer.collapseToLevel(node, TreeViewer.ALL_LEVELS);
		}
	}

}
