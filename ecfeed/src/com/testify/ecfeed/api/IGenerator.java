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

import org.eclipse.core.runtime.IProgressMonitor;

public interface IGenerator<E> {
	/*
	 * returns list of parameters used by this generator.
	 */
	
	public List<IGeneratorParameter> requiredParameters();
	/*
	 * returns list of parameters used by this generator. The returned parameter list should not 
	 * differ from the same function without parameters, but might be more detailed regarding 
	 * allowed set of values for parameter. 
	 */
	public List<IGeneratorParameter> requiredParameters(List<List<E>> inputDomain);
	
	/*
	 * Sets value of generator's parameter.
	 */
	public void setParameter(String name, Object value) throws GeneratorException;

	/*
	 * Should be called prior to first call of getNext()
	 */
	public void initialize(List<List<E>> inputDomain, 
			Collection<IConstraint<E>> constraints,
			IProgressMonitor progressMonitor) throws GeneratorException;
	
	/*
	 * Returns null if no more data can be generated, e.g. due to lack of initialization or 
	 *  if the test generation should end according to the used algorithm or provided
	 *  parameter. Blocking method, implementation should make be cancelable using progressMonitor.
	 */
	public List<E> getNext() throws GeneratorException;
}
