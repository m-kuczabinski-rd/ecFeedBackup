package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class PartitionedNode extends GenericNode{

	private List<PartitionNode> fPartitions;

	public PartitionedNode(String name) {
		super(name);
		fPartitions = new ArrayList<PartitionNode>();
	}

	public abstract ParameterNode getCategory();

	@Override
	public List<? extends GenericNode> getChildren(){
		return fPartitions;
	}

	public List<PartitionNode> getPartitions() {
		return fPartitions;
	}

	public void addPartition(PartitionNode partition) {
		addPartition(partition, fPartitions.size());
	}

	public void addPartition(PartitionNode partition, int index) {
			fPartitions.add(index, partition);
			partition.setParent(this);
	}

	public PartitionNode getPartition(String qualifiedName) {
		return (PartitionNode)getChild(qualifiedName);
	}

	public boolean removePartition(PartitionNode partition) {
		if(fPartitions.contains(partition) && fPartitions.remove(partition)){
			partition.setParent(null);
			return true;
		}
		return false;
	}

	public void replacePartitions(List<PartitionNode> newPartitions) {
		fPartitions.clear();
		fPartitions.addAll(newPartitions);
		for(PartitionNode p : newPartitions){
			p.setParent(this);
		}
	}

	public List<PartitionNode> getLeafPartitions() {
		List<PartitionNode> result = new ArrayList<PartitionNode>();
		for(PartitionNode p : fPartitions){
			if(p.isAbstract() == false){
				result.add(p);
			}
			result.addAll(p.getLeafPartitions());
		}
		return result;
	}

	public Set<PartitionNode> getAllPartitions() {
		Set<PartitionNode> result = new LinkedHashSet<PartitionNode>();
		for(PartitionNode p : fPartitions){
			result.add(p);
			result.addAll(p.getAllPartitions());
		}
		return result;
	}

	public Set<String> getAllPartitionNames() {
		Set<String> result = new LinkedHashSet<String>();
		for(PartitionNode p : fPartitions){
			result.add(p.getQualifiedName());
			result.addAll(p.getAllPartitionNames());
		}
		return result;
	}

	public Set<String> getPartitionNames() {
		Set<String> result = new LinkedHashSet<String>();
		for(PartitionNode p : fPartitions){
			result.add(p.getName());
		}
		return result;
	}

	public Set<PartitionNode> getLabeledPartitions(String label) {
		Set<PartitionNode> result = new LinkedHashSet<PartitionNode>();
		for(PartitionNode p : fPartitions){
			if(p.getLabels().contains(label)){
				result.add(p);
			}
			result.addAll(p.getLabeledPartitions(label));
		}
		return result;
	}

	public Set<String> getLeafLabels() {
		Set<String> result = new LinkedHashSet<String>();
		for(PartitionNode p : getLeafPartitions()){
			result.addAll(p.getAllLabels());
		}
		return result;
	}

	public Set<String> getLeafPartitionValues(){
		Set<String> result = new LinkedHashSet<String>();
		for(PartitionNode p : getLeafPartitions()){
			result.add(p.getValueString());
		}
		return result;
	}

	public Set<String> getLeafPartitionNames(){
		Set<String> result = new LinkedHashSet<String>();
		for(PartitionNode p : getLeafPartitions()){
			result.add(p.getQualifiedName());
		}
		return result;
	}
}