package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.java.Constants;

public class PartitionOperationRename implements IModelOperation {

	private String fNewName;
	private PartitionNode fTarget;
	private String fOriginalName;

	public PartitionOperationRename(PartitionNode target, String newName){
		fNewName = newName;
		fTarget = target;
		fOriginalName = fTarget.getName();
	}
	
	@Override
	public void execute() throws ModelIfException{
		if(fTarget.getSibling(fNewName) != null){
			throw new ModelIfException(Messages.PARTITION_NAME_NOT_UNIQUE_PROBLEM);
		}
		if(fNewName.matches(Constants.REGEX_PARTITION_NODE_NAME) == false){
			throw new ModelIfException(Messages.PARTITION_NAME_REGEX_PROBLEM);
		}
		fTarget.setName(fNewName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new PartitionOperationRename(fTarget, fOriginalName);
	}
	
	@Override
	public String toString(){
		return "rename[" + fTarget + "](" + fNewName + ")"; 
	}

}
