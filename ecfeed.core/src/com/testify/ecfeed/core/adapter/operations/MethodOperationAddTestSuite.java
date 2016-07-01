/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.adapter.operations;

import java.util.List;

import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

public class MethodOperationAddTestSuite extends BulkOperation {

	public MethodOperationAddTestSuite(MethodNode target, String testSuiteName, List<List<ChoiceNode>> testData, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.ADD_TEST_CASES, false);
		for(List<ChoiceNode> values : testData){
			addOperation(new MethodOperationAddTestCase(target, new TestCaseNode(testSuiteName, values), adapterProvider));
		}
	}

}
