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

import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;


public class GlobalParameterOperationSetType extends BulkOperation {

	public GlobalParameterOperationSetType(GlobalParameterNode target, String newType, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.SET_TYPE, true);

		addOperation(new AbstractParameterOperationSetType(target, newType, adapterProvider));
		for(MethodNode method : target.getMethods()){
			addOperation(new MethodOperationMakeConsistent(method));
		}
	}

}
