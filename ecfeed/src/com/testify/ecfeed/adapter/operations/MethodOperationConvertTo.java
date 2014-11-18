package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.MethodNode;

public class MethodOperationConvertTo extends AbstractModelOperation {
	
	private MethodNode fTarget;
	private MethodNode fSource;

	public MethodOperationConvertTo(MethodNode target, MethodNode source) {
		super(OperationNames.CONVERT_METHOD);
		fTarget = target;
		fSource = source;
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fTarget.getClassNode().getMethod(fSource.getName(), fSource.getParametersTypes()) != null){
			throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
		}
		if(fTarget.getParametersTypes().equals(fSource.getParametersTypes()) == false){
			throw new ModelOperationException(Messages.METHODS_INCOMPATIBLE_PROBLEM);
		}

		fTarget.setName(fSource.getName());
		for(int i = 0; i < fTarget.getParameters().size(); i++){
			ParameterNode targetParameter = fTarget.getParameters().get(i);
			ParameterNode sourceParameter = fSource.getParameters().get(i);
			
			targetParameter.setName(sourceParameter.getName());
			targetParameter.setExpected(sourceParameter.isExpected());
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationConvertTo(fSource, fTarget);
	}

}
