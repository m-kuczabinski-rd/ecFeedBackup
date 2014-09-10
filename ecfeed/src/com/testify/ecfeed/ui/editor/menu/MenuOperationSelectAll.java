package com.testify.ecfeed.ui.editor.menu;

import org.eclipse.jface.viewers.TreeViewer;

public class MenuOperationSelectAll extends ViewerOperation {

	public MenuOperationSelectAll(TreeViewer viewer) {
		super("Select &all", viewer);
	}

	@Override
	public Object execute() {
		getViewer().expandAll();
		getViewer().getTree().selectAll();
		getViewer().getTree().deselect(getViewer().getTree().getTopItem());
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
