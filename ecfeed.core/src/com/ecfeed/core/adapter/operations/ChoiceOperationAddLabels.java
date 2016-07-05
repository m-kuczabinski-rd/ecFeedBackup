/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import java.util.Collection;

import com.ecfeed.core.model.ChoiceNode;

public class ChoiceOperationAddLabels extends BulkOperation {
	public ChoiceOperationAddLabels(ChoiceNode target, Collection<String> labels) {
		super(OperationNames.ADD_PARTITION_LABELS, false);
		for(String label : labels){
			if(target.getInheritedLabels().contains(label) == false){
				addOperation(new ChoiceOperationAddLabel(target, label));
			}
		}
	}
}
