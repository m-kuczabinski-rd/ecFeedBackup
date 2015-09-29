/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;

import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.common.utils.EclipseProjectHelper;

public class ImplementerExt {

	public static boolean contentImplemented(
			final String baseRunner, final IFileInfoProvider fileInfoProvider) throws EcException {
		final IImplementerExt implementer = createImplementer(baseRunner, fileInfoProvider);
		return implementer.contentImplemented();
	}

	public static void implementContent(
			final String baseRunner, final IFileInfoProvider fileInfoProvider) throws EcException {
		IImplementerExt implementer = createImplementer(baseRunner, fileInfoProvider);
		implementer.implementContent();
	}

	private static IImplementerExt createImplementer(final String baseRunner, final IFileInfoProvider fileInfoProvider) {
		final IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();
		final String projectPath = EclipseProjectHelper.getProjectPath(fileInfoProvider);
		final IClassImplementHelper classImplementHelper = new EclipseClassImplementHelper(fileInfoProvider);
		return androidFactory.createImplementer(baseRunner, projectPath, classImplementHelper);		
	}
}
