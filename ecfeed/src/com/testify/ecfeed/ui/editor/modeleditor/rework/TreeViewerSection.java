package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class TreeViewerSection extends ViewerSection {

	public TreeViewerSection(Composite parent, FormToolkit toolkit,
			int style, int buttonsPosition) {
		super(parent, toolkit, style, buttonsPosition);
	}

	@Override
	protected StructuredViewer createViewer(Composite viewerComposite, int style) {
		return createTreeViewer(viewerComposite, style);
	}

	protected TreeViewer createTreeViewer(Composite parent, int style) {
		Tree tree = new Tree(parent, style);
		tree.setLayoutData(viewerLayoutData());
		TreeViewer treeViewer = new TreeViewer(tree);
		return treeViewer;
	}
	
	protected void addSelectionChangedListener(ISelectionChangedListener listener){
		getTreeViewer().addSelectionChangedListener(listener);
	}
	
	protected Tree getTree(){
		return getTreeViewer().getTree();
	}
	
	protected TreeViewer getTreeViewer(){
		return (TreeViewer)getViewer();
	}
	
	protected void createViewerColumns(){
	}
}
