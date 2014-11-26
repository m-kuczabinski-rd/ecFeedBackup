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
			restoreOriginalValues(fTarget.getChoices());
			fTarget.setType(fCurrentType);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new AbstractParameterOperationSetType(fTarget, fNewType, fAdapterProvider);
		}

		private void restoreOriginalChoices(ChoicesParentNode parent) {
			parent.replaceChoices(fOriginalChoices.get(parent));
			for(ChoiceNode child : parent.getChoices()){
				restoreOriginalChoices(child);
			}
		}

		private void restoreOriginalValues(List<ChoiceNode> choices) {
			for(ChoiceNode choice : choices){
				if(fOriginalValues.containsKey(choice)){
					choice.setValueString(fOriginalValues.get(choice));
				}
				restoreOriginalValues(choice.getChoices());
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
		fOriginalChoices.clear();
		fOriginalValues.clear();

		saveChoices(fTarget);
		saveValues(fTarget.getChoices());

		if(JavaUtils.isValidTypeName(fNewType) == false){
			throw new ModelOperationException(Messages.CATEGORY_TYPE_REGEX_PROBLEM);
		}

//		ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
		fTarget.setType(fNewType);
		adaptChoices(fTarget);

//
//		convertChoiceValues(fTarget, adapter);
//		convertAbstractNodesValues(fTarget, adapter);
//		removeDeadChoices(fTarget);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

	protected void saveChoices(ChoicesParentNode parent){
		fOriginalChoices.put(parent, new ArrayList<ChoiceNode>(parent.getChoices()));
		for(ChoiceNode child : parent.getChoices()){
			saveChoices(child);
		}
	}

	protected void saveValues(List<ChoiceNode> choices) {
		for(ChoiceNode choice : choices){
			fOriginalValues.put(choice, choice.getValueString());
			saveValues(choice.getChoices());
		}
	}
//
//	private void convertAbstractNodesValues(ChoicesParentNode parent, ITypeAdapter adapter) {
//		for(ChoiceNode choice : parent.getChoices()){
//			if(choice.isAbstract()){
//				String value = adapter.convert(choice.getValueString());
//				if(value == null){
//					value = adapter.defaultValue();
//				}
//				if(choice.getValueString().equals(value) == false){
//					choice.setValueString(value);
//				}
//			}
//			convertAbstractNodesValues(choice, adapter);
//		}
//	}
//
//	private void convertChoiceValues(ChoicesParentNode parent, ITypeAdapter adapter) {
//		for(ChoiceNode p : parent.getChoices()){
//			convertChoiceValue(p, adapter);
//			convertChoiceValues(p, adapter);
//		}
//	}
//
//	private void convertChoiceValue(ChoiceNode p, ITypeAdapter adapter) {
//		fOriginalValues.put(p, p.getValueString());
//		String newValue = adapter.convert(p.getValueString());
//		p.setValueString(newValue);
//	}

	private void adaptChoices(ChoicesParentNode parent){
		Iterator<ChoiceNode> it = parent.getChoices().iterator();
		ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
		while(it.hasNext()){
			ChoiceNode choice = it.next();
			if(choice.isAbstract()){
				adaptChoices(choice);
				if(choice.getChoices().isEmpty()){
					it.remove();
				}else{
					String newValue = adapter.convert(choice.getValueString());
					if(newValue == null){
						fOriginalValues.put(choice, choice.getValueString());
						newValue = adapter.defaultValue();
					}
					choice.setValueString(newValue);
				}
			}else{
				String newValue = adapter.convert(choice.getValueString());
				if(newValue == null){
					it.remove();
				}else{
					fOriginalValues.put(choice, choice.getValueString());
					choice.setValueString(newValue);
				}
			}

		}
	}

//	private void removeDeadChoices(ChoicesParentNode parent) {
//		List<ChoiceNode> toRemove = new ArrayList<ChoiceNode>();
//		for(ChoiceNode p : parent.getChoices()){
//			if(isDead(p)){
//				toRemove.add(p);
//			}
//			else{
//				removeDeadChoices(p);
//			}
//		}
//		for(ChoiceNode removed : toRemove){
//			parent.removeChoice(removed);
//		}
//	}
//
//	private boolean isDead(ChoiceNode p) {
//		if(p.isAbstract() == false){
//			return p.getValueString() == null;
//		}
//		else{
//			boolean allChildrenDead = true;
//			for(ChoiceNode child : p.getChoices()){
//				if(isDead(child) == false){
//					allChildrenDead = false;
//					break;
//				}
//			}
//			return allChildrenDead;
//		}
//	}
}
