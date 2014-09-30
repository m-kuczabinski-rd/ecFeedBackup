/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Mariusz Strozynski (m.strozynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.implementor;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public interface IModelImplementor {
	public void implement(ClassNode node);
	public void implement(MethodNode node);
	public void implement(CategoryNode node);
	public void implement(PartitionNode node);
};
