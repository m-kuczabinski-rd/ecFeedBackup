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

package com.testify.ecfeed.gal.java.partition;

import com.testify.ecfeed.gal.GalException;
import com.testify.ecfeed.gal.ModelOperationManager;
import com.testify.ecfeed.gal.NodeAbstractionLayer;
import com.testify.ecfeed.model.PartitionNode;

public class PartitionAbstractionLayer extends NodeAbstractionLayer{

	PartitionNode fTarget;
	
	public PartitionAbstractionLayer(ModelOperationManager modelAbstraction) {
		super(modelAbstraction);
	}

	public void setTarget(PartitionNode partition){
		fTarget = partition;
	}
	
	public void setName(String newName) throws GalException{
		execute(new PartitionOperationRename(fTarget, newName));
	}

	public void setValue(String newValue) throws GalException{
		execute(new PartitionOperationSetValue(fTarget, newValue));
	}
	
	public String getName(){
		return fTarget.getName();
	}

	public String getValue(){
		return fTarget.getValueString();
	}
}
