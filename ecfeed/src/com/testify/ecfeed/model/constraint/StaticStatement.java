/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model.constraint;

import java.util.List;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.PartitionNode;

public class StaticStatement extends BasicStatement {

	private boolean fValue;
	
	public StaticStatement(boolean value){
		fValue = value;
	}

	public boolean getValue(){
		return fValue;
	}
	
	@Override
	public boolean evaluate(List<PartitionNode> values) {
		return fValue;
	}

	@Override
	public String toString(){
		return fValue?Constants.STATIC_STATEMENT_TRUE_VALUE:Constants.STATIC_STATEMENT_FALSE_VALUE;
	}
	
	public void setValue(boolean value) {
		fValue = value;
	}
}
