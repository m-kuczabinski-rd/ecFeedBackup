/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.android.external;

import com.ecfeed.core.model.MethodNode;

public class AndroidMethodImplementerExt { 

	public static IImplementerExt createImplementer(
			final MethodNode methodNode, final IMethodImplementHelper methodImplementHelper) {
		final IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();
		return androidFactory.createAndroidMethodImplementer(methodNode, methodImplementHelper);
	}
}

