/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http:www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.adapter.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.adapter.ITypeAdapterProvider;

public class GenericRemoveNodesOperation extends BulkOperation {

	private Set<AbstractNode> fRemoved;

	public GenericRemoveNodesOperation(Collection<? extends AbstractNode> nodes, ITypeAdapterProvider adapterProvider, boolean validate){
		super(OperationNames.REMOVE_NODES, false);
		fRemoved = new HashSet<>(nodes);
		Iterator<AbstractNode> it = fRemoved.iterator();
		while(it.hasNext()){
			AbstractNode node = it.next();
			for(AbstractNode ancestor : node.getAncestors()){
				if(fRemoved.contains(ancestor)){
					it.remove();
					break;
				}
			}
		}
		prepareOperations(adapterProvider, validate);
		return;
	}
	
	private void prepareOperations(ITypeAdapterProvider adapterProvider, boolean validate){
		HashMap<ClassNode, HashMap<String, HashMap<MethodNode, List<String>>>> duplicatesMap = new HashMap<>();
		HashMap<MethodNode, List<AbstractParameterNode>> parameterMap = new HashMap<>();
		ArrayList<ClassNode> classes = new ArrayList<>();
		ArrayList<MethodNode> methods = new ArrayList<>();
		ArrayList<MethodParameterNode> params = new ArrayList<>();
		ArrayList<GlobalParameterNode> globals = new ArrayList<>();
		ArrayList<ChoiceNode> choices = new ArrayList<>();
		ArrayList<AbstractNode> others = new ArrayList<>();
		HashSet<ConstraintNode> constraints = new HashSet<>();
		ArrayList<TestCaseNode> testcases = new ArrayList<>();

		for(AbstractNode node : fRemoved){
			if(node instanceof ClassNode){
				classes.add((ClassNode)node);
			} else if(node instanceof MethodNode){
				methods.add((MethodNode)node);
			} else if(node instanceof MethodParameterNode){
				params.add((MethodParameterNode)node);
			} else if(node instanceof GlobalParameterNode){
				globals.add((GlobalParameterNode)node);
			} else if(node instanceof ConstraintNode){
				constraints.add((ConstraintNode)node);
			} else if(node instanceof TestCaseNode){
				testcases.add((TestCaseNode)node);
			} else if(node instanceof ChoiceNode){
				choices.add((ChoiceNode)node);
			} else{
				others.add(node);
			}
		}
		// removing classes, they are independent from anything
		for(ClassNode clazz : classes){
			addOperation(FactoryRemoveOperation.getRemoveOperation(clazz, adapterProvider, validate));
		}
		// removing choices and deleting connected constraints/test cases from their respective to-remove lists beforehand
		for(ChoiceNode choice : choices){
			eliminateMentioningConstraints(choice, constraints);
			Iterator<TestCaseNode> tcaseItr = testcases.iterator();
			while(tcaseItr.hasNext()){
				TestCaseNode tcase = tcaseItr.next();
				if(tcase.mentions(choice)){
					tcaseItr.remove();
				}
			}
			addOperation(FactoryRemoveOperation.getRemoveOperation(choice, adapterProvider, validate));
		}
		// removing test cases
		for(TestCaseNode tcase : testcases){
			addOperation(FactoryRemoveOperation.getRemoveOperation(tcase, adapterProvider, validate));
		}
		// leaving this in case of any further nodes being added
		for(AbstractNode node : others){
			addOperation(FactoryRemoveOperation.getRemoveOperation(node, adapterProvider, validate));
		}
		/*
		 * Iterate through global params. Do the same checks as for method
		 * parameters with every linker. If no linker is in potentially
		 * duplicate method - just proceed to remove global and all linkers and
		 * remove it from the lists.
		 */
		Iterator<GlobalParameterNode> globalItr = globals.iterator();
		while(globalItr.hasNext()){
			GlobalParameterNode global = globalItr.next();
			List<MethodParameterNode> linkers = global.getLinkers();
			boolean isDependent = false;
			for(MethodParameterNode param : linkers){
				MethodNode method = param.getMethod();
				if(addMethodToMap(method, duplicatesMap, methods)){
					duplicatesMap.get(method.getClassNode()).get(method.getName()).get(method).set(param.getIndex(), null);
					isDependent = true;
					if(!parameterMap.containsKey(method)){
						parameterMap.put(method, new ArrayList<AbstractParameterNode>());
					}
					parameterMap.get(method).add(global);
				}
			}
			if(!isDependent){
				//remove mentioning constraints from the list to avoid duplicates
				eliminateMentioningConstraints(global, constraints);
				addOperation(FactoryRemoveOperation.getRemoveOperation(global, adapterProvider, validate));
				globalItr.remove();
				/*
				 * in case linkers contain parameters assigned to removal -
				 * remove them from list; Global param removal will handle them.
				 */
				for(MethodParameterNode param : linkers){
					params.remove(param);
				}
			}
		}
		/*
		 * Iterate through parameters. If parent method is potential duplicate -
		 * add it to map for further validation. Replace values of to-be-deleted
		 * param with NULL to remove them later without disturbing parameters
		 * order. If parameters method is not potential duplicate - simply
		 * forward it for removal and remove it from to-remove list.
		 */
		Iterator<MethodParameterNode> paramItr = params.iterator();
		while(paramItr.hasNext()){
			MethodParameterNode param = paramItr.next();
			MethodNode method = param.getMethod();

			if(addMethodToMap(method, duplicatesMap, methods)){
				duplicatesMap.get(method.getClassNode()).get(method.getName()).get(method).set(param.getIndex(), null);
				if(!parameterMap.containsKey(method)){
					parameterMap.put(method, new ArrayList<AbstractParameterNode>());
				}
				parameterMap.get(method).add(param);
			} else{
				//remove mentioning constraints from the list to avoid duplicates
				eliminateMentioningConstraints(param, constraints);
				addOperation(FactoryRemoveOperation.getRemoveOperation(param, adapterProvider, validate));
				paramItr.remove();
			}
		}
		//Removing methods - information for model map has been already taken
		Iterator<MethodNode> methodItr = methods.iterator();
		while(methodItr.hasNext()){
			MethodNode method = methodItr.next();
				addOperation(FactoryRemoveOperation.getRemoveOperation(method, adapterProvider, validate));
				methodItr.remove();
		}
		// Detect duplicates
		Iterator<ClassNode> classItr = duplicatesMap.keySet().iterator();
		while(classItr.hasNext()){
			ClassNode classNext = classItr.next();
			Iterator<String> nameItr = duplicatesMap.get(classNext).keySet().iterator();
			while(nameItr.hasNext()){		
				//delete removed parameters marked with null (set?)
				// remember that we are validating both param and method removal at once. Need to store params somewhere else.
				HashSet<List<String>> paramSet = new HashSet<>();
				String strNext = nameItr.next();
				methodItr = duplicatesMap.get(classNext).get(strNext).keySet().iterator();
				while(methodItr.hasNext()){
					MethodNode methodNext = methodItr.next();
					List<String> paramList = duplicatesMap.get(classNext).get(strNext).get(methodNext);
					Iterator<String> parameterItr = paramList.iterator();
					//removing parameters from model image
					while(parameterItr.hasNext()){
						if(parameterItr.next() == null){
							parameterItr.remove();
						}
					}
					paramSet.add(paramList);
				}
				//	There is more methods than method signatures, ergo duplicates present. Proceeding to remove with duplicate check on.
				Set<MethodNode> methodSet = duplicatesMap.get(classNext).get(strNext).keySet();
				if(paramSet.size() < methodSet.size()){
					for(MethodNode method : methodSet){
						if(parameterMap.containsKey(method)){
							for(AbstractParameterNode node : parameterMap.get(method)){
								//remove mentioning constraints from the list to avoid duplicates
								eliminateMentioningConstraints(node, constraints);
								addOperation(FactoryRemoveOperation.getRemoveOperation(node, adapterProvider, validate));
							}
						}
					}
				}
				// Else remove with duplicate check off;
				else{
					for(MethodNode method : methodSet){
						if(parameterMap.containsKey(method)){
							for(AbstractParameterNode node : parameterMap.get(method)){
								//remove mentioning constraints from the list to avoid duplicates
								eliminateMentioningConstraints(node, constraints);
								if(node instanceof MethodParameterNode){
									addOperation(new MethodOperationRemoveParameter(method, (MethodParameterNode)node, validate, true));
								} else if(node instanceof GlobalParameterNode){
									addOperation(new GenericOperationRemoveGlobalParameter(((GlobalParameterNode)node).getParametersParent(), (GlobalParameterNode)node, true));
								}
							}
						}
					}
				}
			}
		}
		// removing remaining constraints
		for(ConstraintNode constraint : constraints){
			addOperation(FactoryRemoveOperation.getRemoveOperation(constraint, adapterProvider, validate));
		}


	}

