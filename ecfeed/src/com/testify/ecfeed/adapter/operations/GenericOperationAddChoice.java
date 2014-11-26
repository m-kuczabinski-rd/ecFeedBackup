package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapter;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ChoicesParentNode;
import com.testify.ecfeed.model.MethodNode;

public class GenericOperationAddChoice extends BulkOperation {

	private class AddChoiceOperation extends AbstractModelOperation{
		private ChoicesParentNode fTarget;
		private ChoiceNode fChoice;
		private int fIndex;
		private ITypeAdapterProvider fAdapterProvider;

		public AddChoiceOperation(ChoicesParentNode target, ChoiceNode choice, ITypeAdapterProvider adapterProvider, int index) {
			super(OperationNames.ADD_PARTITION);
			fTarget = target;
			fChoice = choice;
			fIndex = index;
			fAdapterProvider = adapterProvider;
		}

		@Override
		public void execute() throws ModelOperationException {
			if(fIndex == -1){
				fIndex = fTarget.getChoices().size();
			}
			if(fTarget.getChoiceNames().contains(fChoice.getName())){
				throw new ModelOperationException(Messages.PARTITION_NAME_DUPLICATE_PROBLEM);
			}
			if(fIndex < 0){
				throw new ModelOperationException(Messages.NEGATIVE_INDEX_PROBLEM);
			}
			if(fIndex > fTarget.getChoices().size()){
				throw new ModelOperationException(Messages.TOO_HIGH_INDEX_PROBLEM);
			}
			validateChoiceValue(fChoice);
			fTarget.addChoice(fChoice, fIndex);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new GenericOperationRemoveChoice(fTarget, fChoice, fAdapterProvider, false);
		}

		private void validateChoiceValue(ChoiceNode choice) throws ModelOperationException{
			if(choice.isAbstract() == false){
				String type = fTarget.getParameter().getType();
				ITypeAdapter adapter = fAdapterProvider.getAdapter(type);
				String newValue = adapter.convert(choice.getValueString());
				if(newValue == null){
					throw new ModelOperationException(Messages.PARTITION_VALUE_PROBLEM(choice.getValueString()));
				}
			}
			else{
				for(ChoiceNode child : choice.getChoices()){
					validateChoiceValue(child);
				}
			}
		}
	}

	public GenericOperationAddChoice(ChoicesParentNode target, ChoiceNode choice, ITypeAdapterProvider adapterProvider, int index, boolean validate) {
		super(OperationNames.ADD_PARTITION, true);
		addOperation(new AddChoiceOperation(target, choice, adapterProvider, index));
		for(MethodNode method : target.getParameter().getMethods())
		if((method != null) && validate){
			addOperation(new MethodOperationMakeConsistent(method));
		}
	}

	public GenericOperationAddChoice(ChoicesParentNode target, ChoiceNode choice, ITypeAdapterProvider adapterProvider, boolean validate) {
		this(target, choice, adapterProvider, -1, validate);
	}
}
