package com.testify.ecfeed.modelif.java.category;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class CategoryOperationReverseSetType implements IModelOperation {

	private CategoryNode fTarget;
	private String fOriginalType;
	private String fCurrentType;
	private List<PartitionNode> fOriginalPartitions;
	private List<ConstraintNode> fOriginalConstraints;
	private List<TestCaseNode> fOriginalTestCases;

	public CategoryOperationReverseSetType(CategoryNode target,
			String originalType, List<PartitionNode> originalPartitions,
			List<ConstraintNode> originalConstraints,
			List<TestCaseNode> originalTestCases) {
		fTarget = target;
		fOriginalType = originalType;
		fCurrentType = fTarget.getType();
		fOriginalPartitions = originalPartitions;
		fOriginalConstraints = originalConstraints;
		fOriginalTestCases = originalTestCases;
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.setType(fOriginalType);
		fTarget.getPartitions().clear();
		for(PartitionNode p : fOriginalPartitions){
			fTarget.addPartition(p);
		}

		MethodNode method = fTarget.getMethod();
		if(method != null){
			method.getTestCases().clear();
			method.getConstraintNodes().clear();

			for(ConstraintNode c : fOriginalConstraints){
				method.addConstraint(c);
			}
			for(TestCaseNode tc : fOriginalTestCases){
				method.addTestCase(tc);
			}
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationSetType(fTarget, fCurrentType, null);
	}

}
