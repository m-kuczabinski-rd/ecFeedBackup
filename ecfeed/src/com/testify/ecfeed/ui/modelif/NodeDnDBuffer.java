package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.GenericNode;

public class NodeDnDBuffer {

	private static NodeDnDBuffer fInstance = new NodeDnDBuffer();
	
	private List<GenericNode> fDraggedNodes;
	
	public static NodeDnDBuffer getInstance(){
		return fInstance;
	}
	
	public void setDraggedNodes(List<GenericNode>nodes){
		fDraggedNodes = nodes;
	}
	
	public List<GenericNode> getDraggedNodes(){
		return fDraggedNodes;
	}
	
	public List<GenericNode> getDraggedNodesCopy(){
		List<GenericNode> result = new ArrayList<>();
		if(fDraggedNodes != null){
			for(GenericNode node : fDraggedNodes){
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
	
}
