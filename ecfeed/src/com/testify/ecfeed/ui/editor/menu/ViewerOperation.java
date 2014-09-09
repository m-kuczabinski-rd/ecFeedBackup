package com.testify.ecfeed.ui.editor.menu;

import org.eclipse.jface.viewers.TreeViewer;

public abstract class ViewerOperation extends MenuOperation {

	private TreeViewer fViewer;

	public ViewerOperation(String name, TreeViewer viewer) {
		super(name);
		fViewer = viewer;
	}

	protected TreeViewer getViewer(){
		return fViewer;
	}
}
