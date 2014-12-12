package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.GlobalParametersParentNode;
import com.testify.ecfeed.model.MethodParameterNode;

public class GenericOperationRemoveGlobalParameter extends BulkOperation {

	public GenericOperationRemoveGlobalParameter(GlobalParametersParentNode target, GlobalParameterNode parameter) {
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true);
		for(MethodParameterNode linker : parameter.getLinkers()){
			addOperation(new MethodOperationRemoveParameter(linker.getMethod(), linker));
		}
		addOperation(new GenericOperationRemoveParameter(target, parameter));
	}

}
