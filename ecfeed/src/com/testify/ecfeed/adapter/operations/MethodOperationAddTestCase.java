package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapter;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.Constants;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationAddTestCase extends AbstractModelOperation {

	private MethodNode fTarget;
	private TestCaseNode fTestCase;
	private int fIndex;
	private ITypeAdapterProvider fAdapterProvider;

	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase, ITypeAdapterProvider adapterProvider, int index) {
		super(OperationNames.ADD_TEST_CASE);
		fTarget = target;
		fTestCase = testCase;
		fIndex = index;
		fAdapterProvider = adapterProvider;
	}

	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase, ITypeAdapterProvider adapterProvider) {
		this(target, testCase, adapterProvider, -1);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fIndex == -1){
			fIndex = fTarget.getTestCases().size();
		}
		if(fTestCase.getName().matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			throw new ModelOperationException(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		if(fTestCase.updateReferences(fTarget) == false){
			throw new ModelOperationException(Messages.TEST_CASE_INCOMPATIBLE_WITH_METHOD);
		}
		//following must be done AFTER references are updated
		for(PartitionNode choice : fTestCase.getTestData()){
			ParameterNode parameter = choice.getParameter();
			if(choice.getParameter().isExpected()){
				String type = parameter.getType();
				ITypeAdapter adapter = fAdapterProvider.getAdapter(type);
				String newValue = adapter.convert(choice.getValueString());
				if(newValue == null){
					throw new ModelOperationException(Messages.TEST_CASE_DATA_INCOMPATIBLE_WITH_METHOD);
				}
				choice.setValueString(newValue);
			}
		}

		fTarget.addTestCase(fTestCase, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRemoveTestCase(fTarget, fTestCase);
	}

}
