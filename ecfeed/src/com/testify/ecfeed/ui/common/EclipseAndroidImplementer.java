/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import com.testify.ecfeed.android.project.AndroidManifestAccessor;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.utils.ExceptionHelper;

public class EclipseAndroidImplementer implements IExternalImplementer {

	private EclipseLoggerClassImplementer fLoggerClassImplementer;
	private EclipseRunnerClassImplementer fRunnerClassImplementer;
	private EclipseTestClassImplementer fTestClassImplementer;
	private EclipseAndroidManifestImplementer fAndroidManifestImplementer;
	private EclipseAndroidToolsImplementer fAndroidToolsImplementer;

	EclipseAndroidImplementer() {
	}

	private void createAndroidManifestImplementer(IFileInfoProvider fileInfoProvider) {
		fAndroidManifestImplementer =
				new EclipseAndroidManifestImplementer(fileInfoProvider);
	}

	private void createLoggerClassImplementer(
			String testingAppPackage,
			IFileInfoProvider fileInfoProvider) {

		fLoggerClassImplementer = 
				new	EclipseLoggerClassImplementer(testingAppPackage, fileInfoProvider);
	}

	private void createRunnerClassImplementer(
			String testingAppPackage,
			ClassNode classNode,
			IFileInfoProvider fileInfoProvider) {

		String baseRunner = classNode.getAndroidBaseRunner();

		fRunnerClassImplementer = 
				new EclipseRunnerClassImplementer(
						testingAppPackage,
						baseRunner,
						fileInfoProvider);
	}

	private void createTestClassImplementer(
			String testingAppPackage,
			ClassNode classNode, 
			AndroidManifestAccessor androidManifestReader,
			IFileInfoProvider fileInfoProvider) {

		String testingAppClass = "EcFeedTest";
		String testedAppPackage = androidManifestReader.getDefaultTestedAppPackage();

		if (testedAppPackage == null) {
			ExceptionHelper.reportRuntimeException(Messages.DEFAULT_PACKAGE_NOT_SET_IN_ANDROID_MANIFEST);
		}

		fTestClassImplementer = 
				new	EclipseTestClassImplementer(
						testingAppPackage,
						testingAppClass,
						"ActivityInstrumentationTestCase2",
						testedAppPackage,
						"MainActivity",
						fileInfoProvider);
	}

	private void createEclipseAndroidToolsImplementer(IFileInfoProvider fileInfoProvider) {
		fAndroidToolsImplementer = new EclipseAndroidToolsImplementer(fileInfoProvider);
	}

	@Override
	public void implementContent() {
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

	@Override
	public boolean contentImplemented() {
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

	@Override
	public void initialize(ClassNode classNode, IFileInfoProvider fileInfoProvider) {
		
		String projectPath = EclipseProjectHelper.getProjectPath(fileInfoProvider);

		AndroidManifestAccessor androidManifestReader = 
				new AndroidManifestAccessor(projectPath);

		String testingAppPackage = androidManifestReader.getTestingAppPackage();

		createLoggerClassImplementer(testingAppPackage, fileInfoProvider);
		createRunnerClassImplementer(testingAppPackage, classNode, fileInfoProvider);
		createTestClassImplementer(testingAppPackage, classNode, androidManifestReader, fileInfoProvider);
		createAndroidManifestImplementer(fileInfoProvider);
		createEclipseAndroidToolsImplementer(fileInfoProvider);
	}	
}
