/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.api;

import java.util.List;

public interface IGenerator<E> {
	public void initialize(List<List<E>> inputDomain);
	public String[] getAlgorithms();
	public IAlgorithm<E> getAlgorithm(String name);
}
