package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;

public class AbstractParameterOperationSetTypeWithChoices extends AbstractModelOperation{

	private class ReverseOperation extends AbstractReverseOperation{

		public ReverseOperation() {
			super(AbstractParameterOperationSetTypeWithChoices.this);
		}

		@Override
		public void execute() throws ModelOperationException {
			for(IModelOperation operation : reverseOperations()){
				operation.execute();
			}
		}

		@Override
		public IModelOperation reverseOperation() {
			return new AbstractParameterOperationSetTypeWithChoices(fSetTypeOperation, fTarget, fNewType, fDefaultChoices, fAdapterProvider);
		}

		protected List<IModelOperation> reverseOperations(){
			List<IModelOperation> reverseOperations = new ArrayList<IModelOperation>();
			for(IModelOperation operation : fExecuted){
				reverseOperations.add(0, operation.reverseOperation());
			}
			return reverseOperations;
		}
	}

	private IModelOperation fSetTypeOperation;
	private String fNewType;
	private List<ChoiceNode> fDefaultChoices;
	private ITypeAdapterProvider fAdapterProvider;
	private List<IModelOperation> fExecuted;
	private AbstractParameterNode fTarget;


	public AbstractParameterOperationSetTypeWithChoices(IModelOperation setTypeOperation, AbstractParameterNode target, String newType, List<ChoiceNode> defaultChoices, ITypeAdapterProvider adapterProvider) {
		super("Set type");
		fTarget = target;
		fSetTypeOperation = setTypeOperation;
		fNewType = newType;
		fDefaultChoices = defaultChoices;
		fAdapterProvider = adapterProvider;
		fExecuted = new ArrayList<IModelOperation>();

	}

	@Override
	public void execute() throws ModelOperationException {
		execute(fSetTypeOperation);
		execute(new GenericRemoveNodesOperation(fTarget.getChoices(), fAdapterProvider, true));
//		List<ChoiceNode> skipped = new ArrayList<ChoiceNode>();
		for(ChoiceNode choice : fDefaultChoices){
			if(fTarget.getChoiceNames().contains(choice.getName()) == false){
				execute(new GenericOperationAddChoice(fTarget, choice, fAdapterProvider, 0, true));
			}
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

	public void execute(IModelOperation operation){
		try {
			operation.execute();
			fExecuted.add(operation);
		} catch (ModelOperationException e) {
		}
	}

}
