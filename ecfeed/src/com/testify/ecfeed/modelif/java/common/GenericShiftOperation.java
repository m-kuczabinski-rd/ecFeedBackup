package com.testify.ecfeed.modelif.java.common;

import java.util.Collections;
import java.util.List;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class GenericShiftOperation implements IModelOperation{

	private List<? extends GenericNode> fList;
	private int fCurrentIndex;
	private int fNewIndex;

	GenericShiftOperation(List<? extends GenericNode> list, int currentIndex, int newIndex){
		fList = list;
		fCurrentIndex = currentIndex;
		fNewIndex = newIndex;
	}
	
	public static boolean shiftAllowed(GenericNode target, int newIndex){
		return (newIndex >= 0 && newIndex < target.getMaxIndex());
	}
	
	public static int nextAllowedIndex(GenericNode target, boolean up){
		int nextIndex = up ? target.getIndex() - 1 : target.getIndex() + 1;
		return (nextIndex >= 0 && nextIndex < target.getMaxIndex()) ? nextIndex : -1;
	}

	@Override
	public void execute() throws ModelIfException {
		if(fCurrentIndex < 0 || fNewIndex < 0){
			throw new ModelIfException(Messages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fCurrentIndex >= fList.size() || fNewIndex >= fList.size()){
			throw new ModelIfException(Messages.TOO_HIGH_INDEX_PROBLEM);
		}
		int i = fCurrentIndex;
		while(i != fNewIndex){
			Collections.swap(fList, i < fNewIndex ? i++ : i--, i);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericShiftOperation(fList, fCurrentIndex, fNewIndex);
	}
	
}