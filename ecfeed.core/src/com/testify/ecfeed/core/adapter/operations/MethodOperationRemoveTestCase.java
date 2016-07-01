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

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

public class MethodOperationRemoveTestCase extends AbstractModelOperation {

	private MethodNode fTarget;
	private TestCaseNode fTestCase;
	private int fIndex;

	private class DummyAdapterProvider implements ITypeAdapterProvider{

		@Override
		public ITypeAdapter getAdapter(String type) {
			return new ITypeAdapter() {
				@Override
				public boolean isNullAllowed() {
					return false;
				}
				@Override
				public String defaultValue() {
					return null;
				}
				@Override
				public String convert(String value) {
					return value;
				}
				@Override
				public boolean compatible(String type) {
					return true;
				}
			};
		}

	}

	public MethodOperationRemoveTestCase(MethodNode target, TestCaseNode testCase) {
		super(OperationNames.REMOVE_TEST_CASE);
		fTarget = target;
		fTestCase = testCase;
		fIndex = testCase.getIndex();
	}

	@Override
	public void execute() throws ModelOperationException {
		fIndex = fTestCase.getIndex();
		fTarget.removeTestCase(fTestCase);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationAddTestCase(fTarget, fTestCase, new DummyAdapterProvider(), fIndex);
	}

}
