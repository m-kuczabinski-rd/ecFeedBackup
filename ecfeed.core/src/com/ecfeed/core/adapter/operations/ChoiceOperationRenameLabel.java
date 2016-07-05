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

import com.ecfeed.core.model.ChoiceNode;

public class ChoiceOperationRenameLabel extends BulkOperation {

	public ChoiceOperationRenameLabel(ChoiceNode target, String currentLabel, String newLabel) {
		super(OperationNames.RENAME_LABEL, true);
		addOperation(new ChoiceOperationRemoveLabel(target, currentLabel));
		addOperation(new ChoiceOperationAddLabel(target, newLabel));
	}
}
