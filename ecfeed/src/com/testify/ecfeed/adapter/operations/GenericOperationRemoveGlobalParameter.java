/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.GlobalParametersParentNode;
import com.testify.ecfeed.model.MethodParameterNode;

public class GenericOperationRemoveGlobalParameter extends BulkOperation {

	public GenericOperationRemoveGlobalParameter(GlobalParametersParentNode target, GlobalParameterNode parameter) {
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true);
		for(MethodParameterNode linker : parameter.getLinkers()){
			addOperation(new MethodOperationRemoveParameter(linker.getMethod(), linker));
		}
		addOperation(new GenericOperationRemoveParameter(target, parameter));
	}

}
