package com.testify.ecfeed.ui.editor.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.modelif.SelectionInterface;

public class ModelSelectionAction extends Action {
	
	private ISelectionProvider fSelectionProvider;

	public ModelSelectionAction(ISelectionProvider selectionProvider){
		fSelectionProvider = selectionProvider;
	}
	
	protected List<GenericNode> getSelectedNodes(){
		List<GenericNode> result = new ArrayList<>();
		for(Object o : ((IStructuredSelection)fSelectionProvider.getSelection()).toList()){
			if(o instanceof GenericNode){
				result.add((GenericNode)o);
			}
		}
		return result;
	}

	protected SelectionInterface getSelectionInterface(){
		SelectionInterface selectionIf = new SelectionInterface();
		selectionIf.setTarget(getSelectedNodes());
		return selectionIf;
	}
	
	protected boolean isSelectionSibling(){
		List<GenericNode> nodes = getSelectedNodes();
		if(nodes.isEmpty()) return false;
		GenericNode parent = nodes.get(0).getParent();
		for(GenericNode node : nodes){
			if(node.getParent() != parent) return false;
		}
		return true;
	}
	
	protected boolean isSelectionSingleType(){
		List<GenericNode> nodes = getSelectedNodes();
		if(nodes.isEmpty()) return false;
		Class<?> type = nodes.get(0).getClass();
		for(GenericNode node : nodes){
			if(node.getClass().equals(type) == false) return false;
		}
		return true;
	}

}
