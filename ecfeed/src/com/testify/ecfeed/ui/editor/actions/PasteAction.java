package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.modelif.GenericNodeInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.Messages;
import com.testify.ecfeed.ui.modelif.NodeClipboard;
import com.testify.ecfeed.ui.modelif.NodeInterfaceFactory;

public class PasteAction extends ModelModyfyingAction {

	private TreeViewer fTreeViewer;
	private int fIndex;

	public PasteAction(ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		this(-1, selectionProvider, updateContext);
	}
	
	public PasteAction(int index, ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(selectionProvider, updateContext);
		fIndex = index;
	}
	
	public PasteAction(TreeViewer treeViewer, IModelUpdateContext updateContext) {
		this(treeViewer, -1, updateContext);
	}

	public PasteAction(TreeViewer treeViewer, int index, IModelUpdateContext updateContext) {
		super(treeViewer, updateContext);
		fTreeViewer = treeViewer;
		fIndex = index;
	}

	@Override
	public boolean isEnabled(){
		if(getSelectedNodes().size() != 1) return false;
		GenericNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0)); 
		if (fIndex != -1){
			return nodeIf.pasteEnabled(NodeClipboard.getContent(), fIndex);
		}
		return nodeIf.pasteEnabled(NodeClipboard.getContent());
	}
	
	@Override
	public void run(){
		GenericNode parent = getSelectedNodes().get(0);
		GenericNodeInterface parentIf = NodeInterfaceFactory.getNodeInterface(parent); 
		parentIf.addChildren(NodeClipboard.getContentCopy(), getUpdateContext(), Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
		if(fTreeViewer != null){
			fTreeViewer.expandToLevel(parent, 1);
		}
	}

}
