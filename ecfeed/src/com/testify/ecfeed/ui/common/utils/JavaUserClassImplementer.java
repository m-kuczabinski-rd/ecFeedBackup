/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.utils;

import com.testify.ecfeed.ui.common.external.IClassImplementHelper;

public class JavaUserClassImplementer extends ClassImplementer {

	private String fPackage;
	private String fClassNameWithoutExtension;

	public JavaUserClassImplementer(
			String projectPath,
			String thePackage,
			String classNameWithoutExtension,
			IClassImplementHelper classImplementHelper) {
		super(thePackage, classNameWithoutExtension, classImplementHelper);
		fPackage = thePackage;
		fClassNameWithoutExtension = classNameWithoutExtension; 
	}

	@Override
	protected String createUnitContent() {
		StringBuilder contentBuilder = new  StringBuilder();

		contentBuilder.append("package " + fPackage + ";\n\n");

		contentBuilder.append(createImportLine());

		contentBuilder.append("public class " + fClassNameWithoutExtension + " ");

		contentBuilder.append(createExtendsClause());

		contentBuilder.append("{\n\n");

		contentBuilder.append(createSetupMethod());

		contentBuilder.append("\n}");

		return contentBuilder.toString();
	}

	protected String createImportLine() {
		return "";
	}

	protected String createExtendsClause() {
		return "";
	}

	protected String createSetupMethod() {
		return "";
	}
}
