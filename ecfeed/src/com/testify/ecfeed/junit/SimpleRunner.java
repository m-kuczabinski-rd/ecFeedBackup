/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

// experimental use only
public class SimpleRunner extends Runner {
	
	Description fDescription;
	
	public SimpleRunner(Class<?> testClass) {
		System.out.println("Simple runner - constructor: " + testClass.getName());
		fDescription = Description.createTestDescription(testClass, testClass.getName()); 
	}

	@Override
	public Description getDescription() {
		return fDescription;
	}

	@Override
	public void run(RunNotifier runNotifier) {
		System.out.println("Simple runner - run");
		runNotifier.fireTestStarted(fDescription);
		runNotifier.fireTestFinished(fDescription);
	}

	@Override
	public int testCount() {
		return 1;
	}
}
