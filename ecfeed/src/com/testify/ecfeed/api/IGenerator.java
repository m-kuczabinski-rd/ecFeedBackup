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

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IGenerator<E> {
	/*
	 * returns list of parameters used by this generator.
	 */
	public List<IGeneratorParameter> parameters();
	/*
	 * Should be called prior to first call of getNext()
	 */
	public void initialize(List<? extends List<E>> inputDomain, 
			Collection<? extends IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException;
	
	/*
	 * Returns null if no more data can be generated, e.g.if the test generation should end 
	 * all data according to the used algorithm or provided parameter has been generated. 
	 * Blocking method, implementation should make be cancelable using progressMonitor.
	 */
	public List<E> next() throws GeneratorException;
	
	/*
	 * Resets generator to its initial state.
	 */
	public void reset();
	
	public int totalWork();
	
	public int workProgress();
	
	public int totalProgress();
}
