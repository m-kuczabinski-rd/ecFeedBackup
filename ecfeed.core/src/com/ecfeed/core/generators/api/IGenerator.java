/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IGenerator<E> {
	/*
	 * returns list of parameters used by this generator.
	 */
	public List<IGeneratorParameter> parameters();
	/*
	 * Should be called prior to first call of next()
	 */
	public void initialize(List<List<E>> inputDomain, 
			Collection<IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException;
	
	public void addConstraint(IConstraint<E> constraint);

	public void removeConstraint(IConstraint<E> constraint);
	
	public Collection<? extends IConstraint<E>> getConstraints();
	
	/*
	 * Returns null if no more data can be generated, e.g.if the test generation should end 
	 * all data according to the used algorithm or provided parameter has been generated. 
	 * Blocking method.
	 */
	public List<E> next() throws GeneratorException;
	
	/*
	 * Resets generator to its initial state.
	 */
	public void reset();
	
	public int totalWork();
	
	public int workProgress();
	
	public int totalProgress();
	
	public void cancel();
	
}
