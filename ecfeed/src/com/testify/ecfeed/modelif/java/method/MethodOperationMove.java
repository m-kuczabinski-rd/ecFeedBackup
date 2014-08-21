package com.testify.ecfeed.modelif.java.method;

import java.util.Collections;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.classx.ClassOperationAddMethod;
import com.testify.ecfeed.modelif.java.classx.ClassOperationRemoveMethod;
import com.testify.ecfeed.modelif.java.common.BulkOperation;
import com.testify.ecfeed.modelif.java.common.Messages;

public class MethodOperationMove extends BulkOperation{
	
	private class MethodOperationSwap implements IModelOperation{

		private MethodNode fTarget;
		private int fNewIndex;
		private int fCurrentIndex;
		
		public MethodOperationSwap(MethodNode target, int newIndex) {
			fTarget = target;
			fNewIndex = newIndex;
			fCurrentIndex = target.getIndex();
		}

		@Override
		public void execute() throws ModelIfException {
			if(fNewIndex < 0){
				throw new ModelIfException(Messages.NEGATIVE_INDEX_PROBLEM);
			}
			if(fNewIndex >= fTarget.getClassNode().getMethods().size()){
				throw new ModelIfException(Messages.TOO_HIGH_INDEX_PROBLEM);
			}
			Collections.swap(fTarget.getClassNode().getMethods(), fCurrentIndex, fNewIndex);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodOperationSwap(fTarget, fCurrentIndex);
		}
	}

	public MethodOperationMove(MethodNode target, ClassNode newParent, int newIndex) {
		super(false);
		if(target.getClassNode() != newParent){
			addOperation(new ClassOperationAddMethod(newParent, target, newIndex));
			addOperation(new ClassOperationRemoveMethod(target.getClassNode(), target));
		}
		else{
			addOperation(new MethodOperationSwap(target, newIndex));
		}
	}
}
