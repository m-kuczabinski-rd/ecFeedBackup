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
import java.util.Set;

public interface IPartitionedNode extends IGenericNode{
	//quick fix
	//TODO: make PartitionedNode abstract class for category and partition
	public int getIndex();
	
	public CategoryNode getCategory();
	public List<PartitionNode> getPartitions();
	public void addPartition(PartitionNode partition);
	public void addPartition(PartitionNode partition, int index);
	public PartitionNode getPartition(String name);
	public boolean removePartition(PartitionNode partition);
	public boolean removePartition(String name);
	public void replacePartitions(List<PartitionNode> newPpartitions);
	public List<PartitionNode> getLeafPartitions();
	public List<String> getAllPartitionNames();
	public List<String> getPartitionNames();
	public List<PartitionNode> getLabeledPartitions(String label);
	public Set<String> getLeafLabels();
}
