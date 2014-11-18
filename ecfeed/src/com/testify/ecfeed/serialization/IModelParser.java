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

package com.testify.ecfeed.serialization;

import java.io.InputStream;

import com.testify.ecfeed.model.BasicStatement;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.DecomposedParameterStatement;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.TestCaseNode;

public interface IModelParser {
	public RootNode parseModel(InputStream istream) throws ParserException;
	public ClassNode parseClass(InputStream istream) throws ParserException;
	public MethodNode parseMethod(InputStream istream) throws ParserException;
	public ParameterNode parseParameter(InputStream istream) throws ParserException;
	public ChoiceNode parsePartition(InputStream istream) throws ParserException;
	public TestCaseNode parseTestCase(InputStream istream, MethodNode method) throws ParserException;
	public ConstraintNode parseConstraint(InputStream istream, MethodNode method) throws ParserException;
	public BasicStatement parseStatement(InputStream istream, MethodNode method) throws ParserException;
	public StaticStatement parseStaticStatement(InputStream istream) throws ParserException;
	public DecomposedParameterStatement parsePartitionedParameterStatement(InputStream istream, MethodNode method) throws ParserException;
	public ExpectedValueStatement parseExpectedValueStatement(InputStream istream, MethodNode method) throws ParserException;
	public StatementArray parseStatementArray(InputStream istream, MethodNode method) throws ParserException;
}
