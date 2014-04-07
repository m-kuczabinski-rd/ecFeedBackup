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

import java.util.Arrays;
import java.util.List;

public class ExpectedCategoryNode extends AbstractCategoryNode{
	
	private PartitionNode fDefaultValue;

	public ExpectedCategoryNode(String name, String type, Object defaultValue) {
		super(name, type);
		fDefaultValue = new PartitionNode("default value" , defaultValue);
		fDefaultValue.setParent(this);
	}

	@Override
	public List<? extends IGenericNode> getChildren() {
		return EMPTY_CHILDREN_ARRAY;
	}

	@Override
	public AbstractCategoryNode getCategory() {
		return this;
	}

	@Override
	public List<PartitionNode> getPartitions() {
		return Arrays.asList(new PartitionNode[]{fDefaultValue});
	}

	@Override
	public void addPartition(PartitionNode partition) {
	}

	@Override
	public PartitionNode getPartition(String name) {
		return null;
	}

	@Override
	public boolean removePartition(PartitionNode partition) {
		return false;
	}

	@Override
	public boolean removePartition(String name) {
		return false;
	}

	@Override
	public List<PartitionNode> getLeafPartitions() {
		return getPartitions();
	}

	@Override
	public List<String> getAllPartitionNames() {
		return Arrays.asList(new String[]{fDefaultValue.getName()});
	}

	public String toString(){
		return super.toString() + "(" + getDefaultValue() + ")";
	}

	public PartitionNode getDefaultValuePartition(){
		return fDefaultValue;
	}

	public Object getDefaultValue() {
		return fDefaultValue.getValue();
	}

	public void setDefaultValue(Object value) {
		fDefaultValue.setValue(value);
	}
}
