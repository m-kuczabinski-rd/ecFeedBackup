package com.testify.ecfeed.modelif.java.category;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class CategoryOperationReverseSetExpected extends
		CategoryOperationSetExpected implements IModelOperation {

	private List<TestCaseNode> fRestoredTestCases;
	private List<ConstraintNode> fRestoredConstraints;

	public CategoryOperationReverseSetExpected(CategoryNode fTarget, boolean expected,
			List<TestCaseNode> restoredTestCases,
			List<ConstraintNode> restoredConstraints) {
		super(fTarget, expected);
		fRestoredTestCases = restoredTestCases;
		fRestoredConstraints = restoredConstraints;
	}

	@Override
	public void execute() throws ModelIfException {
		getTarget().setExpected(getExpected());
		MethodNode method = getTarget().getMethod();
		if(method != null){
			method.clearTestCases();
			for(TestCaseNode tc : fRestoredTestCases){
				method.addTestCase(tc);
			}
			method.clearConstraints();
			for(ConstraintNode c : fRestoredConstraints){
				method.addConstraint(c);
			}
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationSetExpected(getTarget(), !getExpected());
	}

}
