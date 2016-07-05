/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.List;

public interface IStatement{
	public boolean evaluate(List<ChoiceNode> values);
	public boolean adapt(List<ChoiceNode> values);
	public boolean compare(IStatement statement);
	public Object accept(IStatementVisitor visitor) throws Exception;
}
