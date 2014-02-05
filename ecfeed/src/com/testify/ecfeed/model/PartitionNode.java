/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.model.Constants;

public class PartitionNode extends GenericNode {

	private Object fValue;
	private List<PartitionNode> fPartitions;
	private Set<String> fLabels;

	public PartitionNode(String name, Object value) {
		super(name);
		fValue = value;
		fPartitions = new ArrayList<PartitionNode>();
		fLabels = new HashSet<String>();
	}

	public String getQualifiedName(){
		if(getParent() instanceof PartitionNode){
			return ((PartitionNode)getParent()).getQualifiedName() + ":" + getName();
		}
		return super.getName();
	}

	public Object getValue() {
		return fValue;
	}

	public void setValue(Object value) {
		this.fValue = value;
	}
	
	public String getValueString(){
		if(fValue == null) return Constants.NULL_VALUE_STRING_REPRESENTATION;
		if(fValue instanceof Character){
			if((Character)fValue != 0) return " \\" + (int)((char)fValue ) + " ['" + fValue + "']";
			return "\\0";
		}
		return String.valueOf(fValue);
	}
	
	public CategoryNode getCategory() {
		if(getParent() instanceof CategoryNode){
			return (CategoryNode)getParent();
		}
		else{
			return ((PartitionNode)getParent()).getCategory();
		}
	}

	public PartitionNode getCopy() {
		PartitionNode copy = new PartitionNode(getName(), fValue);
		copy.setParent(getParent());
		return copy;
	}
	
	@Override
	public List<? extends IGenericNode> getChildren(){
		return getPartitions();
	}
	
	public List<PartitionNode> getPartitions(){
		return fPartitions;
	}
	
	public List<String> getAllDescendantsNames() {
		List<String> names = new ArrayList<String>();
		for(PartitionNode child : fPartitions){
			names.add(child.getQualifiedName());
			names.addAll(child.getAllDescendantsNames());
		}
		return names;
	}

	/*
	 * Returns name of this partition and names of all parent partitions
	 */
	public List<String> getAllAncestorsNames(){
		List<String> names = new ArrayList<String>();
		names.add(getName());
		if(getParent() instanceof PartitionNode){
			names.addAll(((PartitionNode)getParent()).getAllAncestorsNames());
		}
		return names;
	}

	public void addPartition(PartitionNode partition){
		fPartitions.add(partition);
		partition.setParent(this);
	}
	
	public PartitionNode getPartition(String name){
		for(PartitionNode partition : fPartitions){
			if(partition.getName().equals(name)){
				return partition;
			}
		}
		return null;
	}
	
	public boolean addLabel(String label){
		if(getAllLabels().contains(label) == false){
			return fLabels.add(label);
		}
		return false;
	}
	
	public boolean removeLabel(String label){
		return fLabels.remove(label);
	}
	
	public Set<String> getLabels(){
		return fLabels;
	}
	
	public Set<String> getAllLabels(){
		Set<String> allLabels = new HashSet<String>(fLabels);
		if(getParent() instanceof PartitionNode){
			allLabels.addAll(((PartitionNode)getParent()).getAllLabels());
		}
		return allLabels;
	}
	
	public boolean removePartition(PartitionNode partition){
		if(getCategory() != null){
			getCategory().partitionRemoved(partition);
		}
		return fPartitions.remove(partition);
	}
	
	public boolean removePartition(String name){
		for(PartitionNode partition : fPartitions){
			if(partition.getName().equals(name)){
				return fPartitions.remove(partition);
			}
		}
		return false;
	}
	
	public boolean isAbstract(){
		return fPartitions.size() != 0;
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
		if(getParent() instanceof PartitionNode){
			PartitionNode parent = (PartitionNode)getParent();
			if(parent == partition || parent.isDescendant(partition)){
				return true;
			}
		}
		return false;
	}

	public int level(){
		if(getParent() instanceof CategoryNode){
			return 0;
		}
		return ((PartitionNode)getParent()).level() + 1;
	}

	public List<PartitionNode> getLeafs() {
		List<PartitionNode> leafs = new ArrayList<PartitionNode>();
		if(fPartitions.size() == 0){
			leafs.add(this);
		}
		else{
			for(PartitionNode child : fPartitions){
				leafs.addAll(child.getLeafs());
			}
		}
		return leafs;
	}
	
	public String toString(){
		if(isAbstract()){
			return getQualifiedName() + "[ABSTRACT]";
		}
		return getQualifiedName() + " [" + getValueString() + "]";
	}
}
