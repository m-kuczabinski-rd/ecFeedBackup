package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.testify.ecfeed.model.AbstractNode;

public class NodeDnDBuffer {

	private static NodeDnDBuffer fInstance = new NodeDnDBuffer();
	
	private List<AbstractNode> fDraggedNodes;
	
	public static NodeDnDBuffer getInstance(){
		return fInstance;
	}
	
	public void setDraggedNodes(List<AbstractNode>nodes){
		fDraggedNodes = nodes;
		removeDuplicatedChildren(fDraggedNodes);
	}
	
	public List<AbstractNode> getDraggedNodes(){
		return fDraggedNodes;
	}
	
	public List<AbstractNode> getDraggedNodesCopy(){
		List<AbstractNode> result = new ArrayList<>();
		if(fDraggedNodes != null){
			for(AbstractNode node : fDraggedNodes){
				result.add(node.getCopy());
			}
		}
		return result;
	}

	public void clear() {
		if(fDraggedNodes != null){
			fDraggedNodes.clear();
		}
	}

	private void removeDuplicatedChildren(List<AbstractNode> nodes) {
		Iterator<AbstractNode> it = nodes.iterator();
		while(it.hasNext()){
			AbstractNode node = it.next();
			AbstractNode parent = node.getParent();
			while(parent != null){
				if(nodes.contains(parent)){
					it.remove();
					break;
				}
				parent = parent.getParent();
			}
		}
	}
}
