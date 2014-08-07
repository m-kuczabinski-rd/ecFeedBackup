package com.testify.ecfeed.modelif.java.category;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class CategoryOperationReverseSetType implements IModelOperation {

	public CategoryOperationReverseSetType(CategoryNode target,
			String fType, List<PartitionNode> originalPartitions,
			List<ConstraintNode> originalConstraints,
			List<TestCaseNode> originalTestCases) {
	}

	@Override
	public void execute() throws ModelIfException {
		// TODO Auto-generated method stub

	}

	@Override
	public IModelOperation reverseOperation() {
		// TODO Auto-generated method stub
		return null;
	}

}
