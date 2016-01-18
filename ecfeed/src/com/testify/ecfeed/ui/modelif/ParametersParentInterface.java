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

package com.testify.ecfeed.ui.modelif;

import java.util.Collection;

import com.testify.ecfeed.core.adapter.java.JavaUtils;
import com.testify.ecfeed.core.adapter.operations.GenericOperationAddParameter;
import com.testify.ecfeed.core.model.AbstractParameterNode;
import com.testify.ecfeed.core.model.ParametersParentNode;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;

public abstract class ParametersParentInterface extends AbstractNodeInterface {

	public ParametersParentInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext, fileInfoProvider);
	}

	public abstract AbstractParameterNode addNewParameter();

	public boolean addParameter(AbstractParameterNode parameter, int index) {
		return execute(new GenericOperationAddParameter(getTarget(), parameter, index), Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}

	protected boolean removeParameters(Collection<? extends AbstractParameterNode> parameters){
		return super.removeChildren(parameters, Messages.DIALOG_REMOVE_PARAMETERS_PROBLEM_TITLE);
	}

	protected String generateNewParameterType() {
		return JavaUtils.supportedPrimitiveTypes()[0];
	}

	protected String generateNewParameterName() {
		int i = 0;
		String name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		while(getTarget().getParameter(name) != null){
			name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		}
		return name;
	}

	@Override
	protected ParametersParentNode getTarget(){
		return (ParametersParentNode)super.getTarget();
	}
}
