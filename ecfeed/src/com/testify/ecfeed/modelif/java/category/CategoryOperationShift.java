package com.testify.ecfeed.modelif.java.category;

import java.util.Collections;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.Messages;

public class CategoryOperationShift implements IModelOperation {

	private int fCurrentIndex;
	private int fNewIndex;
	private CategoryNode fTarget;

	public CategoryOperationShift(CategoryNode target, int newIndex) {
		fTarget = target;
		fNewIndex = newIndex;
		fCurrentIndex = target.getIndex();
	}

	public static boolean shiftAllowed(CategoryNode target, int newIndex){
		int currentIndex = target.getIndex();
		int positionsToMove = newIndex - currentIndex;
		if(newIndex < 0 || newIndex >= target.getMethod().getCategories().size()){
			return false;
		}
		MethodNode method = target.getMethod();
		List<String> types = method.getCategoriesTypes();
		shift(types, currentIndex, positionsToMove);
		MethodNode duplicate = method.getClassNode().getMethod(method.getName(), types);
		if(duplicate != null && duplicate != method){
			return false;
		}
		return true;
	}
	
	public static int nextAllowedIndex(CategoryNode target, boolean up){
		int index = target.getIndex();
		while(index >= 0 && index < target.getMaxIndex()){
			index = up ? --index : ++index;
			if(shiftAllowed(target, index)){
				return index;
			}
		}
		return -1;
	}

	@Override
	public void execute() throws ModelIfException {
		if(fNewIndex < 0){
			throw new ModelIfException(Messages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fNewIndex > fTarget.getMethod().getCategories().size()){
			throw new ModelIfException(Messages.TOO_HIGH_INDEX_PROBLEM);
		}
		if(shiftAllowed(fTarget, fNewIndex) == false){
			throw new ModelIfException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
		}
		shift(fTarget.getMethod().getCategories(), fCurrentIndex, fNewIndex - fCurrentIndex);
		for(TestCaseNode testCase : fTarget.getMethod().getTestCases()){
			shift(testCase.getTestData(), fCurrentIndex, fNewIndex - fCurrentIndex);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationShift(fTarget, fCurrentIndex);
	}
	
	private static boolean shift(List<? extends Object> list, int index, int positions){
		if(index < 0 || index > list.size() - 1 || index + positions < 0 || index + positions > list.size() - 1){
			return false;
		}
		boolean up = positions < 0;
		positions = Math.abs(positions);
		while(positions > 0){
			Collections.swap(list, index, up ? index-1 : index+1);
			index = up? index - 1:index + 1;
			--positions;
		}
		return true;
	}

}
