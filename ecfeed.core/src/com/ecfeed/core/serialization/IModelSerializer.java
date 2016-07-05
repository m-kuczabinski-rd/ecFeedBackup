/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;

public interface IModelSerializer {
	public Object serialize(RootNode node) throws Exception;
	public Object serialize(ClassNode node) throws Exception;
	public Object serialize(MethodNode node) throws Exception;
	public Object serialize(MethodParameterNode node) throws Exception;
	public Object serialize(GlobalParameterNode node) throws Exception;
	public Object serialize(TestCaseNode node) throws Exception;
	public Object serialize(ConstraintNode node) throws Exception;
	public Object serialize(ChoiceNode node) throws Exception;
}
