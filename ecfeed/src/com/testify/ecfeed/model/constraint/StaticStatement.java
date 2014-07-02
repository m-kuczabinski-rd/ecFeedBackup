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

package com.testify.ecfeed.model.constraint;

import java.util.List;

import com.testify.ecfeed.model.PartitionNode;

public class StaticStatement extends BasicStatement {

	public static final String STATIC_STATEMENT_TRUE_VALUE = "true";
	public static final String STATIC_STATEMENT_FALSE_VALUE = "false";
	
	private boolean fValue;
	
	public StaticStatement(boolean value){
		fValue = value;
	}

	public String getLeftHandName(){
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
}
