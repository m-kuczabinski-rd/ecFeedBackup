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

import java.util.Vector;

public class CategoryNode extends GenericNode {
	
	private final String fType;
	private final Vector<PartitionNode> fPartitions;
	
	public CategoryNode(String name, String type) {
		super(name);
		fType = type;
		fPartitions = new Vector<PartitionNode>();
	}

	public String getType() {
		return fType;
	}

	public Vector<? extends IGenericNode> getChildren(){
		return fPartitions;
	}
	
	//TODO unit tests
	public void addPartition(PartitionNode partition) {
		fPartitions.add(partition);
		partition.setParent(this);
	}
	
	//TODO unit tests
	public PartitionNode getPartition(String name){
		return (PartitionNode) super.getChild(name);
	}

	public String toString(){
		return new String(getName() + ": " + getType());
	}

	//TODO unit tests
	public Vector<PartitionNode> getPartitions() {
		return fPartitions;
	}

	public Vector<String> getPartitionNames() {
		Vector<String> names = new Vector<String>();
		for(PartitionNode partition : getPartitions()){
			names.add(partition.getName());
		}
		return names;
	}
}
