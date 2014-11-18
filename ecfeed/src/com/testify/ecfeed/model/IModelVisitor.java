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

public interface IModelVisitor {
	public Object visit(RootNode node) throws Exception;
	public Object visit(ClassNode node) throws Exception;
	public Object visit(MethodNode node) throws Exception;
	public Object visit(ParameterNode node) throws Exception;
	public Object visit(TestCaseNode node) throws Exception;
	public Object visit(ConstraintNode node) throws Exception;
	public Object visit(PartitionNode node) throws Exception;
}
