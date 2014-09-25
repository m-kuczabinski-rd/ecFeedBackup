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

import java.util.List;

public class StaticStatement extends BasicStatement {

	public static final String STATIC_STATEMENT_TRUE_VALUE = "true";
	public static final String STATIC_STATEMENT_FALSE_VALUE = "false";
	
	private boolean fValue;
	
	public StaticStatement(boolean value){
		fValue = value;
	}

	public String getLeftOperandName(){
		return fValue?STATIC_STATEMENT_TRUE_VALUE:STATIC_STATEMENT_FALSE_VALUE;
	}
	
	public boolean getValue(){
		return fValue;
	}
	
	public void setValue(boolean value) {
		fValue = value;
	}

	@Override
	public boolean evaluate(List<PartitionNode> values) {
		return fValue;
	}

	@Override
	public String toString(){
		return fValue?STATIC_STATEMENT_TRUE_VALUE:STATIC_STATEMENT_FALSE_VALUE;
	}
	
	@Override
	public StaticStatement getCopy(){
		return new StaticStatement(fValue);
	}
	
	@Override
	public boolean updateReferences(MethodNode method){
		return true;
	}
	
	@Override
	public boolean compare(IStatement statement){
		if(statement instanceof StaticStatement == false){
			return false;
		}
		StaticStatement compared = (StaticStatement)statement;
		return getValue() == compared.getValue();
	}
	
	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

}
