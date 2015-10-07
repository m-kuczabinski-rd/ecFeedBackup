/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.android.external;

public class AndroidUserClassImplementerExt {

	public static void implementContent(String projectPath,	String thePackage, 
			String classNameWithoutExtension, IClassImplementHelper classImplementHelper) {

		final IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();

		IImplementerExt implementer = 
				androidFactory.createUserClassImplementer(
						projectPath, thePackage, classNameWithoutExtension, classImplementHelper);

		implementer.implementContent();
	}
}
