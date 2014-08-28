package com.testify.ecfeed.modelif.java.category;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.Messages;

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
