package com.testify.ecfeed.modelif.java.classx;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.Messages;;

public class ClassOperationMove implements IModelOperation {
	
	private RootNode fCurrentParent;
	private int fCurrentIndex;
	private RootNode fNewParent;
	private int fNewIndex;
	private ClassNode fTarget;

	public ClassOperationMove(ClassNode target, RootNode newParent, int newIndex) {
		fTarget = target;
		fCurrentParent = target.getRoot();
		if(fCurrentParent != null){
			fCurrentIndex = fCurrentParent.getClasses().indexOf(target);
		}
		fNewParent = newParent;
		fNewIndex = newIndex;
	}

	public ClassOperationMove(ClassNode target, RootNode newParent) {
		this(target, newParent, newParent.getClasses().size());
	}

	@Override
	public void execute() throws ModelIfException {
		if(fCurrentParent == null || fNewParent == null){
			throw new ModelIfException(Messages.CLASS_PARENT_DOES_NOT_EXIST_PROBLEM);
		}
		if(fNewIndex < 0){
			throw new ModelIfException(Messages.CLASS_INDEX_NEGATIVE_PROBLEM);
		}
		if(fNewIndex > fNewParent.getClasses().size()){
			throw new ModelIfException(Messages.CLASS_INDEX_TOO_HIGH_PROBLEM);
		}
		String targetName = JavaClassUtils.getQualifiedName(fTarget);
		if(fCurrentParent != fNewParent){
			for(ClassNode child : fNewParent.getClasses()){
				String childName = JavaClassUtils.getQualifiedName(child);
				if(targetName.equals(childName)){
					throw new ModelIfException(Messages.CLASS_NAME_DUPLICATE_PROBLEM);
				}
			}
		}
		fCurrentParent.removeClass(fTarget);
		fNewParent.addClass(fTarget, fNewIndex);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationMove(fTarget, fCurrentParent, fCurrentIndex);
	}

}
