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

package com.ecfeed.core.model;

public enum EStatementOperator{
	AND("AND"), 
	OR("OR");
	
	public static final String OPERATOR_AND = "AND";
	public static final String OPERATOR_OR = "OR";

	String fValue;

	private EStatementOperator(String value){
		fValue = value;
	}
	
	public String toString(){
		return fValue; 
	}
	
	public static EStatementOperator getOperator(String text){
		switch(text){
		case OPERATOR_AND:
			return AND;
		case OPERATOR_OR:
			return OR;
		}
		return null;
	}

}
