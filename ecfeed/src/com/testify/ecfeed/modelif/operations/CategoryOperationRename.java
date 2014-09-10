package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.JavaUtils;

public class CategoryOperationRename extends AbstractOperationRename {

	public CategoryOperationRename(GenericNode target, String newName) {
		super(target, newName);
	}

	@Override
	public void execute() throws ModelIfException {
		CategoryNode target = (CategoryNode)getTarget();
		if(fNewName.matches(Constants.REGEX_CATEGORY_NODE_NAME) == false){
			throw new ModelIfException(Messages.CATEGORY_NAME_REGEX_PROBLEM);
		}
		if(JavaUtils.isJavaKeyword(fNewName)){
			throw new ModelIfException(Messages.CATEGORY_NAME_REGEX_PROBLEM);
		}
		if(target.getMethod().getCategory(fNewName) != null){
			throw new ModelIfException(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
		}
		fTarget.setName(fNewName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationRename(getTarget(), getOriginalName());
	}

}
