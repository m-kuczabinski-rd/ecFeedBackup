/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.testutils;

import static com.ecfeed.core.adapter.java.Constants.TYPE_NAME_BOOLEAN;
import static com.ecfeed.core.adapter.java.Constants.TYPE_NAME_BYTE;
import static com.ecfeed.core.adapter.java.Constants.TYPE_NAME_CHAR;
import static com.ecfeed.core.adapter.java.Constants.TYPE_NAME_DOUBLE;
import static com.ecfeed.core.adapter.java.Constants.TYPE_NAME_FLOAT;
import static com.ecfeed.core.adapter.java.Constants.TYPE_NAME_INT;
import static com.ecfeed.core.adapter.java.Constants.TYPE_NAME_LONG;
import static com.ecfeed.core.adapter.java.Constants.TYPE_NAME_SHORT;
import static com.ecfeed.core.adapter.java.Constants.TYPE_NAME_STRING;

public enum ETypeName {
	BOOLEAN(TYPE_NAME_BOOLEAN), 
	BYTE(TYPE_NAME_BYTE), 
	CHAR(TYPE_NAME_CHAR), 
	SHORT(TYPE_NAME_SHORT), 
	INT(TYPE_NAME_INT), 
	LONG(TYPE_NAME_LONG), 
	FLOAT(TYPE_NAME_FLOAT), 
	DOUBLE(TYPE_NAME_DOUBLE), 
	STRING(TYPE_NAME_STRING), 
	USER_TYPE("user.type");
	
	private String fName;

	private ETypeName(String name){
		fName = name;
	}
	
	public String getTypeName(){
		return fName;
	}
	
	public static final String[] SUPPORTED_TYPES = {
		TYPE_NAME_BOOLEAN,
		TYPE_NAME_BYTE,
		TYPE_NAME_CHAR,
		TYPE_NAME_DOUBLE,
		TYPE_NAME_FLOAT,
		TYPE_NAME_INT,
		TYPE_NAME_LONG,
		TYPE_NAME_SHORT,
		TYPE_NAME_STRING,
};

}
