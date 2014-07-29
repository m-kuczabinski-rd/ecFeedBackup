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

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.partition.PartitionOperationRename;
import com.testify.ecfeed.modelif.java.partition.PartitionOperationSetValue;
import com.testify.ecfeed.ui.editor.BasicSection;

public class PartitionInterface extends GenericNodeInterface{

	PartitionNode fTarget;
	
	public PartitionInterface(ModelOperationManager modelAbstraction) {
		super(modelAbstraction);
	}

	public void setTarget(PartitionNode partition){
		fTarget = partition;
	}
	
	public void setName(String newName, BasicSection source){
		execute(new PartitionOperationRename(fTarget, newName), source, Messages.DIALOG_PARTITION_NAME_PROBLEM_TITLE);
	}

	public void setValue(String newValue, BasicSection source){
		execute(new PartitionOperationSetValue(fTarget, newValue), source, Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE);
	}
}
