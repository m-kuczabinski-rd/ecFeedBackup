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

package com.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Arrays;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.TestCaseOperationUpdateTestData;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IFileInfoProvider;

public class TestCaseInterface extends AbstractNodeInterface {

	private IFileInfoProvider fFileInfoProvider;

	public TestCaseInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	protected TestCaseNode getTarget() {
		return (TestCaseNode)super.getTarget();
	}

	public boolean isExpected(ChoiceNode testValue) {
		return getTarget().getMethodParameter(testValue).isExpected();
	}

	public boolean isExecutable(TestCaseNode tc){
		MethodInterface mIf = new MethodInterface(getUpdateContext(), fFileInfoProvider);
		if(tc.getMethod() == null) return false;
		mIf.setTarget(tc.getMethod());
		EImplementationStatus tcStatus = getImplementationStatus(tc);
		EImplementationStatus methodStatus = mIf.getImplementationStatus();
		return tcStatus == EImplementationStatus.IMPLEMENTED && methodStatus != EImplementationStatus.NOT_IMPLEMENTED;
	}

	public boolean isExecutable(){
		return isExecutable(getTarget());
	}

	public void executeStaticTest() throws EcException {
		MethodInterface methodIf = new MethodInterface(getUpdateContext(), fFileInfoProvider);

		TestCaseNode testCaseNode = getTarget();
		MethodNode methodNode = (MethodNode)testCaseNode.getParent();
		methodIf.setTarget(methodNode);

		methodIf.executeStaticTests(
				new ArrayList<TestCaseNode>(Arrays.asList(new TestCaseNode[]{getTarget()})), fFileInfoProvider);
	}

	public boolean updateTestData(int index, ChoiceNode value) {
		IModelOperation operation = new TestCaseOperationUpdateTestData(getTarget(), index, value);
		return execute(operation, Messages.DIALOG_UPDATE_TEST_DATA_PROBLEM_TITLE);
	}

	@Override
	public boolean goToImplementationEnabled(){
		return false;
	}
}
