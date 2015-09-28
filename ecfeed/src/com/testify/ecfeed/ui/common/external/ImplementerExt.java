/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;

import com.testify.ecfeed.generators.api.EcException;

public class ImplementerExt {

	public static boolean contentImplemented(String baseRunner, IFileInfoProvider fileInfoProvider) throws EcException {
		IImplementerExt implementer = createImplementer(baseRunner, fileInfoProvider);
		return implementer.contentImplemented();
	}

	public static void implementContent(String baseRunner, IFileInfoProvider fileInfoProvider) throws EcException {
		IImplementerExt implementer = createImplementer(baseRunner, fileInfoProvider);
		implementer.implementContent();
	}

	private static IImplementerExt createImplementer(String baseRunner, IFileInfoProvider fileInfoProvider) {
		IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();
		return androidFactory.createImplementer(baseRunner, fileInfoProvider);		
	}
}
