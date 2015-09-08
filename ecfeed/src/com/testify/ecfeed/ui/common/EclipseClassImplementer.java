/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.external.IImplementer;
import com.testify.ecfeed.utils.ExceptionHelper;

public abstract class EclipseClassImplementer implements IImplementer {

	private IFileInfoProvider fFileInfoProvider;
	private String fPackage;
	private String fClassNameWithoutExtension;

	public EclipseClassImplementer(
			String argPackage, 
			String classNameWithoutExtension,
			IFileInfoProvider fileInfoProvider) {
		if (fileInfoProvider == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}
		fFileInfoProvider = fileInfoProvider;
		fPackage = argPackage; 
		fClassNameWithoutExtension = classNameWithoutExtension;
	}

	abstract protected String createUnitContent();

	public void implementContent() {
		EclipseClassImplementHelper.implementClass(
				fPackage, fClassNameWithoutExtension, createUnitContent(), fFileInfoProvider);
	}

	public boolean contentImplemented() {
		return EclipseClassImplementHelper.classImplemented(fPackage, fClassNameWithoutExtension);
	}

	protected boolean classImplemented(String superclassName) {
		return EclipseClassImplementHelper.classImplemented(fPackage, fClassNameWithoutExtension, superclassName);
	}
}
