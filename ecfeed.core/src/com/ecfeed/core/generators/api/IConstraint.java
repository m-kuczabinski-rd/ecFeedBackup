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

package com.ecfeed.core.generators.api;

import java.util.List;


/*
 * Constraints can be adapting or evaluating. The adapting constraints
 * should change the content of 'values' vector, so the constraint is 
 * fulfilled after adaption. The evaluate function should always return 
 * true in this case.
 * 
 * The evaluating constraints should not modify the values (adapt function 
 * should do nothing). The evaluate function shall return information
 * if the vector fulfills the constraint condition.
 */
public interface IConstraint<E> {
	public boolean evaluate(List<E> values);
	public boolean adapt(List<E> values);
}
