package com.testify.ecfeed.modelif.java.method;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.Messages;

public class MethodOperationMove implements IModelOperation{
	private ClassNode fCurrentParent;
	private int fCurrentIndex;
	private ClassNode fNewParent;
	private int fNewIndex;
	private MethodNode fTarget;

	public MethodOperationMove(MethodNode target, ClassNode newParent,
			int newIndex) {
		fTarget = target;
		fCurrentParent = target.getClassNode();
		if(fCurrentParent != null){
			fCurrentIndex = fCurrentParent.getMethods().indexOf(target);
		}
		fNewParent = newParent;
		fNewIndex = newIndex;
	}

	public MethodOperationMove(MethodNode target, ClassNode newParent) {
		this(target, newParent, newParent.getMethods().size());
	}

	@Override
	public void execute() throws ModelIfException {
		if(fCurrentParent == null || fNewParent == null){
			throw new ModelIfException(Messages.MISSING_PARENT_PROBLEM);
		}
		if(fNewIndex < 0){
			throw new ModelIfException(Messages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fNewIndex > fNewParent.getMethods().size()){
			throw new ModelIfException(Messages.TOO_HIGH_INDEX_PROBLEM);
		}
		String targetName = fTarget.getName();
		if(fCurrentParent != fNewParent){
			if(fNewParent.getMethod(targetName, fTarget.getCategoriesTypes()) != null){
				throw new ModelIfException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
			}
		}
		fCurrentParent.removeMethod(fTarget);
		fNewParent.addMethod(fTarget, fNewIndex);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationMove(fTarget, fCurrentParent, fCurrentIndex);
	}

}
