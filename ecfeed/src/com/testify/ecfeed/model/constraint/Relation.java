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

import com.testify.ecfeed.model.Constants;

public enum Relation{
	EQUAL(Constants.RELATION_EQUAL), 
	NOT(Constants.RELATION_NOT);
	
	String fValue;
	
	private Relation(String value){
		fValue = value;
	}
	
	public String toString(){
		return fValue; 
	}
	
	public static Relation getRelation(String text){
		switch(text){
		case Constants.RELATION_EQUAL:
			return EQUAL;
		case Constants.RELATION_NOT:
			return NOT;
		}
		return NOT;
	}
}
