package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.Constants;
import com.testify.ecfeed.model.PartitionNode;

public class PartitionOperationRename extends AbstractModelOperation {

	private String fNewName;
	private PartitionNode fTarget;
	private String fOriginalName;

	public PartitionOperationRename(PartitionNode target, String newName){
		super(OperationNames.RENAME);
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
		markModelUpdated();
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
