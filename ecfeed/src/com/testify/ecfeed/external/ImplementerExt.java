/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.external;

import com.testify.ecfeed.utils.EcException;

public class ImplementerExt {

	final IProjectHelper fProjectHelper;
	final IClassImplementHelper fClassImplementHelper;
	final String fBaseRunner;

	public ImplementerExt(
			final String baseRunner, 
			final IProjectHelper projectHelper, 
			final IClassImplementHelper classImplementHelper) {
		fBaseRunner = baseRunner;
		fProjectHelper = projectHelper;
		fClassImplementHelper = classImplementHelper;
	}

	public boolean contentImplemented() throws EcException {
		final IImplementerExt implementer = createImplementer();
		return implementer.contentImplemented();
	}

	public void implementContent() throws EcException {
		IImplementerExt implementer = createImplementer();
		implementer.implementContent();
	}

	private IImplementerExt createImplementer() {
		final IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();
		final String projectPath = fProjectHelper.getProjectPath();
		return androidFactory.createCommonImplementer(fBaseRunner, projectPath, fClassImplementHelper);		
	}
}
