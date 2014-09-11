package com.testify.ecfeed.ui.editor.actions;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

import com.testify.ecfeed.model.GenericNode;

public class ExpandAction extends ModelSelectionAction {

	TreeViewer fViewer;
	
	public ExpandAction(TreeViewer viewer) {
		super(viewer);
		fViewer = viewer;
	}

	@Override
	public boolean isEnabled(){
		boolean enabled = false;
		List<GenericNode> nodes = getSelectedNodes();
		for(GenericNode node : nodes){
			if((fViewer.isExpandable(node)) && (getBranchExpandedState(node, fViewer) == false)){
				enabled = true;
			}
		}
		return enabled;
	}
	
	@Override
	public void run(){
		for(GenericNode node : getSelectedNodes()){
			fViewer.expandToLevel(node, TreeViewer.ALL_LEVELS);
		}
	}
	
	protected boolean getBranchExpandedState(GenericNode branchRoot, TreeViewer viewer){
		if(viewer.getExpandedState(branchRoot) == false) return false;
		for(GenericNode child : branchRoot.getChildren()){
			if(getBranchExpandedState(child, viewer) == false) return false;
		}
		return true;
	}
}
