package com.testify.ecfeed.modelif.java.partition;

import java.util.Arrays;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.common.Messages;
import com.testify.ecfeed.utils.ClassUtils;

public class PartitionOperationSetValue implements IModelOperation {

	private String fNewValue;
	private String fOriginalValue;
	private String fOriginalDefaultValue;
	private PartitionNode fTarget;
	
	private class ReverseOperation implements IModelOperation{

		@Override
		public void execute() throws ModelIfException {
			fTarget.setValueString(fOriginalValue);
			fTarget.getCategory().setDefaultValueString(fOriginalDefaultValue);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new PartitionOperationSetValue(fTarget, fNewValue);
		}
	}
	
	public PartitionOperationSetValue(PartitionNode target, String newValue){
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = fTarget.getValueString();
		fOriginalDefaultValue = fTarget.getCategory().getDefaultValueString();
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
		if (type.equals(com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_STRING)) return true;
	
		if (value.length() == 0) return false;
		if (value.length() > Constants.MAX_PARTITION_VALUE_STRING_LENGTH) return false;
	
		if (ClassUtils.getPartitionValueFromString(value, type, ClassUtils.getClassLoader(true, null)) != null){
			return true;
		} else if (!getJavaTypes().contains(type)) {
				return value.matches(Constants.REGEX_JAVA_IDENTIFIER);
		}
		return false;
	}

	private List<String> getJavaTypes() {
		return Arrays.asList(Constants.SUPPORTED_PRIMITIVE_TYPES);
	}

}
