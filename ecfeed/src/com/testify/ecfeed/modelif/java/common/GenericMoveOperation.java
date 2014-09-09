package com.testify.ecfeed.modelif.java.common;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class GenericMoveOperation extends BulkOperation {
	
	public GenericMoveOperation(GenericNode target, GenericNode newParent, int newIndex) throws ModelIfException {
		super(true);
		try {
			if(target.getParent() == newParent){
				addOperation((IModelOperation)newParent.accept(new SwapOperationFactory(target, newIndex)));
			}
			else{
				addOperation((IModelOperation)newParent.accept(new RemoveChildOperationProvider(target)));
				addOperation((IModelOperation)target.getParent().accept(new AddChildOperationProvider(target, newIndex)));
			}
		} catch (Exception e) {
			throw new ModelIfException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}
}
