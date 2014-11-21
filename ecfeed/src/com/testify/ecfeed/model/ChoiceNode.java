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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ChoiceNode extends ChoicesParentNode{

	private ChoicesParentNode fParent;
	private String fValueString;
	private Set<String> fLabels;
	
	public ChoiceNode(String name, String value) {
		super(name);
		fValueString = value;
		fLabels = new LinkedHashSet<String>();
	}

	@Override
	public ParameterNode getParameter() {
		if(fParent != null){
			return fParent.getParameter();
		}
		return null;
	}

	@Override
	public ChoicesParentNode getParent(){
		return fParent;
	}

	@Override
	public List<? extends AbstractNode> getChildren(){
		return getChoices();
	}

	@Override
	public String toString(){
		if(isAbstract()){
			return getQualifiedName() + "[ABSTRACT]";
		}
		return getQualifiedName() + " [" + getValueString() + "]";
	}

	@Override
	public ChoiceNode getCopy(){
		ChoiceNode copy = new ChoiceNode(getName(), fValueString);
		copy.setParent(fParent);
		for(ChoiceNode choice : getChoices()){
			copy.addChoice(choice.getCopy());
		}
		for(String label : fLabels){
			copy.addLabel(label);
		}
		return copy;
	}

	public String getQualifiedName(){
		if(parentChoice() != null){
			return parentChoice().getQualifiedName() + ":" + getName();
		}
		return getName();
	}

	public void setParent(ChoicesParentNode parent){
		super.setParent(parent);
		fParent = parent;
	}
	
	public String getValueString() {
		return fValueString;
	}

	public void setValueString(String value) {
		fValueString = value;
	}
	
	public boolean addLabel(String label){
		return fLabels.add(label);
	}
	
	public boolean removeLabel(String label){
		return fLabels.remove(label);
	}
	
	public Set<String> getLabels(){
		return fLabels;
	}

	@Override
	public Set<String> getLeafLabels() {
		if(isAbstract() == false){
			return getAllLabels();
		}
		return super.getLeafLabels();
	}
	

	public Set<String> getAllLabels(){
		Set<String> allLabels = getInheritedLabels();
		allLabels.addAll(fLabels);
		return allLabels;
	}
	
	public Set<String> getInheritedLabels(){
		if(parentChoice() != null){
			return parentChoice().getAllLabels();
		}
		return new LinkedHashSet<String>();
	}

	public boolean isAbstract(){
		return getChoices().size() != 0;
	}
	
	public boolean is(ChoiceNode choice){
		return (this == (choice)) || (parentChoice() != null ? parentChoice().is(choice) : false);
	}
	
	public int level(){
		if(parentChoice() == null){
			return 0;
		}
		return parentChoice().level() + 1;
	}
	
	@Override
	public boolean compare(AbstractNode node){
		if(node instanceof ChoiceNode == false){
			return false;
		}
		
		ChoiceNode compared = (ChoiceNode)node;
		
		if(getLabels().equals(compared.getLabels()) == false){
			return false;
		}
		
		if(getValueString().equals(compared.getValueString()) == false){
			return false;
		}
		
		if(getChoices().size() != compared.getChoices().size()){
			return false;
		}
		
		for(int i = 0; i < getChoices().size(); i++){
			if(getChoices().get(i).compare(compared.getChoices().get(i)) == false){
				return false;
			}
		}
		
		return super.compare(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	private ChoiceNode parentChoice(){
		if(fParent != null && fParent != getParameter()){
			return (ChoiceNode)fParent;
		}
		return null;
	}
}
