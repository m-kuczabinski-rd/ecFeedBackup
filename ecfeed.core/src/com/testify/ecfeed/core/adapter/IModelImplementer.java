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

package com.testify.ecfeed.core.adapter;

import com.testify.ecfeed.core.model.AbstractNode;

public interface IModelImplementer {
	public boolean implementable(Class<? extends AbstractNode> type);
	public boolean implementable(AbstractNode node);
	public boolean implement(AbstractNode node) throws Exception;
	public EImplementationStatus getImplementationStatus(AbstractNode node);
}
