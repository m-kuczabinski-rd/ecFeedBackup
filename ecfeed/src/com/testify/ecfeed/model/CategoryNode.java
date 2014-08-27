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

import java.util.List;

public class CategoryNode extends PartitionedNode{
	
	private String fType;
	private boolean fExpected;
	private PartitionNode fDefaultValue;
	
	public CategoryNode(String name, String type, String defaultValue, boolean expected) {
		super(name);
		fExpected = expected;
		fType = type;
		fDefaultValue = new PartitionNode("default value" , defaultValue);
		fDefaultValue.setParent(this);
	}
	
	@Override
	public int getIndex(){
		if(getMethod() == null){
			return -1;
		}
		return getMethod().getCategories().indexOf(this);
	}

	@Override
	public String toString(){
		if(fExpected){
			return super.toString() + "(" + getDefaultValueString() + "): " + getType();
		}
		return new String(getName() + ": " + getType());
	}
	
	public void addPartitions(List<PartitionNode> partitions) {
		for(PartitionNode p : partitions){
			addPartition(p);
		}
	}

	@Override
	public CategoryNode getCopy(){
		CategoryNode category = new CategoryNode(getName(), getType(), getDefaultValueString(), isExpected());
		category.setParent(this.getParent());
		if(getDefaultValueString() != null)
			category.setDefaultValueString(getDefaultValueString());
		for(PartitionNode partition : getPartitions()){
			category.addPartition(partition.getCopy());
		}
		category.setParent(getParent());
		return category;
	}
	
	@Override
	public CategoryNode getCategory() {
		return this;
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

	@Override
	public boolean compare(IGenericNode node){
		if(node instanceof CategoryNode == false){
			return false;
		}
		CategoryNode comparedCategory = (CategoryNode)node;
		
		if(getType().equals(comparedCategory.getType()) == false){
			return false;
		}

		if(isExpected() != comparedCategory.isExpected()){
			return false;
		}
		
		if(getDefaultValuePartition().compare(comparedCategory.getDefaultValuePartition()) == false){
			return false;
		}

		int partitionsCount = getPartitions().size();
		if(partitionsCount != comparedCategory.getPartitions().size()){
			return false;
		}

		for(int i = 0; i < partitionsCount; i++){
			if(getPartitions().get(i).compare(comparedCategory.getPartitions().get(i)) == false){
				return false;
			}
		}

		return super.compare(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override 
	public int getMaxIndex(){
		if(getMethod() != null){
			return getMethod().getCategories().size();
		}
		return -1;
	}
}
