package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

import com.testify.ecfeed.model.GenericNode;

public class MenuOperationExpand extends ViewerOperation {

	private List<? extends GenericNode> fNodes;

	public MenuOperationExpand(TreeViewer viewer, List<? extends GenericNode> nodes){
		super("Expand", viewer);
		fNodes = nodes;
	}
	@Override
	public Object execute() {
		for(GenericNode node : fNodes){
			getViewer().expandToLevel(node, TreeViewer.ALL_LEVELS);
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		boolean enabled = false;
		for(GenericNode node : fNodes){
			if((getViewer().isExpandable(node)) && (getViewer().getExpandedState(node) == false)){
				enabled = true;
			}
		}
		return enabled;
	}

}
