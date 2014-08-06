package com.testify.ecfeed.modelif.java.method;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class MethodOperationReverseAddParameter implements IModelOperation {

	private List<TestCaseNode> fRemovedTestCases;
	private CategoryNode fParameter;
	private MethodNode fTarget;

	public MethodOperationReverseAddParameter(MethodNode target, CategoryNode parameter, 
			List<TestCaseNode> removedTestCases) {
		fTarget = target;
		fParameter = parameter;
		fRemovedTestCases = removedTestCases;
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.removeCategory(fParameter);
		for(TestCaseNode testCase : fRemovedTestCases){
			if(validateTestCaseSignature(testCase)){
				fTarget.addTestCase(testCase);
			}
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationAddParameter(fTarget, fParameter);
	}

	private boolean validateTestCaseSignature(TestCaseNode testCase) {
		List<String> types = new ArrayList<String>();
		for(PartitionNode partition : testCase.getTestData()){
			types.add(partition.getCategory().getType());
		}
		return types.equals(fTarget.getCategoriesTypes());
	}

}
