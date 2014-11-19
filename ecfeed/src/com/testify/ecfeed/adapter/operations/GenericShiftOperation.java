package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractNode;

public class GenericShiftOperation extends AbstractModelOperation {

	private List<? extends AbstractNode> fShifted;
	private int fShift;
	private List<? extends AbstractNode> fCollection;

	public GenericShiftOperation(List<? extends AbstractNode> collection, AbstractNode shifted, boolean up){
		this(collection, Arrays.asList(new AbstractNode[]{shifted}), up);
	}

	public GenericShiftOperation(List<? extends AbstractNode> collection, List<? extends AbstractNode> shifted, boolean up){
		this(collection, shifted, 0);
		fShift = minAllowedShift(shifted, up);
	}

	public GenericShiftOperation(List<? extends AbstractNode> collection, List<? extends AbstractNode> shifted, int shift){
		super(OperationNames.MOVE);
		shift = shiftAllowed(shifted, shift) ? shift : 0;
		fShifted = new ArrayList<>(shifted);
		fCollection = collection;
		fShift = shift;
	}
	
	@Override
	public void execute() throws ModelOperationException {
		shiftElements(fCollection, indices(fCollection, fShifted), fShift);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericShiftOperation(fCollection, fShifted, -fShift);
	}

	public int getShift(){
		return fShift;
	}

	protected List<? extends AbstractNode> getCollection(){
		return fCollection;
	}

	protected List<? extends AbstractNode> getShiftedElements(){
		return fShifted;
	}

	protected void setShift(int shift){
		fShift = shift;
	}
	
	protected int minAllowedShift(List<? extends AbstractNode> shifted, boolean up){
		int shift = up ? -1 : 1;
		return shiftAllowed(shifted, shift) ? shift : 0; 
	}

	protected boolean haveSameParent(List<? extends AbstractNode> shifted) {
		if(shifted.size() == 0)return true;
		AbstractNode parent = shifted.get(0).getParent();
		for(AbstractNode node : shifted){
			if(node.getParent() != parent){
				return false;
			}
		}
		return true;
	}

	protected boolean areInstancesOfSameClass(List<? extends AbstractNode> shifted) {
		if(shifted.size() == 0) return true;
		Class<?> _class = shifted.get(0).getClass();
		for(AbstractNode node : shifted){
			if(node.getClass().equals(_class) == false){
				return false;
			}
		}
		return true;
	}
	
	protected AbstractNode borderNode(List<? extends AbstractNode> nodes, int shift){
		return shift < 0 ? minIndexNode(nodes) : maxIndexNode(nodes);
	}
	
	protected List<Integer> indices(List<?> collection, List<?> elements){
		List<Integer> indices = new ArrayList<>();
		for(Object element : elements){
			indices.add(collection.indexOf(element));
		}
		return indices;
	}

	protected void shiftElements(List<?> list, List<Integer> indices, int shift){
		Collections.sort(indices);
		if(shift > 0){
			Collections.reverse(indices);
		}

		for(int i = 0; i < indices.size(); i++){
			shiftElement(list, indices.get(i), shift);
		}
	}
	protected void shiftElement(List<?> list, int index, int shift) {
	
		int minIndex = Math.min(index, index+shift);
		int maxIndex = Math.max(index, index+shift) + ((shift < 0) ? 1:0);
		List<?> rotated = list.subList(minIndex, (shift > 0) ? maxIndex + 1 : maxIndex);
		int rotation = (shift>0) ? -1 : 1;
		Collections.rotate(rotated, rotation);
	}

	protected boolean shiftAllowed(List<? extends AbstractNode> shifted, int shift){
		if(areInstancesOfSameClass(shifted) == false){
			return false;
		}
		if(haveSameParent(shifted) == false){
			return false;
		}
		if(shift == 0){
			return false;
		}
		int newIndex = (borderNode(shifted, shift) != null) ? borderNode(shifted, shift).getIndex() + shift : -1;
		return newIndex >= 0 && newIndex < shifted.get(0).getMaxIndex();
	}

	private AbstractNode minIndexNode(List<? extends AbstractNode> nodes){
		if(nodes.size() == 0) return null;
		AbstractNode minIndexNode = nodes.get(0);
		for(AbstractNode node : nodes){
			minIndexNode = node.getIndex() < minIndexNode.getIndex() ? node : minIndexNode; 
		}
		return minIndexNode;
	}

	private AbstractNode maxIndexNode(List<? extends AbstractNode> nodes){
		if(nodes.size() == 0) return null;
		AbstractNode maxIndexNode = nodes.get(0);
		for(AbstractNode node : nodes){
			maxIndexNode = node.getIndex() > maxIndexNode.getIndex() ? node : maxIndexNode; 
		}
		return maxIndexNode;
	}

}
