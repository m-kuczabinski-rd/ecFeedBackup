/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;


public class ParameterNode extends AbstractParameterNode{

	private boolean fExpected;
	private String fDefaultValue;

	public ParameterNode(String name, String type, String defaultValue, boolean expected) {
		super(name, type);
		fExpected = expected;
		fDefaultValue = defaultValue;
	}

	@Override
	public String toString(){
		if(fExpected){
			return super.toString() + "(" + getDefaultValue() + "): " + getType();
		}
		return new String(getName() + ": " + getType());
	}

	@Override
	public ParameterNode getCopy(){
		ParameterNode parameter = new ParameterNode(getName(), getType(), getDefaultValue(), isExpected());
		parameter.setParent(this.getParent());
		if(getDefaultValue() != null)
			parameter.setDefaultValueString(getDefaultValue());
		for(ChoiceNode choice : getChoices()){
			parameter.addChoice(choice.getCopy());
		}
		parameter.setParent(getParent());
		return parameter;
	}

	@Override
	public ParameterNode getParameter() {
		return this;
	}

	public MethodNode getMethod() {
		return (MethodNode)getParent();
	}

	public String getDefaultValue() {
		return fDefaultValue;
	}

	public void setDefaultValueString(String value) {
		fDefaultValue = value;
	}

	public boolean isExpected(){
		return fExpected;
	}

	public void setExpected(boolean isexpected){
		fExpected = isexpected;
	}

	@Override
	public boolean compare(AbstractNode node){
		if(node instanceof ParameterNode == false){
			return false;
		}
		ParameterNode comparedParameter = (ParameterNode)node;

		if(getType().equals(comparedParameter.getType()) == false){
			return false;
		}

		if(isExpected() != comparedParameter.isExpected()){
			return false;
		}

		if(fDefaultValue.equals(comparedParameter.getDefaultValue()) == false){
			return false;
		}

		int choicesCount = getChoices().size();
		if(choicesCount != comparedParameter.getChoices().size()){
			return false;
		}

		for(int i = 0; i < choicesCount; i++){
			if(getChoices().get(i).compare(comparedParameter.getChoices().get(i)) == false){
				return false;
			}
		}

		return super.compare(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}
}
