package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodNode;


public class GlobalParameterOperationSetType extends BulkOperation {

	public GlobalParameterOperationSetType(GlobalParameterNode target, String newType, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.SET_TYPE, true);

		addOperation(new AbstractParameterOperationSetType(target, newType, adapterProvider));
		for(MethodNode method : target.getMethods()){
			addOperation(new MethodOperationMakeConsistent(method));
		}
	}

}
