package com.testify.ecfeed.modeladp.operations;

import java.util.Arrays;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

public class CategoryShiftOperation extends GenericShiftOperation {

	private List<CategoryNode> fCategories;

	public CategoryShiftOperation(List<CategoryNode> categories, GenericNode shifted, boolean up) {
		this(categories, Arrays.asList(new GenericNode[]{shifted}), up);
	}

	public CategoryShiftOperation(List<CategoryNode> categories, List<? extends GenericNode> shifted, boolean up) {
		this(categories, shifted, 0);
		setShift(minAllowedShift(shifted, up));
	}

	public CategoryShiftOperation(List<CategoryNode> categories, List<? extends GenericNode> shifted, int shift) {
		super(categories, shifted, shift);
		fCategories = categories;
	}
	
	@Override
	public void execute() throws ModelOperationException {
		if(shiftAllowed(getShiftedElements(), getShift()) == false){
			throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
		}
		MethodNode method = fCategories.get(0).getMethod();
		List<Integer> indices = indices(fCategories, getShiftedElements());
		shiftElements(fCategories, indices, getShift());
		for(TestCaseNode testCase : method.getTestCases()){
			shiftElements(testCase.getTestData(), indices, getShift());
		}
	}

	@Override 
	public IModelOperation reverseOperation(){
		return new CategoryShiftOperation(fCategories, getShiftedElements(), -getShift());
	}

	@Override
	protected boolean shiftAllowed(List<? extends GenericNode> shifted, int shift){
		if(super.shiftAllowed(shifted, shift) == false) return false;
		if(shifted.get(0) instanceof CategoryNode == false) return false;
		MethodNode method = ((CategoryNode)shifted.get(0)).getMethod();
		List<String> parameterTypes = method.getCategoriesTypes();
		List<Integer> indices = indices(method.getCategories(), shifted);
		shiftElements(parameterTypes, indices, shift);
		MethodNode sibling = method.getClassNode().getMethod(method.getName(), parameterTypes);
		if(sibling != null && sibling != method){
			return false;
		}
		return true;
	}

	@Override
	protected int minAllowedShift(List<? extends GenericNode> shifted, boolean up){
		int shift = up ? -1 : 1;
		while(shiftAllowed(shifted, shift) == false){
			shift += up ? -1 : 1;
			int borderIndex = (borderNode(shifted, shift) != null) ? borderNode(shifted, shift).getIndex() + shift : -1; 
			if(borderIndex < 0 || borderIndex >= borderNode(shifted, shift).getMaxIndex()){
				return 0;
			}
		}
		return shift;
	}
	
}
