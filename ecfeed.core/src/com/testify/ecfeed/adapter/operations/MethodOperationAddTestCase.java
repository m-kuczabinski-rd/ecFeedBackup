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

package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapter;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.Constants;
import com.testify.ecfeed.adapter.java.Messages;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationAddTestCase extends AbstractModelOperation {

	private MethodNode fTarget;
	private TestCaseNode fTestCase;
	private int fIndex;
	private ITypeAdapterProvider fAdapterProvider;

	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase, ITypeAdapterProvider adapterProvider, int index) {
		super(OperationNames.ADD_TEST_CASE);
		fTarget = target;
		fTestCase = testCase;
		fIndex = index;
		fAdapterProvider = adapterProvider;
	}

	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase, ITypeAdapterProvider adapterProvider) {
		this(target, testCase, adapterProvider, -1);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fIndex == -1){
			fIndex = fTarget.getTestCases().size();
		}
		if(fTestCase.getName().matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			ModelOperationException.report(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		if(fTestCase.updateReferences(fTarget) == false){
			ModelOperationException.report(Messages.TEST_CASE_INCOMPATIBLE_WITH_METHOD);
		}
		//following must be done AFTER references are updated
		fTestCase.setParent(fTarget);
		for(ChoiceNode choice : fTestCase.getTestData()){
			MethodParameterNode parameter = fTestCase.getMethodParameter(choice);
			if(parameter.isExpected()){
				String type = parameter.getType();
				ITypeAdapter adapter = fAdapterProvider.getAdapter(type);
				String newValue = adapter.convert(choice.getValueString());
				if(newValue == null){
					ModelOperationException.report(Messages.TEST_CASE_DATA_INCOMPATIBLE_WITH_METHOD);
				}
				choice.setValueString(newValue);
			}
		}

		fTarget.addTestCase(fTestCase, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRemoveTestCase(fTarget, fTestCase);
	}

}
