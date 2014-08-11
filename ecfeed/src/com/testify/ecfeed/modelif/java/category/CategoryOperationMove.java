package com.testify.ecfeed.modelif.java.category;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class CategoryOperationMove implements IModelOperation {
	
	private MethodNode fCurrentParent;
	private MethodNode fNewParent;
	private CategoryNode fTarget;
	private int fCurrentIndex;
	private int fNewIndex;
	private List<ConstraintNode> fCurrentParentOriginalConstraints;
	private List<TestCaseNode> fCurrentParentOriginalTestCases;
	private List<TestCaseNode> fNewParentOriginalTestCases;
	
	public CategoryOperationMove(CategoryNode target, MethodNode newParent, int index){
		fTarget = target;
		fCurrentParent = target.getMethod();
		fCurrentIndex = fTarget.getIndex();
		fNewParent = newParent;
		fNewIndex = index;
		
		fCurrentParentOriginalConstraints = fCurrentParent.getConstraintNodes();
		fCurrentParentOriginalTestCases = fCurrentParent.getTestCases();
		fNewParentOriginalTestCases = fNewParent.getTestCases();
	}

	@Override
	public void execute() throws ModelIfException {
		fCurrentParent.removeCategory(fTarget);
		fCurrentParent.makeConsistent();
		fNewParent.addCategory(fTarget, fNewIndex);
		fNewParent.clearTestCases();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationReverseMove(fTarget, fCurrentParent, fCurrentIndex, fCurrentParentOriginalConstraints, fCurrentParentOriginalTestCases, fNewParentOriginalTestCases);
	}

}
