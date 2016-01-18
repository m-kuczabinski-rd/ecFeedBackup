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

package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalParameterNode extends AbstractParameterNode {

	public GlobalParameterNode(String name, String type) {
		super(name, type);
	}

	//copy constructor creating a global parameter instance from other types, eg. MethodParameterNode
	public GlobalParameterNode(AbstractParameterNode source) {
		this(source.getName(), source.getType());
		for(ChoiceNode choice : source.getChoices()){
			addChoice(choice.getCopy());
		}
	}

	@Override
	public GlobalParameterNode getCopy() {
		GlobalParameterNode copy = new GlobalParameterNode(getName(), getType());
		for(ChoiceNode choice : getChoices()){
			copy.addChoice(choice.getCopy());
		}
		return copy;
	}

	@Override
	public List<MethodNode> getMethods() {
		return getParametersParent().getMethods(getParameter());
	}

	public List<MethodParameterNode> getLinkers(){
		List<MethodParameterNode> result = new ArrayList<>();
		for(MethodNode method : getMethods()){
			result.addAll(method.getLinkers(this));
		}
		return result;
	}

	@Override
	public Object accept(IParameterVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public Object accept(IChoicesParentVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	public String getQualifiedName() {
		if(getParent() == getRoot() || getParent() == null){
			return getName();
		}
		return getParent().getName() + ":" + getName();
	}

	@Override
	public String toString(){
		return getName() + ": " + getType();
	}

	@Override
	public GlobalParametersParentNode getParametersParent(){
		return (GlobalParametersParentNode)getParent();
	}

	@Override
	public Set<ConstraintNode> mentioningConstraints() {
		Set<ConstraintNode> result = new HashSet<ConstraintNode>();
		for(MethodParameterNode parameter : getLinkers()){
			result.addAll(parameter.mentioningConstraints());
		}
		return result;
	}

	@Override
	public Set<ConstraintNode> mentioningConstraints(String label){
		Set<ConstraintNode> result = new HashSet<ConstraintNode>();
		for(MethodParameterNode parameter : getLinkers()){
			for(ConstraintNode constraint : parameter.mentioningConstraints()){
				if(constraint.mentions(parameter, label)){
					result.add(constraint);
				}
			}
		}
		return result;
	}

	public List<ChoiceNode> getChoicesCopy() {
		List<ChoiceNode> copy = new ArrayList<>();
		for(ChoiceNode choice : getChoices()){
			copy.add(choice.getCopy());
		}
		return copy;
	}
}
