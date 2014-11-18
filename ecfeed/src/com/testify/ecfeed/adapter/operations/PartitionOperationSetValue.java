package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.Constants;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.PartitionNode;

public class PartitionOperationSetValue extends AbstractModelOperation {

	private String fNewValue;
	private String fOriginalValue;
	private String fOriginalDefaultValue;
	private PartitionNode fTarget;
	
	private ITypeAdapterProvider fAdapterProvider;
	
	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation() {
			super(PartitionOperationSetValue.this.getName());
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.setValueString(fOriginalValue);
			fTarget.getParameter().setDefaultValueString(fOriginalDefaultValue);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new PartitionOperationSetValue(fTarget, fNewValue, fAdapterProvider);
		}
	}
	
	public PartitionOperationSetValue(PartitionNode target, String newValue, ITypeAdapterProvider adapterProvider){
		super(OperationNames.SET_PARTITION_VALUE);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = fTarget.getValueString();
		fOriginalDefaultValue = fTarget.getParameter().getDefaultValue();
		
		fAdapterProvider = adapterProvider;
	}
	
	@Override
	public void execute() throws ModelOperationException {
		if(validatePartitionValue(fTarget.getParameter().getType(), fNewValue) == false){
			throw new ModelOperationException(Messages.PARTITION_VALUE_PROBLEM(fNewValue));
		}
		fTarget.setValueString(fNewValue);
		ParameterNode parameter = fTarget.getParameter();
		if(parameter != null && JavaUtils.isUserType(parameter.getType())){
			if(parameter.getLeafPartitionValues().contains(fOriginalDefaultValue) == false){
				parameter.setDefaultValueString(fNewValue);
			}
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

	@Override
	public String toString(){
		return "setValue[" + fTarget + "](" + fNewValue + ")"; 
	}

	private boolean validatePartitionValue(String type, String value) {
		if (value.length() > Constants.MAX_PARTITION_VALUE_STRING_LENGTH) return false;

		return fAdapterProvider.getAdapter(type).convert(value) != null;
	}
}
