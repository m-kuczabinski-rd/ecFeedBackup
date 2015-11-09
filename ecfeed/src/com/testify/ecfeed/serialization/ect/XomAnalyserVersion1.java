/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.serialization.ect;

public class XomAnalyserVersion1 extends XomAnalyser {

	protected String getChoiceNodeName() {
		return Constants.CHOICE_NODE_NAME_VERSION_1;
	}

	protected String getParameterNodeName() {
		return Constants.PARAMETER_NODE_NAME_VERSION_1;
	}
}
