package com.testify.ecfeed.ui.editor.menu;

import org.eclipse.jface.viewers.TreeViewer;

public abstract class ViewerManipulationOperation extends MenuOperation {

	private TreeViewer fViewer;

	public ViewerManipulationOperation(String name, TreeViewer viewer) {
		super(name);
		fViewer = viewer;
	}

	protected TreeViewer getViewer(){
		return fViewer;
	}
}
