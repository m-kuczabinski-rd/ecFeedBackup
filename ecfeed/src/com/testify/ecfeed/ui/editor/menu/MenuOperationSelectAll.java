package com.testify.ecfeed.ui.editor.menu;

import org.eclipse.jface.viewers.TreeViewer;

public class MenuOperationSelectAll extends MenuOperation {

	private TreeViewer fViewer;

	public MenuOperationSelectAll(TreeViewer viewer) {
		super("Select all");
		fViewer = viewer;
	}

	@Override
	public Object execute() {
		fViewer.expandAll();
		fViewer.getTree().selectAll();
		fViewer.getTree().deselect(fViewer.getTree().getTopItem());
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
