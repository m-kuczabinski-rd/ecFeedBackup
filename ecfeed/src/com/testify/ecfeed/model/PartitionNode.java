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

	@Override
	public void addPartition(PartitionNode partition){
		addPartition(partition, fPartitions.size());
	}

	@Override
	public void addPartition(PartitionNode partition, int index) {
			fPartitions.add(index, partition);
			partition.setParent(this);
	}

	@Override
	public CategoryNode getCategory() {
		return fPartitionedParent.getCategory();
	}

	@Override
	public PartitionNode getPartition(String name){
		for(PartitionNode partition : fPartitions){
			if(partition.getName().equals(name)){
				return partition;
			}
		}
		return null;
	}

	@Override
	public List<PartitionNode> getPartitions(){
		return fPartitions;
	}

	public List<String> getPartitionNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(PartitionNode partition : getPartitions()){
			names.add(partition.getName());
		}
		return names;
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

	@Override
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
	public boolean removePartition(PartitionNode partition){
		if(fPartitions.contains(partition) && fPartitions.remove(partition)){
			partition.setParent(null);
			return true;
		}
		return false;
	}

	@Override
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
		copy.setParent(fPartitionedParent);
		for(PartitionNode partition : fPartitions){
			copy.addPartition(partition.getCopy());
		}
		for(String label : fLabels){
			copy.addLabel(label);
		}
		return copy;
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
		// FIXME remove category dependency
		try {
			String type = getCategory().getType();
			if ((fValueString.length()) > 1) {
				if (type.equals("char") && (fValueString.charAt(0) == '\\')) {
					String value = fValueString.substring(1);
					return "\\" + Integer.parseInt(value) + " ['" + Character.valueOf((char)Integer.parseInt(value)) + "']";
				} else if (type.equals("byte")) {
					return Byte.decode(fValueString).toString();
				} else if (type.equals("int")) {
					return Integer.decode(fValueString).toString();
				} else if (type.equals("long")) {
					return Long.decode(fValueString).toString();
				} else if (type.equals("short")) {
					return Short.decode(fValueString).toString();
				}
			}
		} catch (Throwable e) {
		}
		return fValueString;
	}

	public String getExactValueString() {
		return fValueString;
	}

	public void setValueString(String value) {
		fValueString = value;
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
		Set<String> labels = new LinkedHashSet<>(getLabels());
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
	
	@Override
	public boolean compare(IGenericNode node){
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

	@Override
	public void replacePartitions(List<PartitionNode> newPpartitions) {
		fPartitions.clear();
		fPartitions.addAll(newPpartitions);
	}

	@Override
	public List<PartitionNode> getLabeledPartitions(String label) {
		List<PartitionNode> result = new ArrayList<PartitionNode>();
		for(PartitionNode p : getPartitions()){
			if(p.getLabels().contains(label)){
				result.add(p);
				result.addAll(p.getLabeledPartitions(label));
			}
		}
		return result;
	}

	@Override
	public Set<String> getLeafLabels() {
		Set<String> result = new HashSet<String>();
		for(PartitionNode p : getLeafPartitions()){
			result.addAll(p.getAllLabels());
		}
		return result;
	}
}
