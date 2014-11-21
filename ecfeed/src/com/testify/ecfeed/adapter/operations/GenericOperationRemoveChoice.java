package com.testify.ecfeed.adapter.operations;

import java.util.Set;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ChoicesParentNode;

public class GenericOperationRemoveChoice extends BulkOperation {

	private class RemoveChoiceOperation extends AbstractModelOperation{
		
		private ChoicesParentNode fTarget;
		private ChoiceNode fChoice;
		private String fOriginalDefaultValue;
		private int fOriginalIndex;

		private class ReverseOperation extends AbstractModelOperation{

			public ReverseOperation() {
				super(RemoveChoiceOperation.this.getName());
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.addChoice(fChoice, fOriginalIndex);
				fTarget.getParameter().setDefaultValueString(fOriginalDefaultValue);
				markModelUpdated();
			}

			@Override
			public IModelOperation reverseOperation() {
				return new RemoveChoiceOperation(fTarget, fChoice);
			}
			
		}
		
		public RemoveChoiceOperation(ChoicesParentNode target, ChoiceNode choice){
			super(OperationNames.REMOVE_PARTITION);
			fTarget = target;
			fChoice = choice;
			fOriginalIndex = fChoice.getIndex();
			fOriginalDefaultValue = target.getParameter().getDefaultValue();
		}
		
		@Override
		public void execute() throws ModelOperationException {
			fOriginalIndex = fChoice.getIndex();
			MethodParameterNode parameter = fTarget.getParameter();
			if(parameter.isExpected() && JavaUtils.isPrimitive(parameter.getType()) == false && parameter.getChoices().size() == 1 && parameter.getChoices().get(0) == fChoice){
				// We are removing the only choice of expected parameter. 
				// The last parameter must represent the default expected value
				throw new ModelOperationException(Messages.EXPECTED_USER_TYPE_CATEGORY_LAST_PARTITION_PROBLEM);
			}
			fTarget.removeChoice(fChoice);
			if(parameter.isExpected() && fChoice.getValueString().equals(parameter.getDefaultValue())){
				// the value of removed choice is the same as default expected value
				// Check if there are leaf choices with the same value. If not, update the default value
				Set<String> leafValues = parameter.getLeafChoiceValues();
				if(leafValues.contains(parameter.getDefaultValue()) == false){
					if(leafValues.size() > 0){
						parameter.setDefaultValueString(leafValues.toArray(new String[]{})[0]);
					}
					else{
						throw new ModelOperationException(Messages.UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT);
					}
				}
			}
		}
		
		@Override
		public IModelOperation reverseOperation() {
			return new ReverseOperation();
		}
	}

	public GenericOperationRemoveChoice(ChoicesParentNode target, ChoiceNode choice, boolean validate) {
		super(OperationNames.REMOVE_PARTITION, true);
		addOperation(new RemoveChoiceOperation(target, choice));
		if((target.getParameter().getMethod() != null) && validate){
			addOperation(new MethodOperationMakeConsistent(target.getParameter().getMethod()));
		}
	}
}
