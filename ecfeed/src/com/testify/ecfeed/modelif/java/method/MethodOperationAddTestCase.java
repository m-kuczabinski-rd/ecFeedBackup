package com.testify.ecfeed.modelif.java.method;

import java.util.List;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.common.Messages;

public class MethodOperationAddTestCase implements IModelOperation {
	
	private MethodNode fTarget;
	private TestCaseNode fTestCase;
	private int fIndex;
	
	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase, int index) {
		fTarget = target;
		fTestCase = testCase;
		fIndex = index;
	}

	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase) {
		fTarget = target;
		fTestCase = testCase;
		fIndex = -1;
	}

	@Override
	public void execute() throws ModelIfException {
		if(fTestCase.getName().matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			throw new ModelIfException(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		if(fTestCase.getMethod() != null && fTestCase.getMethod() != fTarget){
			updateTestData(fTarget, fTestCase);
		}
		if(fIndex == -1){
			fTarget.addTestCase(fTestCase);
		}
		else{
			fTarget.addTestCase(fTestCase, fIndex);
		}
	}

	private void updateTestData(MethodNode method, TestCaseNode testCase) throws ModelIfException {
		int size = testCase.getTestData().size();
		if(size != method.getCategories().size()){
			throw new ModelIfException(Messages.TEST_CASE_INCOMPATIBLE_WITH_METHOD);
		}
		for(int i = 0; i < size; i++){
			List<PartitionNode> testData = testCase.getTestData();
			PartitionNode newPartition = method.getCategories().get(i).getPartition(testData.get(i).getName());
			if(newPartition == null){
				throw new ModelIfException(Messages.TEST_CASE_INCOMPATIBLE_WITH_METHOD);
			}
			testData.set(i, newPartition);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRemoveTestCase(fTarget, fTestCase);
	}

}
