package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class TestCaseOperationUpdateTestData implements IModelOperation {
	
	private PartitionNode fNewValue;
	private PartitionNode fPreviousValue;
	private int fIndex;
	private TestCaseNode fTarget;

	public TestCaseOperationUpdateTestData(TestCaseNode target, int index, PartitionNode value) {
		fTarget = target;
		fIndex = index;
		fNewValue = value;
		fPreviousValue = target.getTestData().get(index);
	}

	@Override
	public void execute() throws ModelIfException {
		if(fNewValue.getCategory() != fTarget.getTestData().get(fIndex).getCategory()){
			throw new ModelIfException(Messages.TEST_DATA_CATEGORY_MISMATCH_PROBLEM);
		}
		fTarget.getTestData().set(fIndex, fNewValue);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new TestCaseOperationUpdateTestData(fTarget, fIndex, fPreviousValue);
	}

}
