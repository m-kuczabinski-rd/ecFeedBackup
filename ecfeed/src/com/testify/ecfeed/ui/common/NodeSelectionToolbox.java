package com.testify.ecfeed.ui.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.modelif.SelectionInterface;

public class NodeSelectionToolbox {
	
	private ISelectionProvider fSelectionProvider;

	public NodeSelectionToolbox(ISelectionProvider selectionProvider){
		fSelectionProvider = selectionProvider;
	}
	
	public SelectionInterface getSelectionInterface(){
		SelectionInterface selectionIf = new SelectionInterface();
		selectionIf.setTarget(getSelectedNodes());
		return selectionIf;
	}
	
	public boolean isSelectionSibling(){
		List<GenericNode> nodes = getSelectedNodes();
		if(nodes.isEmpty()) return false;
		GenericNode parent = nodes.get(0).getParent();
		for(GenericNode node : nodes){
			if(node.getParent() != parent) return false;
		}
		return true;
	}
	
	public boolean isSelectionSingleType(){
		List<GenericNode> nodes = getSelectedNodes();
		if(nodes.isEmpty()) return false;
		Class<?> type = nodes.get(0).getClass();
		for(GenericNode node : nodes){
			if(node.getClass().equals(type) == false) return false;
		}
		return true;
	}

	public List<GenericNode> getSelectedNodes() {
		List<GenericNode> result = new ArrayList<>();
		IStructuredSelection selection = (IStructuredSelection)fSelectionProvider.getSelection();
		for(Object o : selection.toList()){
			if(o instanceof GenericNode){
				result.add((GenericNode)o);
			}
		}
		return result;
	}

}
