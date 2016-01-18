/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.serialization;

import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.model.ConstraintNode;
import com.testify.ecfeed.core.model.GlobalParameterNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.RootNode;
import com.testify.ecfeed.core.model.TestCaseNode;

public interface IModelSerializer {
	public Object serialize(RootNode node) throws Exception;
	public Object serialize(ClassNode node) throws Exception;
	public Object serialize(MethodNode node) throws Exception;
	public Object serialize(MethodParameterNode node) throws Exception;
	public Object serialize(GlobalParameterNode node) throws Exception;
	public Object serialize(TestCaseNode node) throws Exception;
	public Object serialize(ConstraintNode node) throws Exception;
	public Object serialize(ChoiceNode node) throws Exception;
}
