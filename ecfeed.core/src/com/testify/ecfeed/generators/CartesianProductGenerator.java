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

package com.testify.ecfeed.generators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.generators.algorithms.CartesianProductAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class CartesianProductGenerator<E> extends AbstractGenerator<E> {
	@Override
	public void initialize(List<List<E>> inputDomain,
			Collection<IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException {
		
		super.initialize(inputDomain, constraints, parameters);
		setAlgorithm(new CartesianProductAlgorithm<E>());
	}
}
