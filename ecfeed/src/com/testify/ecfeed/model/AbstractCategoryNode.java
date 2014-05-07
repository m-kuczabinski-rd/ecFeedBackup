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


public abstract class AbstractCategoryNode extends GenericNode implements IPartitionedNode{
	
	private final String fType;
	
	public AbstractCategoryNode(String name, String type) {
		super(name);
		fType = type;
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
		return new String(getName() + ": " + getType());
	}
}
