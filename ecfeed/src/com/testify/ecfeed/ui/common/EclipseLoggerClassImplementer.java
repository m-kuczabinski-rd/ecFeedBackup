/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.ui.common.external.IFileInfoProvider;


public class EclipseLoggerClassImplementer extends EclipseProjectSpecificClassImplementer {

	public EclipseLoggerClassImplementer(String testingAppPackage, IFileInfoProvider fileInfoProvider) {
		super(testingAppPackage, "Logger", fileInfoProvider);
	}

	@ Override
	protected String createUnitContent() {
		return
				"package com.mamlambo.article.simplecalc.test.ecFeed.android;\n" +
				"\n" +
				"import android.util.Log;\n" + 
				"import com.testify.ecfeed.android.junit.tools.ILogger;\n" +
				"\n" +
				"public class Logger implements ILogger {\n" +
				"\n" +
				"\tfinal String TAG = \"ecFeed\";\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic void log(String message) {\n" +
				"\t\tLog.d(TAG, message);\n" +
				"\t}\n" + 
				"}\n";
	}
}
