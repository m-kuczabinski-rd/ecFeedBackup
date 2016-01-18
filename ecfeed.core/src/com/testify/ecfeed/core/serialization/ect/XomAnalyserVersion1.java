/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.core.serialization.ect;

public class XomAnalyserVersion1 extends XomAnalyser {

	@Override
	protected int getModelVersion() {
		return 1;
	}

	@Override
	protected String getChoiceNodeName() {
		return SerializationHelperVersion1.getChoiceNodeName();
	}

	@Override	
	protected String getChoiceAttributeName() {
		return SerializationHelperVersion1.getChoiceAttributeName();
	}

	@Override
	protected String getStatementChoiceAttributeName() {
		return SerializationHelperVersion1.getStatementChoiceAttributeName();
	}

	@Override
	protected String getParameterNodeName() {
		return SerializationHelperVersion1.getParameterNodeName();
	}

	@Override
	protected String getStatementParameterAttributeName() {
		return SerializationHelperVersion1.getStatementParameterAttributeName();
	}
}
