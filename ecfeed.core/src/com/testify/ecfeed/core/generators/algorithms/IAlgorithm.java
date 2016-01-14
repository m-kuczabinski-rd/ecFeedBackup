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

package com.testify.ecfeed.core.generators.algorithms;

import java.util.Collection;
import java.util.List;

import com.testify.ecfeed.core.generators.api.GeneratorException;
import com.testify.ecfeed.core.generators.api.IConstraint;

public interface IAlgorithm<E> {
	public void initialize(List<List<E>> input, 
			Collection<IConstraint<E>> constraints) throws GeneratorException;
	public List<E> getNext() throws GeneratorException;
	public void reset();
	public void addConstraint(IConstraint<E> constraint);
	public void removeConstraint(IConstraint<E> constraint);
	public Collection<? extends IConstraint<E>> getConstraints();
	public int totalWork();
	public int totalProgress();
	public int workProgress();
	public void cancel();
}
