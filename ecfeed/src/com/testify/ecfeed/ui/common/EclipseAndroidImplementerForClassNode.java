/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.android.project.AndroidManifestAccessor;
import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.ClassNode;

public class EclipseAndroidImplementerForClassNode implements IImplementer {

	private EclipseLoggerClassImplementer fLoggerClassImplementer;
	private EclipseRunnerClassImplementer fRunnerClassImplementer;
	private EclipseTestClassImplementer fTestClassImplementer;
	private EclipseAndroidManifestImplementer fAndroidManifestImplementer;
	private EclipseAndroidToolsImplementer fAndroidToolsImplementer;

	EclipseAndroidImplementerForClassNode(
			IFileInfoProvider fileInfoProvider,
			ClassNode classNode) throws EcException {

		String projectPath = EclipseProjectHelper.getProjectPath(fileInfoProvider);

		AndroidManifestAccessor androidManifestReader = 
				new AndroidManifestAccessor(projectPath);

		String testingAppPackage = androidManifestReader.getTestingAppPackage();

		createLoggerClassImplementer(fileInfoProvider, testingAppPackage);
		createRunnerClassImplementer(fileInfoProvider, testingAppPackage, classNode);
		createTestClassImplementer(fileInfoProvider, testingAppPackage, classNode, androidManifestReader);
		createAndroidManifestImplementer(fileInfoProvider);
		createEclipseAndroidToolsImplementer(fileInfoProvider);
	}

	private void createAndroidManifestImplementer(IFileInfoProvider fileInfoProvider) throws EcException {
		fAndroidManifestImplementer =
				new EclipseAndroidManifestImplementer(fileInfoProvider);
	}

	private void createLoggerClassImplementer(
			IFileInfoProvider fileInfoProvider, 
			String testingAppPackage) throws EcException {

		fLoggerClassImplementer = 
				new	EclipseLoggerClassImplementer(fileInfoProvider, testingAppPackage);
	}

	private void createRunnerClassImplementer(
			IFileInfoProvider fileInfoProvider,
			String testingAppPackage,
			ClassNode classNode) throws EcException {

		String baseRunner = classNode.getAndroidBaseRunner();

		fRunnerClassImplementer = 
				new EclipseRunnerClassImplementer(
						fileInfoProvider, 
						testingAppPackage,
						baseRunner);
	}

	private void createTestClassImplementer(
			IFileInfoProvider fileInfoProvider,
			String testingAppPackage,
			ClassNode classNode, 
			AndroidManifestAccessor androidManifestReader) throws EcException {

		String testingAppClass = "EcFeedTest";
		String testedAppPackage = androidManifestReader.getDefaultTestedAppPackage();

		if (testedAppPackage == null) {
			EcException.report(Messages.DEFAULT_PACKAGE_NOT_SET_IN_ANDROID_MANIFEST);
		}

		fTestClassImplementer = 
				new	EclipseTestClassImplementer(
						fileInfoProvider, 
						testingAppPackage,
						testingAppClass,
						"ActivityInstrumentationTestCase2",
						testedAppPackage,
						"MainActivity");
	}

	private void createEclipseAndroidToolsImplementer(IFileInfoProvider fileInfoProvider) throws EcException {
		fAndroidToolsImplementer = new EclipseAndroidToolsImplementer(fileInfoProvider);
	}

	public void implementContent() throws EcException {
		if (!fLoggerClassImplementer.contentImplemented()) {
			fLoggerClassImplementer.implementContent();
		}
		if (!fRunnerClassImplementer.contentImplemented()) {
			fRunnerClassImplementer.implementContent();
		}
		if (!fTestClassImplementer.contentImplemented()) {
			fTestClassImplementer.implementContent();
		}
		if (!fAndroidManifestImplementer.contentImplemented()) {
			fAndroidManifestImplementer.implementContent();
		}
		if (!fAndroidToolsImplementer.contentImplemented()) {
			fAndroidToolsImplementer.implementContent();
		}
	}

	public boolean contentImplemented() throws EcException {
		if (!fLoggerClassImplementer.contentImplemented()) {
			return false;
		}
		if (!fRunnerClassImplementer.contentImplemented()) {
			return false;
		}
		if (!fTestClassImplementer.contentImplemented()) {
			return false;
		}
		if (!fAndroidManifestImplementer.contentImplemented()) {
			return false;
		}		
		if (!fAndroidToolsImplementer.contentImplemented()) {
			return false;
		}
		return true;
	}	
}
