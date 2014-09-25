package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.Constants;
import com.testify.ecfeed.modeladp.java.JavaUtils;

public class CategoryOperationRename extends AbstractOperationRename {

	public CategoryOperationRename(GenericNode target, String newName) {
		super(target, newName);
	}

	@Override
	public void execute() throws ModelOperationException {
		CategoryNode target = (CategoryNode)getTarget();
		if(fNewName.matches(Constants.REGEX_CATEGORY_NODE_NAME) == false){
			throw new ModelOperationException(Messages.CATEGORY_NAME_REGEX_PROBLEM);
		}
		if(JavaUtils.isJavaKeyword(fNewName)){
			throw new ModelOperationException(Messages.CATEGORY_NAME_REGEX_PROBLEM);
		}
		if(target.getMethod().getCategory(fNewName) != null){
			throw new ModelOperationException(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
		}
		fTarget.setName(fNewName);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationRename(getTarget(), getOriginalName());
	}

}
