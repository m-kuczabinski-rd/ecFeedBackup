/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

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
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;

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
		// Check for duplicate signatures possibly caused by global parameter type change
		if(fTarget instanceof GlobalParameterNode){
			GlobalParameterNode target = (GlobalParameterNode)fTarget;
			List<MethodNode> linkingMethods = new ArrayList<MethodNode>(target.getMethods());
			MethodNode testedMethod;
			// Iterate through methods. Methods of same class and name are matched just once and then removed from iteration.
			for(int i = 0; i < linkingMethods.size();){
				testedMethod = linkingMethods.get(i);
				ClassNode classNode = testedMethod.getClassNode();
				// Map of methods and their parameter lists
				HashMap<MethodNode, List<String>> methods = new HashMap<>();
				//searching for methods with same name as currently investigated
				for(MethodNode methodNode: classNode.getMethods()){
					if(methodNode.getName().equals(testedMethod.getName())
							&& methodNode.getParameters().size() == testedMethod.getParameters().size()){
						// if method links edited global parameter - replace types before matching
						if(target.getMethods().contains(methodNode)){
							List<String> types = methodNode.getParametersTypes();
							for(AbstractParameterNode parameter: methodNode.getParameters()){
								MethodParameterNode param = (MethodParameterNode)parameter;
								if(param.isLinked() && param.getLink().equals(target)){
									types.set(parameter.getIndex(), fNewType);
								}			
							}
							methods.put(methodNode, types);
						}
						// else add parameter list without alterations
						else {
							methods.put(methodNode, methodNode.getParametersTypes());
						}		
						// remove from linking parameter list, so no methods are matched twice
						linkingMethods.remove(methodNode);	
					}
				}
				// if less than 2 methods of same name found - skip matching
				if(methods.size() < 2) continue;
				// else match all found methods with each other
				else{
					ArrayList<MethodNode> remainingMethods = new ArrayList<MethodNode>(methods.keySet());
					MethodNode method;
					// match with not yet iterated through methods till duplicate found or there is just 1 method left
					for(int n = 0; n < remainingMethods.size() -1; n++){
						method = remainingMethods.get(n);
						for(int k = n+1; k < remainingMethods.size(); k++){
							if(methods.get(method).equals(methods.get(remainingMethods.get(k)))){
								throw new ModelOperationException(Messages.METHOD_GLOBAL_PARAMETER_SIGNATURE_DUPLICATE_PROBLEM(
										method.getClassNode().getName(), method.getName(), method.getParameters().toString(),
										remainingMethods.get(k).getParameters().toString()));
							}
						}
					}
				}
			}
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
