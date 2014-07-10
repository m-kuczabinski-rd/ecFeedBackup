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

public interface IPartitionedNode extends IGenericNode{
	public CategoryNode getCategory();
	public List<PartitionNode> getPartitions();
	public void addPartition(PartitionNode partition);
	public PartitionNode getPartition(String name);
	public boolean removePartition(PartitionNode partition);
	public boolean removePartition(String name);
	public List<PartitionNode> getLeafPartitions();
	public List<String> getAllPartitionNames();
	public void partitionRemoved(PartitionNode partition);
}
