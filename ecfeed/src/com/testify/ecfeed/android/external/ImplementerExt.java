/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.android.external;

import com.testify.ecfeed.utils.EcException;

public class ImplementerExt {

	final IProjectHelper fProjectHelper;
	final IClassImplementHelper fClassImplementHelper;
	final IInstallationDirFileHelper fInstallationDirFileHelper;
	final String fBaseRunner;

	public ImplementerExt(
			final String baseRunner, 
			final IProjectHelper projectHelper, 
			final IClassImplementHelper classImplementHelper,
			final IInstallationDirFileHelper installationDirFileHelper) {
		fBaseRunner = baseRunner;
		fProjectHelper = projectHelper;
		fClassImplementHelper = classImplementHelper;
		fInstallationDirFileHelper = installationDirFileHelper;
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
		return androidFactory.createCommonImplementer(
				fBaseRunner, fClassImplementHelper, fProjectHelper, fInstallationDirFileHelper);		
	}
}
