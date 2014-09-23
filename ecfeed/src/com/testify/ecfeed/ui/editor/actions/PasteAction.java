package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.modelif.GenericNodeInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.NodeClipboard;
import com.testify.ecfeed.ui.modelif.NodeInterfaceFactory;

public class PasteAction extends ModelModyfyingAction {

	private TreeViewer fTreeViewer;
	private int fIndex;

	public PasteAction(ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		this(-1, selectionProvider, updateContext);
	}
	
	public PasteAction(int index, ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(GlobalActions.PASTE.getId(), GlobalActions.PASTE.getName(), selectionProvider, updateContext);
		fIndex = index;
	}
	
	public PasteAction(TreeViewer treeViewer, IModelUpdateContext updateContext) {
		this(-1, treeViewer, updateContext);
	}

	public PasteAction(int index, TreeViewer treeViewer, IModelUpdateContext updateContext) {
		this(index, (ISelectionProvider)treeViewer, updateContext);
		fTreeViewer = treeViewer;
	}

	@Override
	public boolean isEnabled(){
		if(getSelectedNodes().size() != 1) return false;
		GenericNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), getUpdateContext()); 
		if (fIndex != -1){
			return nodeIf.pasteEnabled(NodeClipboard.getContent(), fIndex);
		}
		return nodeIf.pasteEnabled(NodeClipboard.getContent());
	}
	
	@Override
	public void run(){
		GenericNode parent = getSelectedNodes().get(0);
		GenericNodeInterface parentIf = NodeInterfaceFactory.getNodeInterface(parent, getUpdateContext()); 
		parentIf.addChildren(NodeClipboard.getContentCopy());
		if(fTreeViewer != null){
			fTreeViewer.expandToLevel(parent, 1);
		}
	}

}
