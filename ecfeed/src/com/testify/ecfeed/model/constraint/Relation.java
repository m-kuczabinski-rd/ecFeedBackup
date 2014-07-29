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

public enum Relation{
	
	EQUAL("="), 
	NOT("\u2260");
	
	String fValue;

	public static final String RELATION_EQUAL = "=";
	public static final String RELATION_NOT = "\u2260";


	private Relation(String value){
		fValue = value;
	}
	
	public String toString(){
		return fValue; 
	}
	
	public static Relation getRelation(String text){
		switch(text){
		case RELATION_EQUAL:
			return EQUAL;
		case RELATION_NOT:
			return NOT;
		}
		return NOT;
	}
}
