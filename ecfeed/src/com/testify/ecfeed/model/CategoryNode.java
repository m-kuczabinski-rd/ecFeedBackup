package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CategoryNode extends GenericNode implements IPartitionedNode{
	
	private String fType;
	protected final List<PartitionNode> fPartitions;
	private boolean fExpected;
	private PartitionNode fDefaultValue;
	
	@Override
	public void partitionRemoved(PartitionNode partition) {
		if(getMethod() != null){
			getMethod().partitionRemoved(partition);
		}
	}

	@Override
	public String toString(){
		if(fExpected){
			return super.toString() + "(" + getDefaultValueString() + ")";
		}
		return new String(getName() + ": " + getType());
	}
	
	@Override
	public void addPartition(PartitionNode partition) {
			fPartitions.add(partition);
			partition.setParent(this);
	}
	
	@Override
	public PartitionNode getPartition(String qualifiedName){
		if(fExpected){
			return null;
		}
		return (PartitionNode)getChild(qualifiedName);
	}
	
	@Override
	public List<PartitionNode> getPartitions() {
		if(fExpected){
			return Arrays.asList(new PartitionNode[]{fDefaultValue});
		}
		return fPartitions;
	}

	@Override
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
	
	@Override
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

	@Override
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

	@Override
	public List<? extends IGenericNode> getChildren(){
		if(fExpected){
			return EMPTY_CHILDREN_ARRAY;
		}
		return fPartitions;
	}
	
	@Override
	public CategoryNode getCopy(){
		CategoryNode category = new CategoryNode(getName(), getType(), isExpected());
		category.setParent(this.getParent());
		if(getDefaultValueString() != null)
			category.setDefaultValueString(getDefaultValueString());
		for(PartitionNode partition : fPartitions){
			category.addPartition(partition.getCopy());
		}
		category.setParent(getParent());
		return category;
	}
	
	@Override
	public CategoryNode getCategory() {
		return this;
	}
	
	public CategoryNode(String name, String type, boolean expected) {
		super(name);
		fExpected = expected;
		fType = type;
		fPartitions = new ArrayList<PartitionNode>();
		fDefaultValue = new PartitionNode("default value" , "/null");
		fDefaultValue.setParent(this);
	}

	public String getType() {
		return fType;
	}

	public void setType(String type) {
		fType = type;
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
	
	public PartitionNode getDefaultValuePartition(){
		return fDefaultValue;
	}

	public String getDefaultValueString() {
		return fDefaultValue.getValueString();
	}

	public void setDefaultValueString(String value) {
		fDefaultValue.setValueString(value);
	}
	
	public boolean isExpected(){
		return fExpected;
	}
	
	public void setExpected(boolean isexpected){
		fExpected = isexpected;
	}
	
	public List<PartitionNode> getOrdinaryPartitions(){
		return fPartitions;
	}
	
	public String toShortString(){
		if(fExpected){
			return toString();
		}

		return new String(getName() + ": " + getShortType());
	}
	
	public String getShortType(){
		String type = fType;
		int lastindex = type.lastIndexOf(".");
		if(!(lastindex == -1 || lastindex >= type.length())){
			type = type.substring(lastindex + 1);
		}
		return new String(type);
	}


	
}
