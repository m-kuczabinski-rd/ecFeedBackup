package com.testify.ecfeed.modelif.java.classx;

import java.util.Collections;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.BulkOperation;
import com.testify.ecfeed.modelif.java.common.Messages;
import com.testify.ecfeed.modelif.java.root.RootOperationAddNewClass;
import com.testify.ecfeed.modelif.java.root.RootOperationRemoveClass;

public class ClassOperationMove extends BulkOperation/*implements IModelOperation */{

	private class ClassOperationSwap implements IModelOperation{

		private ClassNode fTarget;
		private int fNewIndex;
		private int fCurrentIndex;
		
		public ClassOperationSwap(ClassNode target, int newIndex) {
			fTarget = target;
			fNewIndex = newIndex;
			fCurrentIndex = target.getIndex();
		}

		@Override
		public void execute() throws ModelIfException {
			if(fNewIndex < 0){
				throw new ModelIfException(Messages.NEGATIVE_INDEX_PROBLEM);
			}
			if(fNewIndex >= fTarget.getRoot().getClasses().size()){
				throw new ModelIfException(Messages.TOO_HIGH_INDEX_PROBLEM);
			}
			Collections.swap(fTarget.getRoot().getClasses(), fCurrentIndex, fNewIndex);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ClassOperationSwap(fTarget, fCurrentIndex);
		}
	}
	
	public ClassOperationMove(ClassNode target, RootNode newParent, int newIndex) {
		super(false);
		if(target.getRoot() != newParent){
			addOperation(new RootOperationAddNewClass(newParent, target, newIndex));
			addOperation(new RootOperationRemoveClass(target.getRoot(), target));
		}
		else{
			addOperation(new ClassOperationSwap(target, newIndex));
		}
	}
}
