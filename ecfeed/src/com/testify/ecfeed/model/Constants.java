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

	public static final String RELATION_EQUAL = "=";
	public static final String RELATION_NOT = "\u2260";

	public static final String REGEX_JAVA_IDENTIFIER = "[A-Za-z][A-Za-z0-9_]{1,64}";
	public static final String REGEX_ALPHANUMERIC_WITH_SPACES = "[A-Za-z][A-Za-z0-9_]{1,64}";
	public static final String REGEX_ROOT_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES;
	public static final String REGEX_CLASS_NODE_NAME = "([A-Za-z]{1}[A-Za-z0-9]{1,16}){1}(\\.[A-Za-z_]{1}[A-Za-z0-9_]{1,16})*";
	public static final String REGEX_METHOD_NODE_NAME = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_CATEGORY_NODE_NAME = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_CATEGORY_TYPE_NAME = REGEX_CLASS_NODE_NAME;
	public static final String REGEX_CONSTRAINT_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES;
	public static final String REGEX_PARTITION_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES;
	public static final String REGEX_PARTITION_LABEL = REGEX_ALPHANUMERIC_WITH_SPACES;
	
	public static final String REGEX_USER_TYPE_VALUE = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_STRING_TYPE_VALUE = "[A-Za-z1-9 !@#$%^&*()_+=;':,.<>/?]{0,1024}";
	public static final String REGEX_CHAR_TYPE_VALUE = "[A-Za-z1-9 !@#$%^&*()_+=;':,.<>/?]";
	
}
