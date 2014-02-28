package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class TreeViewerSection extends ViewerSection {

	public TreeViewerSection(Composite parent, FormToolkit toolkit, int style, IModelUpdateListener updateListener) {
		super(parent, toolkit, style, updateListener);
	}

	@Override
	protected StructuredViewer createViewer(Composite viewerComposite, int style) {
		return createTreeViewer(viewerComposite, style);
	}

	@Override
	protected void createViewerColumns(){
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
}
