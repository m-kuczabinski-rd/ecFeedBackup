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

package com.testify.ecfeed.model;

import com.testify.ecfeed.model.PartitionedCategoryStatement.LabelCondition;
import com.testify.ecfeed.model.PartitionedCategoryStatement.PartitionCondition;

public interface IStatementVisitor {
	public Object visit(StaticStatement statement) throws Exception;
	public Object visit(StatementArray statement) throws Exception;
	public Object visit(ExpectedValueStatement statement) throws Exception;
	public Object visit(PartitionedCategoryStatement statement) throws Exception;
	public Object visit(LabelCondition condition) throws Exception;
	public Object visit(PartitionCondition condition) throws Exception;
}
