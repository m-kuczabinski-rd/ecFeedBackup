package com.testify.ecfeed.ui.editor.actions;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

import com.testify.ecfeed.model.AbstractNode;


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
