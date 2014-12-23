package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;

public class ReplaceChoicesOperation extends BulkOperation {

	public ReplaceChoicesOperation(AbstractParameterNode target, List<ChoiceNode> choices, ITypeAdapterProvider adapterProvider) {
		super("Replace choices", true);
		List<ChoiceNode> skipped = new ArrayList<ChoiceNode>();
		for(ChoiceNode choice : choices){
			if(target.getChoiceNames().contains(choice.getName())){
				skipped.add(choice);
			}else{
				addOperation(new GenericOperationAddChoice(target, choice, adapterProvider, true));
			}
		}
		addOperation(new GenericRemoveNodesOperation(target.getChoices(), adapterProvider, true));
		for(ChoiceNode choice : skipped){
			addOperation(new GenericOperationAddChoice(target, choice, adapterProvider, true));
		}
	}

}
