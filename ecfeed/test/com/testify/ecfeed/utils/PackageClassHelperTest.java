/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.testify.ecfeed.core.utils.PackageClassHelper;

public class PackageClassHelperTest{

	private static final String PACKAGE = "com.testify.ecfeed.utils";
	private static final String CLASS = "Class";
	private static final String PACKAGE_WITH_CLASS = PACKAGE + PackageClassHelper.PACKAGE_CLASS_SEPARATOR + CLASS;

	@Test
	public void shouldGetPackage(){
		String result = PackageClassHelper.getPackage(PACKAGE_WITH_CLASS);
		assertEquals(PACKAGE, result);
	}

	@Test
	public void shouldGetClass(){
		String result = PackageClassHelper.getClass(PACKAGE_WITH_CLASS);
		assertEquals(CLASS, result);
	}	

	@Test
	public void shouldCreatePackageWithClass(){
		String result = PackageClassHelper.createPackageWithClass(PACKAGE, CLASS);
		assertEquals(PACKAGE_WITH_CLASS, result);
	}	
}
