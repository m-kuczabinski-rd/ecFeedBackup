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

import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.ModelOperationException;
import com.testify.ecfeed.core.adapter.java.Messages;

public class MethodParameterShiftOperation extends GenericShiftOperation {

	private List<AbstractParameterNode> fParameters;

	public MethodParameterShiftOperation(List<AbstractParameterNode> parameters, AbstractNode shifted, boolean up) {
		this(parameters, Arrays.asList(new AbstractNode[]{shifted}), up);
	}

	public MethodParameterShiftOperation(List<AbstractParameterNode> parameters, List<? extends AbstractNode> shifted, boolean up) {
		this(parameters, shifted, 0);
		setShift(minAllowedShift(shifted, up));
	}

	public MethodParameterShiftOperation(List<AbstractParameterNode> parameters, List<? extends AbstractNode> shifted, int shift) {
		super(parameters, shifted, shift);
		fParameters = parameters;
	}

	@Override
	public void execute() throws ModelOperationException {
		MethodNode method = ((MethodParameterNode)fParameters.get(0)).getMethod();
		if(shiftAllowed(getShiftedElements(), getShift()) == false){
			ModelOperationException.report(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
		}
		List<Integer> indices = indices(fParameters, getShiftedElements());
		shiftElements(fParameters, indices, getShift());
		for(TestCaseNode testCase : method.getTestCases()){
			shiftElements(testCase.getTestData(), indices, getShift());
		}
	}

	@Override
	public IModelOperation reverseOperation(){
		return new MethodParameterShiftOperation(fParameters, getShiftedElements(), -getShift());
	}

	@Override
	protected boolean shiftAllowed(List<? extends AbstractNode> shifted, int shift){
		if(super.shiftAllowed(shifted, shift) == false) return false;
		if(shifted.get(0) instanceof MethodParameterNode == false) return false;
		MethodNode method = ((MethodParameterNode)shifted.get(0)).getMethod();
		List<String> parameterTypes = method.getParametersTypes();
		List<Integer> indices = indices(method.getParameters(), shifted);
		shiftElements(parameterTypes, indices, shift);
		MethodNode sibling = method.getClassNode().getMethod(method.getName(), parameterTypes);
		if(sibling != null && sibling != method){
			return false;
		}
		return true;
	}

	@Override
	protected int minAllowedShift(List<? extends AbstractNode> shifted, boolean up){
		int shift = up ? -1 : 1;
		while(shiftAllowed(shifted, shift) == false){
			shift += up ? -1 : 1;
			int borderIndex = (borderNode(shifted, shift) != null) ? borderNode(shifted, shift).getIndex() + shift : -1;
			if(borderIndex < 0 || borderIndex >= borderNode(shifted, shift).getMaxIndex()){
				return 0;
			}
		}
		return shift;
	}

}
