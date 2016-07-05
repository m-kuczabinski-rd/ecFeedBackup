/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter;

import com.ecfeed.core.model.AbstractNode;

public interface IModelImplementer {
	public boolean implementable(Class<? extends AbstractNode> type);
	public boolean implementable(AbstractNode node);
	public boolean implement(AbstractNode node) throws Exception;
	public EImplementationStatus getImplementationStatus(AbstractNode node);
}
