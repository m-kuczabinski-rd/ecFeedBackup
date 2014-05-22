package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CategoryNode extends GenericNode implements IPartitionedNode{
	
	private final String fType;
	protected final List<PartitionNode> fPartitions;
	private boolean fExpected;
	private PartitionNode fDefaultValue;
	
	public CategoryNode(String name, String type, boolean expected) {
		super(name);
		fExpected = expected;
		fType = type;
		fPartitions = new ArrayList<PartitionNode>();
		fDefaultValue = new PartitionNode("default value" , null);
		fDefaultValue.setParent(this);
	}

	public String getType() {
		return fType;
	}

	public MethodNode getMethod() {
		return (MethodNode)getParent();
	}

	/**
	 * Checks if certain name is valid for given partition in given category
	 * @param name Name to validate
	 * @param parent Parent for which the name is validated
	 * @param partition Partition for which the name is validated. May be null
	 * @return
	 */ 
	public boolean validatePartitionName(String name){
		return validateNodeName(name);
	}

	public void partitionRemoved(PartitionNode partition) {
		if(getMethod() != null){
			getMethod().partitionRemoved(partition);
		}
	}

	public String toString(){
		if(fExpected){
			return super.toString() + "(" + getDefaultValue() + ")";
		}
		return new String(getName() + ": " + getType());
	}
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~PARTITIONED~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public void addPartition(PartitionNode partition) {
		if(!fExpected){
			fPartitions.add(partition);
			partition.setParent(this);
		}
	}
	
	@Override
	public CategoryNode getCategory() {
		return this;
	}

	public PartitionNode getPartition(String qualifiedName){
		if(fExpected){
			return null;
		}
		return (PartitionNode)getChild(qualifiedName);
	}
	
	public List<PartitionNode> getPartitions() {
		if(fExpected){
			return Arrays.asList(new PartitionNode[]{fDefaultValue});
		}
		return fPartitions;
	}


	public List<PartitionNode> getLeafPartitions(){
		if(fExpected){
			return getPartitions();
		}
		List<PartitionNode> leafs = new ArrayList<PartitionNode>();
		for(PartitionNode child : fPartitions){
			leafs.addAll(child.getLeafPartitions());
		}
		return leafs;
	}
	
	public List<String> getAllPartitionNames(){
		if(fExpected){
			return Arrays.asList(new String[]{fDefaultValue.getName()});
		}
		List<String> names = new ArrayList<String>();
		for(PartitionNode child : getPartitions()){
			names.add(child.getQualifiedName());
			names.addAll(child.getAllPartitionNames());
		}
		return names;
	}

	public boolean removePartition(PartitionNode partition){
		if(fExpected){
			return false;
		}
		if(fPartitions.contains(partition) && fPartitions.remove(partition)){
			MethodNode parentMethod = getMethod();
			if(parentMethod != null){
				parentMethod.partitionRemoved(partition);
			}
		}
		return false;
	}

	@Override
	public boolean removePartition(String qualifiedName) {
		if(fExpected){
			return false;
		}
		return removePartition(getPartition(qualifiedName));
	}

	public List<? extends IGenericNode> getChildren(){
		if(fExpected){
			return EMPTY_CHILDREN_ARRAY;
		}
		return fPartitions;
	}

	public List<String> getPartitionNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(PartitionNode partition : getPartitions()){
			names.add(partition.getName());
		}
		return names;
	}

	public List<String> getLeafPartitionNames(){
		List<PartitionNode> leafPartitions = getLeafPartitions();
		List<String> names = new ArrayList<String>();
		for(PartitionNode leaf : leafPartitions){
			names.add(leaf.getQualifiedName());
		}
		return names;
	}
	
	public Set<String> getAllPartitionLabels(){
		Set<String> labels = new LinkedHashSet<String>();
		for(PartitionNode p : getPartitions()){
			labels.addAll(p.getAllDescendingLabels());
		}
		return labels;
	}
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~EXPECTED~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PartitionNode getDefaultValuePartition(){
		return fDefaultValue;
	}

	public Object getDefaultValue() {
		return fDefaultValue.getValue();
	}

	public void setDefaultValue(Object value) {
		fDefaultValue.setValue(value);
	}
	
	public boolean isExpected(){
		return fExpected;
	}
}
