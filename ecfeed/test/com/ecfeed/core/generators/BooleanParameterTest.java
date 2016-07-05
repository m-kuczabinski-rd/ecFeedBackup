/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ecfeed.core.generators.BooleanParameter;

public class BooleanParameterTest {

	@Test
	public void testTest() {
		BooleanParameter parameter = new BooleanParameter("parameter", true, false);
		assertTrue(parameter.test(true));
		assertTrue(parameter.test(false));
		assertFalse(parameter.test(8));
	}

}
