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

public class ExpectedValueCategoryNode extends CategoryNode implements
		IGenericNode {
	private PartitionNode fDefaultValue;

	public ExpectedValueCategoryNode(String name, String type, Object defaultValue) {
		super(name, type);
		fDefaultValue = new PartitionNode("default value" , defaultValue);
		fDefaultValue.setParent(this);
		fPartitions.add(fDefaultValue);
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
	
	@Override
	public List<? extends IGenericNode> getChildren() {
		return EMPTY_CHILDREN_ARRAY;
	}

	public String toString(){
		return super.toString() + "(" + getDefaultValue() + ")";
	}

	@Override
	public boolean isExpected(){
		return true;
	}
}
