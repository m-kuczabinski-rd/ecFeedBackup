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

import java.util.List;

import com.testify.ecfeed.constants.Constants;

public class PartitionNode extends GenericNode {

	private Object fValue;
	private List<PartitionNode> fPartitions;

	public PartitionNode(String name, Object value) {
		super(name);
		fValue = value;
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
	
	public String toString(){
		return getName() + ": " + getValueString();
	}
	
	public CategoryNode getCategory() {
		return (CategoryNode)getParent();
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
	
	public boolean removePartition(PartitionNode partition){
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
}