	private boolean addMethodToMap(MethodNode method, HashMap<ClassNode, HashMap<String, HashMap<MethodNode, List<String>>>> duplicatesMap, List<MethodNode> removedMethods){
		ClassNode clazz = method.getClassNode();
		boolean hasDuplicate = false;
		for(MethodNode classMethod : clazz.getMethods()){
			if(classMethod != method && classMethod.getName().equals(method.getName()) && !removedMethods.contains(classMethod)){
				if(duplicatesMap.get(clazz) == null){
					duplicatesMap.put(clazz, new HashMap<String, HashMap<MethodNode, List<String>>>());
				}
				if(!(duplicatesMap.get(clazz).containsKey(classMethod.getName()))){
					duplicatesMap.get(clazz).put(classMethod.getName(), new HashMap<MethodNode, List<String>>());
				}
				if(!duplicatesMap.get(clazz).get(classMethod.getName()).containsKey(classMethod)){
					duplicatesMap.get(clazz).get(classMethod.getName())
							.put(classMethod, new ArrayList<String>(classMethod.getParametersTypes()));
				}
				if(!duplicatesMap.get(clazz).get(classMethod.getName()).containsKey(method)){
					duplicatesMap.get(clazz).get(classMethod.getName()).put(method, new ArrayList<String>(method.getParametersTypes()));
				}
				hasDuplicate = true;
			}
		}
		return hasDuplicate;
	}
	
	private void eliminateMentioningConstraints(AbstractNode node, HashSet<ConstraintNode> constraints){
		if(node instanceof ChoiceNode){
			Iterator<ConstraintNode> itr = constraints.iterator();
			while(itr.hasNext()){
				ConstraintNode constraint = itr.next();
				if(constraint.mentions((ChoiceNode)node)){
					itr.remove();
				}
			}
		} else if(node instanceof AbstractParameterNode){
			Iterator<ConstraintNode> itr = constraints.iterator();
			while(itr.hasNext()){
				ConstraintNode constraint = itr.next();
				if(constraint.mentions((AbstractParameterNode)node)){
					itr.remove();
				}
			}
		}
	}

}
