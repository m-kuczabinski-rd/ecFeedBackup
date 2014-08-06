package com.testify.ecfeed.modelif.java.category;

import java.util.Collections;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.Messages;

public class CategoryOperationSwap implements IModelOperation {

	private int fCurrentIndex;
	private int fNewIndex;
	private CategoryNode fTarget;

	public CategoryOperationSwap(CategoryNode target, int newIndex) {
		fTarget = target;
		fNewIndex = newIndex;
		fCurrentIndex = target.getIndex();
	}

	public static boolean swapAllowed(CategoryNode target, int newIndex){
		int currentIndex = target.getIndex();
		if(newIndex < 0){
			return false;
		}
		if(newIndex > target.getMethod().getCategories().size()){
			return false;
		}
		MethodNode method = target.getMethod();
		List<String> types = method.getCategoriesTypes();
		Collections.swap(types, newIndex, currentIndex);
		if(method.getClassNode().getMethod(method.getName(), types) != null){
			return false;
		}
		return true;
	}

	@Override
	public void execute() throws ModelIfException {
		if(fNewIndex < 0){
			throw new ModelIfException(Messages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fNewIndex > fTarget.getMethod().getCategories().size()){
			throw new ModelIfException(Messages.TOO_HIGH_INDEX_PROBLEM);
		}
		MethodNode method = fTarget.getMethod();
		List<String> types = method.getCategoriesTypes();
		Collections.swap(types, fNewIndex, fCurrentIndex);
		if(method.getClassNode().getMethod(method.getName(), types) != null){
			throw new ModelIfException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
		}
		Collections.swap(method.getCategories(), fNewIndex, fCurrentIndex);
		for(TestCaseNode testCase : method.getTestCases()){
			Collections.swap(testCase.getTestData(), fNewIndex, fCurrentIndex);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationSwap(fTarget, fCurrentIndex);
	}

}
