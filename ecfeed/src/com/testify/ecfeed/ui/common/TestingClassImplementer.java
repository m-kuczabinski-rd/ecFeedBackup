/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import org.eclipse.core.runtime.CoreException;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.android.utils.AndroidBaseRunnerHelper;
import com.testify.ecfeed.android.utils.AndroidManifestAccessor;
import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.common.external.IClassImplementHelper;

public class TestingClassImplementer {

	public void implementClassDefinition(
			ClassNode node, String projectPath, IClassImplementHelper implementHelper) 
			throws CoreException, EcException {
		String packageName = JavaUtils.getPackageName(node.getName());
		String className = JavaUtils.getLocalName(node.getName());

		AndroidManifestAccessor androidManifestAccesor = 
				new AndroidManifestAccessor(projectPath);
 
		String testingAppPackage = androidManifestAccesor.getTestingAppPackage();
		String classContent = classDefinitionContent(node, packageName, testingAppPackage);

		implementHelper.implementClass(packageName, className, classContent);
	}
	
	private String classDefinitionContent(ClassNode classNode, String userPackageName, String testingAppPackage){
		StringBuilder contentBuilder = new  StringBuilder();

		contentBuilder.append("package " + userPackageName + ";\n\n");

		if (classNode.getRunOnAndroid()) {
			contentBuilder.append(
					"import " + testingAppPackage + "." + 
							AndroidBaseRunnerHelper.getEcFeedAndroidPackagePrefix() + ".EcFeedTest;\n\n");
		}

		contentBuilder.append("public class " + JavaUtils.getLocalName(classNode) + " ");

		if (classNode.getRunOnAndroid()) {
			contentBuilder.append("extends EcFeedTest ");
		}		

		contentBuilder.append("{\n\n");

		if (classNode.getRunOnAndroid()) {
			contentBuilder.append("\t@Override\n\tprotected void setUp() throws Exception {\n\t\tsuper.setUp();\n\t}\n");
		}

		contentBuilder.append("\n}");

		return contentBuilder.toString();
	}
	
}
