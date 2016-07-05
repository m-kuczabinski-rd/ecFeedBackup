/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization.ect;

public class SerializationHelperVersion1 {

	private static final String PARAMETER_NODE_NAME_VERSION_1 = "Parameter";
	private static final String STATEMENT_PARAMETER_ATTRIBUTE_NAME_VERSION_1 = "parameter";
	private static final String CHOICE_NODE_NAME_VERSION_1 = "Choice";
	private static final String CHOICE_ATTRIBUTE_NAME_VERSION_1 = "choice";
	private static final String STATEMENT_CHOICE_ATTRIBUTE_NAME_VERSION_1 = "choice";

	public static String getChoiceNodeName() {
		return CHOICE_NODE_NAME_VERSION_1;
	}

	public static String getChoiceAttributeName() {
		return CHOICE_ATTRIBUTE_NAME_VERSION_1;
	}

	public static String getStatementChoiceAttributeName() {
		return STATEMENT_CHOICE_ATTRIBUTE_NAME_VERSION_1;
	}

	public static String getParameterNodeName() {
		return PARAMETER_NODE_NAME_VERSION_1;
	}

	public static String getStatementParameterAttributeName() {
		return STATEMENT_PARAMETER_ATTRIBUTE_NAME_VERSION_1;
	}
}
