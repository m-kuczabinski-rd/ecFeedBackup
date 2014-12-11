package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapter;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ChoicesParentNode;

public class AbstractParameterOperationSetType extends AbstractModelOperation {

	private AbstractParameterNode fTarget;
	private String fNewType;
	private String fCurrentType;
	private ITypeAdapterProvider fAdapterProvider;
	private Map<ChoicesParentNode, List<ChoiceNode>> fOriginalChoices;
	private Map<ChoiceNode, String> fOriginalValues;

	protected class ReverseOperation extends AbstractReverseOperation{

		public ReverseOperation() {
			super(AbstractParameterOperationSetType.this);
		}

		@Override
		public void execute() throws ModelOperationException {
			restoreOriginalChoices(fTarget);
			restoreOriginalValues(fTarget);
			fTarget.setType(fCurrentType);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new AbstractParameterOperationSetType(fTarget, fNewType, fAdapterProvider);
		}

		protected void restoreOriginalChoices(ChoicesParentNode parent) {
			parent.replaceChoices(getOriginalChoices().get(parent));
			for(ChoiceNode child : getChoices(parent)){
				restoreOriginalChoices(child);
			}
		}

		protected void restoreOriginalValues(ChoicesParentNode parent) {
			for(ChoiceNode choice : getChoices(parent)){
				if(getOriginalValues().containsKey(choice)){
					choice.setValueString(getOriginalValues().get(choice));
				}
				restoreOriginalValues(choice);
			}
		}

	}

	public AbstractParameterOperationSetType(AbstractParameterNode target, String newType, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.SET_TYPE);
		fTarget = target;
		fNewType = newType;
		fAdapterProvider = adapterProvider;
		fOriginalChoices = new HashMap<>();
		fOriginalValues = new HashMap<>();
	}

	@Override
	public void execute() throws ModelOperationException {
		fCurrentType = fTarget.getType();
		getOriginalChoices().clear();
		getOriginalValues().clear();

		saveChoices(fTarget);
		saveValues(fTarget);

		if(JavaUtils.isValidTypeName(fNewType) == false){
			throw new ModelOperationException(Messages.CATEGORY_TYPE_REGEX_PROBLEM);
		}

		fTarget.setType(fNewType);
		adaptChoices(fTarget);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

	protected void saveChoices(ChoicesParentNode parent){
		getOriginalChoices().put(parent, new ArrayList<ChoiceNode>(getChoices(parent)));
		for(ChoiceNode child : getChoices(parent)){
			saveChoices(child);
		}
	}

	protected void saveValues(ChoicesParentNode parent) {
		for(ChoiceNode choice : getChoices(parent)){
			getOriginalValues().put(choice, choice.getValueString());
			saveValues(choice);
		}
	}

	// removed choices that cannot be converted and parents of only non-convertable choices.
	// convert values of remaining choices.
	private void adaptChoices(ChoicesParentNode parent){
		Iterator<ChoiceNode> it = getChoices(parent).iterator();
		ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
		while(it.hasNext()){
			ChoiceNode choice = it.next();
			if(choice.isAbstract()){
				adaptChoices(choice);
				if(getChoices(choice).isEmpty()){
					it.remove();
				}else{
					String newValue = adapter.convert(choice.getValueString());
					if(newValue == null){
						newValue = adapter.defaultValue();
					}
					choice.setValueString(newValue);
				}
			}else{
				String newValue = adapter.convert(choice.getValueString());
				if(newValue == null){
					it.remove();
				}else{
					choice.setValueString(newValue);
				}
			}
		}
	}

	protected Map<ChoicesParentNode, List<ChoiceNode>> getOriginalChoices(){
		return fOriginalChoices;
	}

	protected Map<ChoiceNode, String> getOriginalValues(){
		return fOriginalValues;
	}

	protected List<ChoiceNode> getChoices(ChoicesParentNode parent){
		return parent.getChoices();
	}

	protected ITypeAdapterProvider getAdapterProvider(){
		return fAdapterProvider;
	}

	protected String getNewType(){
		return fNewType;
	}

}
