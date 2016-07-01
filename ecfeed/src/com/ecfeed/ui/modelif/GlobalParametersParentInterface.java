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

import java.util.Collection;
import java.util.List;

import com.ecfeed.core.adapter.operations.ReplaceMethodParametersWithGlobalOperation;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.ui.common.EclipseModelBuilder;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IFileInfoProvider;

public class GlobalParametersParentInterface extends ParametersParentInterface {

	public GlobalParametersParentInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext, fileInfoProvider);
	}

	@Override
	public AbstractParameterNode addNewParameter() {
		EclipseModelBuilder modelBuilder = new EclipseModelBuilder();
		GlobalParameterNode parameter = new GlobalParameterNode(generateNewParameterName(), generateNewParameterType());
		parameter.addChoices(modelBuilder.defaultChoices(parameter.getType()));
		if(addParameter(parameter, getTarget().getParameters().size())){
			return parameter;
		}
		return null;
	}

	public boolean removeGlobalParameters(Collection<GlobalParameterNode> parameters){
		return super.removeParameters(parameters);
	}

	@Override
	protected GlobalParametersParentNode getTarget(){
		return (GlobalParametersParentNode)super.getTarget();
	}

	public boolean replaceMethodParametersWithGlobal(List<MethodParameterNode> originalParameters) {
		return execute(new ReplaceMethodParametersWithGlobalOperation(getTarget(), originalParameters, getAdapterProvider()), Messages.DIALOG_REPLACE_PARAMETERS_WITH_LINKS_TITLE);
	}
}
