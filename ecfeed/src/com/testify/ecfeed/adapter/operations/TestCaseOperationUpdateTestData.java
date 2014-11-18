package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class TestCaseOperationUpdateTestData extends AbstractModelOperation {
	
	private PartitionNode fNewValue;
	private PartitionNode fPreviousValue;
	private int fIndex;
	private TestCaseNode fTarget;

	public TestCaseOperationUpdateTestData(TestCaseNode target, int index, PartitionNode value) {
		super(OperationNames.UPDATE_TEST_DATA);
		fTarget = target;
		fIndex = index;
		fNewValue = value;
		fPreviousValue = target.getTestData().get(index);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fNewValue.getParameter() != fTarget.getTestData().get(fIndex).getParameter()){
			throw new ModelOperationException(Messages.TEST_DATA_CATEGORY_MISMATCH_PROBLEM);
		}
		fTarget.getTestData().set(fIndex, fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new TestCaseOperationUpdateTestData(fTarget, fIndex, fPreviousValue);
	}

}
