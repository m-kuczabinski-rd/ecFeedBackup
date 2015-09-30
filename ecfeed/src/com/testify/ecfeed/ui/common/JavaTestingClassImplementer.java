/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.android.utils.AndroidManifestAccessor;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.common.external.IClassImplementHelper;
import com.testify.ecfeed.ui.common.utils.ClassImplementer;

public class JavaTestingClassImplementer extends ClassImplementer {

	ClassNode fClassNode;
	String fUserPackageName;
	String fTestingAppPackage;

	public JavaTestingClassImplementer(
			ClassNode classNode,
			String projectPath,
			IClassImplementHelper classImplementHelper) {
		super(JavaUtils.getPackageName(classNode.getName()), 
				JavaUtils.getLocalName(classNode.getName()), 
				classImplementHelper);
		fClassNode = classNode;
		fUserPackageName = JavaUtils.getPackageName(classNode.getName());

		AndroidManifestAccessor androidManifestAccesor = 
				new AndroidManifestAccessor(projectPath);

		fTestingAppPackage = androidManifestAccesor.getTestingAppPackage();
	}

	@Override
	protected String createUnitContent() {
		StringBuilder contentBuilder = new  StringBuilder();

		contentBuilder.append("package " + fUserPackageName + ";\n\n");

		contentBuilder.append(createImportLine());

		contentBuilder.append("public class " + JavaUtils.getLocalName(fClassNode) + " ");

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
