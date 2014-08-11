package com.testify.ecfeed.modelif.java.category;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class CategoryOperationReverseMove implements IModelOperation {

	private CategoryNode fTarget;
	private MethodNode fPreviousParent;
	private int fPreviousIndex;
	private MethodNode fCurrentParent;
	private int fCurrentIndex;
	private List<ConstraintNode> fPreviousParentOriginalConstraints;
	private List<TestCaseNode> fPreviousParentOriginalTestCases;
	private List<TestCaseNode> fCurrentParentOriginalTestCases;
	
	public CategoryOperationReverseMove(CategoryNode target,
			MethodNode previousParent,
			int previousIndex,
			List<ConstraintNode> previousParentOriginalConstraints,
			List<TestCaseNode> previousParentOriginalTestCases,
			List<TestCaseNode> currentParentOriginalTestCases) {
		fTarget = target;
		fPreviousParent = previousParent;
		fPreviousIndex = previousIndex;
		fCurrentParent = fTarget.getMethod();
		fPreviousParentOriginalConstraints = previousParentOriginalConstraints;
		fPreviousParentOriginalTestCases = previousParentOriginalTestCases;
		fCurrentParentOriginalTestCases = currentParentOriginalTestCases;
	}

	@Override
	public void execute() throws ModelIfException {
		fCurrentParent.removeCategory(fTarget);
		fCurrentParent.clearTestCases();
		for(TestCaseNode tc : fCurrentParentOriginalTestCases){
			fCurrentParent.addTestCase(tc);
		}
		fPreviousParent.addCategory(fTarget, fPreviousIndex);
		fPreviousParent.clearTestCases();
		for(TestCaseNode tc : fPreviousParentOriginalTestCases){
			fCurrentParent.addTestCase(tc);
		}
		fPreviousParent.clearConstraints();
		for(ConstraintNode c : fPreviousParentOriginalConstraints){
			fPreviousParent.addConstraint(c);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationMove(fTarget, fCurrentParent, fCurrentIndex);
	}

}
