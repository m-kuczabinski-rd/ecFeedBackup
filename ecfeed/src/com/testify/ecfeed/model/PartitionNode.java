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

public class PartitionNode extends PartitionedNode{

	private PartitionedNode fParent;
	private String fValueString;
	private Set<String> fLabels;
	
	public PartitionNode(String name, String value) {
		super(name);
		fValueString = value;
		fLabels = new LinkedHashSet<String>();
	}

	@Override
	public ParameterNode getCategory() {
		if(fParent != null){
			return fParent.getCategory();
		}
		return null;
	}

	@Override
	public PartitionedNode getParent(){
		return fParent;
	}

	@Override
	public List<? extends GenericNode> getChildren(){
		return getPartitions();
	}

	@Override
	public String toString(){
		if(isAbstract()){
			return getQualifiedName() + "[ABSTRACT]";
		}
		return getQualifiedName() + " [" + getValueString() + "]";
	}

	@Override
	public PartitionNode getCopy(){
		PartitionNode copy = new PartitionNode(getName(), fValueString);
		copy.setParent(fParent);
		for(PartitionNode partition : getPartitions()){
			copy.addPartition(partition.getCopy());
		}
		for(String label : fLabels){
			copy.addLabel(label);
		}
		return copy;
	}

	public String getQualifiedName(){
		if(parentPartition() != null){
			return parentPartition().getQualifiedName() + ":" + getName();
		}
		return getName();
	}

	public void setParent(PartitionedNode parent){
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
		if(parentPartition() != null){
			return parentPartition().getAllLabels();
		}
		return new LinkedHashSet<String>();
	}

	public boolean isAbstract(){
		return getPartitions().size() != 0;
	}
	
	public boolean is(PartitionNode partition){
		return (this == (partition)) || (parentPartition() != null ? parentPartition().is(partition) : false);
	}
	
	public int level(){
		if(parentPartition() == null){
			return 0;
		}
		return parentPartition().level() + 1;
	}
	
	@Override
	public boolean compare(GenericNode node){
		if(node instanceof PartitionNode == false){
			return false;
		}
		
		PartitionNode compared = (PartitionNode)node;
		
		if(getLabels().equals(compared.getLabels()) == false){
			return false;
		}
		
		if(getValueString().equals(compared.getValueString()) == false){
			return false;
		}
		
		if(getPartitions().size() != compared.getPartitions().size()){
			return false;
		}
		
		for(int i = 0; i < getPartitions().size(); i++){
			if(getPartitions().get(i).compare(compared.getPartitions().get(i)) == false){
				return false;
			}
		}
		
		return super.compare(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	private PartitionNode parentPartition(){
		if(fParent != null && fParent != getCategory()){
			return (PartitionNode)fParent;
		}
		return null;
	}
}
