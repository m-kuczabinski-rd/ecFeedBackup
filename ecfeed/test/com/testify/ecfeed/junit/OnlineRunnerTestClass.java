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

package com.testify.ecfeed.junit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.core.generators.CartesianProductGenerator;
import com.testify.ecfeed.core.generators.NWiseGenerator;
import com.testify.ecfeed.core.generators.RandomGenerator;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;
import com.testify.ecfeed.junit.annotations.GeneratorParameter;
import com.testify.ecfeed.junit.annotations.GeneratorParameterNames;
import com.testify.ecfeed.junit.annotations.GeneratorParameterValues;

@RunWith(OnlineRunner.class)
@Generator(CartesianProductGenerator.class)
@Constraints("constraint")
@EcModel("test/com/testify/ecfeed/junit/OnlineRunnerTest.ect")
public class OnlineRunnerTestClass {

	@Test
	@Generator(NWiseGenerator.class)
	@GeneratorParameter(name = "N", value = "2")
	public void nWiseTest(int a, int b, int c, int d) {
//		System.out.println("test(" + a + ", " + b + ", " + c + ", " + d + ")");
	}

	@Test
	@Generator(CartesianProductGenerator.class)
	@Constraints("other constraint")
	public void cartesianTest(int a, int b, int c, int d) {
//		System.out.println("test(" + a + ", " + b + ", " + c + ", " + d + ")");
	}

	@Test
	@Generator(RandomGenerator.class)
	@GeneratorParameterNames({"length", "duplicates"})
	@GeneratorParameterValues({"100", "false"})
	public void randomTest(int a, int b, int c, int d) {
//		System.out.println("test(" + a + ", " + b + ", " + c + ", " + d + ")");
	}


}
