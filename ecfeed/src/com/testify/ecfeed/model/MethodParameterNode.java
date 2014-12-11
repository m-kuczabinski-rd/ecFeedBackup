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

import java.util.Arrays;
import java.util.List;


public class MethodParameterNode extends AbstractParameterNode{

	private boolean fExpected;
	private String fDefaultValue;
	private boolean fLinked;
	private GlobalParameterNode fLink;

	public MethodParameterNode(String name, String type, String defaultValue, boolean expected, boolean linked, GlobalParameterNode link) {
		super(name, type);
		fExpected = expected;
		fDefaultValue = defaultValue;
		fLinked = linked;
		fLink = link;
	}

	public MethodParameterNode(String name, String type, String defaultValue, boolean expected) {
		this(name, type, defaultValue, expected, false, null);
	}

	public MethodParameterNode(AbstractParameterNode source, String defaultValue, boolean expected, boolean linked, GlobalParameterNode link) {
		this(source.getName(), source.getType(), defaultValue, expected, linked, link);
		addChoices(source.getChoices());
	}

	public MethodParameterNode(AbstractParameterNode source, String defaultValue, boolean expected) {
		this(source, defaultValue, expected, false, null);
	}

	@Override
	public String toString(){
		if(fExpected){
			return super.toString() + "(" + getDefaultValue() + "): " + getType();
		}
		return new String(getName() + ": " + getType());
	}

	@Override
	public MethodParameterNode getCopy(){
		MethodParameterNode parameter = new MethodParameterNode(getName(), getType(), getDefaultValue(), isExpected());
		parameter.setParent(this.getParent());
		if(getDefaultValue() != null)
			parameter.setDefaultValueString(getDefaultValue());
		for(ChoiceNode choice : fChoices){
			parameter.addChoice(choice.getCopy());
		}
		parameter.setParent(getParent());
		return parameter;
	}

	@Override
	public String getType(){
		if(isLinked() && fLink != null){
			return fLink.getType();
		}
		return super.getType();
	}

	public String getRealType() {
		return super.getType();
	}

	@Override
	public List<ChoiceNode> getChoices(){
		if(isLinked() && fLink != null){
			return fLink.getChoices();
		}
		return super.getChoices();
	}

	@Override
	public ChoiceNode getChoice(String qualifiedName) {
		if(isLinked()){
			return getLink().getChoice(qualifiedName);
		}
		return super.getChoice(qualifiedName);
	}

	public List<ChoiceNode> getRealChoices() {
		return super.getChoices();
	}

	@Override
	public List<MethodNode> getMethods() {
		return Arrays.asList(new MethodNode[]{getMethod()});
	}

	public List<ChoiceNode> getOwnChoices(){
		return super.getChoices();
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

	public boolean isLinked() {
		return fLinked;
	}

	public void setLinked(boolean fLinked) {
		this.fLinked = fLinked;
	}

	public GlobalParameterNode getLink() {
		return fLink;
	}

	public void setLink(GlobalParameterNode link) {
		this.fLink = link;
	}

	@Override
	public boolean compare(AbstractNode node){
		if(node instanceof MethodParameterNode == false){
			return false;
		}
		MethodParameterNode comparedParameter = (MethodParameterNode)node;

		if(getType().equals(comparedParameter.getType()) == false){
			return false;
		}

		if(isExpected() != comparedParameter.isExpected()){
			return false;
		}

		if(fDefaultValue.equals(comparedParameter.getDefaultValue()) == false){
			return false;
		}

		int choicesCount = fChoices.size();
		if(choicesCount != comparedParameter.fChoices.size()){
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

	@Override
	public Object accept(IChoicesParentVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	@Override
	public Object accept(IParameterVisitor visitor) throws Exception {
		return visitor.visit(this);
	}
}
