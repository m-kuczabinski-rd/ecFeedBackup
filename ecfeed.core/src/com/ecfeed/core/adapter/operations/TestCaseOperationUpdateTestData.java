/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.TestCaseNode;

public class TestCaseOperationUpdateTestData extends AbstractModelOperation {
	
	private ChoiceNode fNewValue;
	private ChoiceNode fPreviousValue;
	private int fIndex;
	private TestCaseNode fTarget;

	public TestCaseOperationUpdateTestData(TestCaseNode target, int index, ChoiceNode value) {
		super(OperationNames.UPDATE_TEST_DATA);
		fTarget = target;
		fIndex = index;
		fNewValue = value;
		fPreviousValue = target.getTestData().get(index);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fNewValue.getParameter() != fTarget.getTestData().get(fIndex).getParameter()){
			ModelOperationException.report(Messages.TEST_DATA_CATEGORY_MISMATCH_PROBLEM);
		}
		fTarget.getTestData().set(fIndex, fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new TestCaseOperationUpdateTestData(fTarget, fIndex, fPreviousValue);
	}

}
