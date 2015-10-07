/*******************************************************************************
 * Copyright (c) 2015 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.ui.common.utils;

import com.testify.ecfeed.android.external.IClassImplementHelper;
import com.testify.ecfeed.android.external.IImplementerExt;

public abstract class ClassImplementer implements IImplementerExt {

	private String fPackage;
	private String fClassNameWithoutExtension;
	private IClassImplementHelper fClassImplementHelper;

	public ClassImplementer(
			final String thePackage, 
			final String classNameWithoutExtension,
			final IClassImplementHelper classImplementHelper) {
		fPackage = thePackage; 
		fClassNameWithoutExtension = classNameWithoutExtension;
		fClassImplementHelper = classImplementHelper;
	}

	abstract protected String createUnitContent();

	public void implementContent() {
		fClassImplementHelper.implementClass(fPackage, fClassNameWithoutExtension, createUnitContent());
	}

	public boolean contentImplemented() {
		return fClassImplementHelper.classImplemented(fPackage, fClassNameWithoutExtension);
	}

	protected boolean classImplemented(String superclassName) {
		return fClassImplementHelper.classImplemented(fPackage, fClassNameWithoutExtension, superclassName);
	}
}
