package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ITypeAdapter;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.CategoryNode;

public class CategoryOperationSetDefaultValue implements IModelOperation {

	private CategoryNode fTarget;
	private ITypeAdapter fTypeAdapter;
	private String fNewValue;
	private String fOriginalValue;

	public CategoryOperationSetDefaultValue(CategoryNode target, String newValue, ITypeAdapter typeAdapter) {
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getDefaultValue();
		fTypeAdapter = typeAdapter;
	}

	@Override
	public void execute() throws ModelIfException {
		if(fTypeAdapter.convert(fNewValue) == null){
			throw new ModelIfException(Messages.CATEGORY_DEFAULT_VALUE_REGEX_PROBLEM);
		}
		fTarget.setDefaultValueString(fNewValue);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationSetDefaultValue(fTarget, fOriginalValue, fTypeAdapter);
	}

}
