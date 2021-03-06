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

public class XomBuilderVersion0 extends XomBuilder {

	@Override
	protected String getChoiceNodeName() {
		return SerializationHelperVersion0.getChoiceNodeName();
	}

	@Override	
	protected String getChoiceAttributeName() {
		return SerializationHelperVersion0.getChoiceAttributeName();
	}

	@Override
	protected String getStatementChoiceAttributeName() {
		return SerializationHelperVersion0.getStatementChoiceAttributeName();
	}

	@Override
	protected String getParameterNodeName() {
		return SerializationHelperVersion0.getParameterNodeName();
	}

	@Override
	protected String getStatementParameterAttributeName() {
		return SerializationHelperVersion0.getStatementParameterAttributeName();
	}
}
