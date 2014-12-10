package com.testify.ecfeed.adapter.operations;

import java.util.Arrays;
import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodParameterShiftOperation extends GenericShiftOperation {

	private List<MethodParameterNode> fParameters;

	public MethodParameterShiftOperation(List<MethodParameterNode> parameters, AbstractNode shifted, boolean up) {
		this(parameters, Arrays.asList(new AbstractNode[]{shifted}), up);
	}

	public MethodParameterShiftOperation(List<MethodParameterNode> parameters, List<? extends AbstractNode> shifted, boolean up) {
		this(parameters, shifted, 0);
		setShift(minAllowedShift(shifted, up));
	}

	public MethodParameterShiftOperation(List<MethodParameterNode> parameters, List<? extends AbstractNode> shifted, int shift) {
		super(parameters, shifted, shift);
		fParameters = parameters;
	}

	@Override
	public void execute() throws ModelOperationException {
		MethodNode method = fParameters.get(0).getMethod();
		if(shiftAllowed(getShiftedElements(), getShift()) == false){
			throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
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
