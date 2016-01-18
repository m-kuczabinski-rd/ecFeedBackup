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

package com.testify.ecfeed.core.adapter.operations;

import java.util.Collection;

import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.model.AbstractNode;

public class GenericAddChildrenOperation extends BulkOperation {

	public GenericAddChildrenOperation(AbstractNode target, Collection<? extends AbstractNode> children, ITypeAdapterProvider adapterProvider, boolean validate) {
		this(target, children, -1, adapterProvider, validate);
	}

	public GenericAddChildrenOperation(AbstractNode target, Collection<? extends AbstractNode> children, int index, ITypeAdapterProvider adapterProvider, boolean validate) {
		super(OperationNames.ADD_CHILDREN, false);
		for(AbstractNode child : children){
			IModelOperation operation;
			try {
				if(index != -1){
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child, index++, adapterProvider, validate));
				}
				else{
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child, adapterProvider, validate));
				}
				if(operation != null){
					addOperation(operation);
				}
			} catch (Exception e) {SystemLogger.logCatch(e.getMessage());}
		}
	}

	public boolean enabled(){
		return operations().isEmpty() == false;
	}
}
