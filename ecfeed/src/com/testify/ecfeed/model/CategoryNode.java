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
import java.util.List;

public class CategoryNode extends GenericNode {
	
	private final String fType;
	protected final List<PartitionNode> fPartitions;
	
	public CategoryNode(String name, String type) {
		super(name);
		fType = type;
		fPartitions = new ArrayList<PartitionNode>();
	}

	public String getType() {
		return fType;
	}

	//TODO unit tests
	public void addPartition(PartitionNode partition) {
		fPartitions.add(partition);
		partition.setParent(this);
	}
	
	//TODO unit tests
	public PartitionNode getPartition(String qualifiedName){
		return (PartitionNode)getChild(qualifiedName);
	}
	
	public List<PartitionNode> getPartitions() {
		return fPartitions;
	}


	public List<PartitionNode> getLeafPartitions(){
		List<PartitionNode> leafs = new ArrayList<PartitionNode>();
		for(PartitionNode child : fPartitions){
			leafs.addAll(child.getLeafs());
		}
		return leafs;
	}
	
	public ArrayList<String> getPartitionNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(PartitionNode partition : getPartitions()){
			names.add(partition.getName());
		}
		return names;
	}

	public boolean removePartition(PartitionNode partition){
		if(fPartitions.contains(partition) && fPartitions.remove(partition)){
			MethodNode parent = getMethod();
			if(parent != null){
				parent.partitionRemoved(partition);
			}
		}
		return false;
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
		if (name == null) return false;
		if(name.length() == 0) return false;
		if(name.length() >= Constants.MAX_PARTITION_NAME_LENGTH) return false;
		if(name.matches("[ ]+.*")) return false;
		
		return true;
	}

	public boolean validatePartitionStringValue(String valueString){
		if(fType.equals(Constants.TYPE_NAME_STRING)) return true;
		return (getPartitionValueFromString(valueString) != null);
	}

	public Object getPartitionValueFromString(String valueString){
		try{
			switch(fType){
			case Constants.TYPE_NAME_BOOLEAN:
				return Boolean.valueOf(valueString).booleanValue();
			case Constants.TYPE_NAME_BYTE:
				return Byte.valueOf(valueString).byteValue();
			case Constants.TYPE_NAME_CHAR:
				if(valueString.charAt(0) != '\\' || valueString.length() == 1) return(valueString.charAt(0));
				return Character.toChars(Integer.parseInt(valueString.substring(1)));
			case Constants.TYPE_NAME_DOUBLE:
				return Double.valueOf(valueString).doubleValue();
			case Constants.TYPE_NAME_FLOAT:
				return Float.valueOf(valueString).floatValue();
			case Constants.TYPE_NAME_INT:
				return Integer.valueOf(valueString).intValue();
			case Constants.TYPE_NAME_LONG:
				return Long.valueOf(valueString).longValue();
			case Constants.TYPE_NAME_SHORT:
				return Short.valueOf(valueString).shortValue();
			case Constants.TYPE_NAME_STRING:
				return valueString;
			default:
				return null;
			}
		}catch(NumberFormatException|IndexOutOfBoundsException e){
			return null;
		}
	}

	public List<? extends IGenericNode> getChildren(){
		return fPartitions;
	}

	public String toString(){
		return new String(getName() + ": " + getType());
	}

	@Deprecated
	public boolean isExpected() {
		return false;
	}

	public void partitionRemoved(PartitionNode partition) {
		if(getMethod() != null){
			getMethod().partitionRemoved(partition);
		}
	}
}
