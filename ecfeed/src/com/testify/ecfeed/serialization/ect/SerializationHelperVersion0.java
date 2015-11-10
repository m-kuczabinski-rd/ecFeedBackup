/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.serialization.ect;

public class SerializationHelperVersion0 {

	private static final String CHOICE_NODE_NAME_VERSION_0 = "Partition";
	private static final String CHOICE_ATTRIBUTE_NAME_VERSION_0 = "partition";
	private static final String STATEMENT_CHOICE_ATTRIBUTE_NAME_VERSION_0 = "partition";
	private static final String PARAMETER_NODE_NAME_VERSION_0 = "Category";
	private static final String STATEMENT_PARAMETER_ATTRIBUTE_NAME_VERSION_0 = "category";

	public static String getChoiceNodeName() {
		return CHOICE_NODE_NAME_VERSION_0;
	}

	public static String getChoiceAttributeName() {
		return CHOICE_ATTRIBUTE_NAME_VERSION_0;
	}

	public static String getStatementChoiceAttributeName() {
		return STATEMENT_CHOICE_ATTRIBUTE_NAME_VERSION_0;
	}

	public static String getParameterNodeName() {
		return PARAMETER_NODE_NAME_VERSION_0;
	}

	public static String getStatementParameterAttributeName() {
		return STATEMENT_PARAMETER_ATTRIBUTE_NAME_VERSION_0;
	}
}
