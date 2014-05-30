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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PartitionNode extends GenericNode implements IPartitionedNode{

	private IPartitionedNode fPartitionedParent;
	private PartitionNode fParentPartition;
	
	private String fValueString;
	private List<PartitionNode> fPartitions;
	private Set<String> fLabels;
	
	public PartitionNode(String name, String value) {
		super(name);
		fValueString = value;
		fPartitions = new ArrayList<PartitionNode>();
		fLabels = new LinkedHashSet<String>();
	}

	public void addPartition(PartitionNode partition){
		fPartitions.add(partition);
		partition.setParent(this);
	}

	public CategoryNode getCategory() {
		return fPartitionedParent.getCategory();
	}

	public PartitionNode getPartition(String name){
		for(PartitionNode partition : fPartitions){
			if(partition.getName().equals(name)){
				return partition;
			}
		}
		return null;
	}

	public List<PartitionNode> getPartitions(){
		return fPartitions;
	}

	@Override
	public List<String> getAllPartitionNames() {
		List<String> names = new ArrayList<String>();
		for(PartitionNode child : fPartitions){
			names.add(child.getQualifiedName());
			names.addAll(child.getAllPartitionNames());
		}
		return names;
	}

	public List<PartitionNode> getLeafPartitions() {
		List<PartitionNode> leafs = new ArrayList<PartitionNode>();
		if(fPartitions.size() == 0){
			leafs.add(this);
		}
		else{
			for(PartitionNode child : fPartitions){
				leafs.addAll(child.getLeafPartitions());
			}
		}
		return leafs;
	}

	@Override
	public void partitionRemoved(PartitionNode partition) {
		getParent().partitionRemoved(partition);
	}

	public boolean removePartition(PartitionNode partition){
		boolean result = fPartitions.remove(partition); 
		if(result && getCategory() != null){
			getCategory().partitionRemoved(partition);
		}
		return result;
	}

	public boolean removePartition(String name){
		for(PartitionNode partition : fPartitions){
			if(partition.getName().equals(name)){
				return fPartitions.remove(partition);
			}
		}
		return false;
	}

	@Override
	public IPartitionedNode getParent(){
		return fPartitionedParent;
	}

	@Override
	public List<? extends IGenericNode> getChildren(){
		return getPartitions();
	}

	public String toString(){
		if(isAbstract()){
			return getQualifiedName() + "[ABSTRACT]";
		}
		return getQualifiedName() + " [" + getValueString() + "]";
	}

	public String getQualifiedName(){
		if(fParentPartition != null){
			return fParentPartition.getQualifiedName() + ":" + getName();
		}
		return getName();
	}

	public void setParent(IPartitionedNode parent){
		fPartitionedParent = parent;
	}
	
	public void setParent(PartitionNode parentPartition){
		fPartitionedParent = fParentPartition = parentPartition;
	}
	
	public String getValueString() {
		return fValueString;
	}

	public void setValueString(String value) {
		fValueString = value;
	}

	public PartitionNode getCopy() {
		PartitionNode copy = new PartitionNode(getName(), fValueString);
		copy.setParent(fPartitionedParent);
		return copy;
	}
	
	/*
	 * Returns name of this partition and names of all parent partitions
	 */
	public List<String> getAllAncestorsNames(){
		List<String> names;
		if(fParentPartition != null){
			names = fParentPartition.getAllAncestorsNames();
		}
		else{
			names = new ArrayList<String>();
		}
		names.add(getName());
		return names;
	}

	public boolean addLabel(String label){
		if(getAllLabels().contains(label) == false){
			for(PartitionNode child : fPartitions){
				//in case when a child already was labeled with new label,
				//the parent (this) takes the label (non-reversible operation)
				child.removeLabel(label);
			}
			return fLabels.add(label);
		}
		return false;
	}
	
	public boolean removeLabel(String label){
		for(PartitionNode child : fPartitions){
			child.removeLabel(label);
		}
		return fLabels.remove(label);
	}
	
	public Set<String> getLabels(){
		return fLabels;
	}
	
	public Set<String> getAllLabels(){
		Set<String> allLabels = getInheritedLabels();
		allLabels.addAll(fLabels);
		return allLabels;
	}
	
	public Set<String> getInheritedLabels(){
		if(fParentPartition != null){
			return fParentPartition.getAllLabels();
		}
		return new HashSet<String>();
	}
	
	public Set<String> getAllDescendingLabels() {
		Set<String> labels = getLabels();
		for(PartitionNode p : fPartitions){
			labels.addAll(p.getAllDescendingLabels());
		}
		return labels;
	}

	public boolean isAbstract(){
		return fPartitions.size() != 0;
	}
	
	public boolean is(PartitionNode partition){
		return this.isDescendant(partition) || this == (partition);
	}
	
	public boolean isAncestor(PartitionNode partition) {
		for(PartitionNode child : fPartitions){
			if(child == partition || child.isAncestor(partition)){
				return true;
			}
		}
		return false;
	}

	public boolean isDescendant(PartitionNode partition){
		if(fParentPartition != null){
			return fParentPartition == partition || fParentPartition.isDescendant(partition);
		}
		return false;
	}

	public int level(){
		if(fParentPartition == null){
			return 0;
		}
		return fParentPartition.level() + 1;
	}
}
