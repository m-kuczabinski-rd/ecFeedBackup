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

public class Constants {

	public static final int MAX_NODE_NAME_LENGTH = 64;
	
	public static final String TYPE_NAME_BOOLEAN = "boolean";
	public static final String TYPE_NAME_BYTE = "byte";
	public static final String TYPE_NAME_CHAR = "char";
	public static final String TYPE_NAME_DOUBLE = "double";
	public static final String TYPE_NAME_FLOAT = "float";
	public static final String TYPE_NAME_INT = "int";
	public static final String TYPE_NAME_LONG = "long";
	public static final String TYPE_NAME_SHORT = "short";
	public static final String TYPE_NAME_STRING = "String";
	public static final String TYPE_NAME_UNSUPPORTED = "unsupported";

	public static final String NULL_VALUE_STRING_REPRESENTATION = "/null";
	public static final String RELATION_EQUAL = "=";
	public static final String RELATION_NOT = "\u2260";

	public static final String ROOT_NODE_NAME_REGEX = "[A-Za-z][A-Za-z0-9 ]{1,64}";
	public static final String CLASS_NODE_NAME_REGEX = "([A-Za-z]{1}[A-Za-z0-9]{1,16}){1}(\\.[A-Za-z]{1}[A-Za-z0-9]{1,16})*";
	
}
