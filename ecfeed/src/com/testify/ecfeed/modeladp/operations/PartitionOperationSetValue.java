package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ITypeAdapterProvider;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.Constants;
import com.testify.ecfeed.modeladp.java.JavaUtils;

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
			fTarget.getCategory().setDefaultValueString(fOriginalDefaultValue);
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
		fOriginalDefaultValue = fTarget.getCategory().getDefaultValue();
		
		fAdapterProvider = adapterProvider;
	}
	
	@Override
	public void execute() throws ModelOperationException {
		if(validatePartitionValue(fTarget.getCategory().getType(), fNewValue) == false){
			throw new ModelOperationException(Messages.PARTITION_VALUE_PROBLEM(fNewValue));
		}
		fTarget.setValueString(fNewValue);
		CategoryNode category = fTarget.getCategory();
		if(category != null && JavaUtils.isUserType(category.getType())){
			if(category.getLeafPartitionValues().contains(fOriginalDefaultValue) == false){
				category.setDefaultValueString(fNewValue);
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
