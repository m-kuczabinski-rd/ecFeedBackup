package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ITypeAdapterProvider;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.JavaUtils;

public class PartitionOperationSetValue implements IModelOperation {

	private String fNewValue;
	private String fOriginalValue;
	private String fOriginalDefaultValue;
	private PartitionNode fTarget;
	
	private ITypeAdapterProvider fAdapterProvider;
	
	private class ReverseOperation implements IModelOperation{

		@Override
		public void execute() throws ModelIfException {
			fTarget.setValueString(fOriginalValue);
			fTarget.getCategory().setDefaultValueString(fOriginalDefaultValue);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new PartitionOperationSetValue(fTarget, fNewValue, fAdapterProvider);
		}
	}
	
	public PartitionOperationSetValue(PartitionNode target, String newValue, ITypeAdapterProvider adapterProvider){
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = fTarget.getValueString();
		fOriginalDefaultValue = fTarget.getCategory().getDefaultValue();
		
		fAdapterProvider = adapterProvider;
	}
	
	@Override
	public void execute() throws ModelIfException {
		if(validatePartitionValue(fTarget.getCategory().getType(), fNewValue) == false){
			throw new ModelIfException(Messages.PARTITION_VALUE_PROBLEM(fNewValue));
		}
		fTarget.setValueString(fNewValue);
		CategoryNode category = fTarget.getCategory();
		if(category != null && JavaUtils.isUserType(category.getType())){
			if(category.getLeafPartitionValues().contains(fOriginalDefaultValue) == false){
				category.setDefaultValueString(fNewValue);
			}
		}
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
