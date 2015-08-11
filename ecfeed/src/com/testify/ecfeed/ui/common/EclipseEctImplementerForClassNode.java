/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.android.project.AndroidManifestAccessor;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.utils.PackageClassHelper;
import com.testify.ecfeed.utils.ProjectHelper;

public class EclipseEctImplementerForClassNode {

	EclipseLoggerClassImplementer fLoggerClassImplementer;
	EclipseRunnerClassImplementer fRunnerClassImplementer;
	EclipseTestClassImplementer fTestClassImplementer;
	EclipseAndroidManifestImplementer fAndroidManifestImplementer; 

	EclipseEctImplementerForClassNode(
			IFileInfoProvider fileInfoProvider,
			ClassNode classNode) {

		AndroidManifestAccessor androidManifestReader = 
				new AndroidManifestAccessor(ProjectHelper.getProjectPath(fileInfoProvider));

		String testingAppPackage = androidManifestReader.getTestingAppPackage();

		createLoggerClassImplementer(fileInfoProvider, testingAppPackage);
		createRunnerClassImplementer(fileInfoProvider, testingAppPackage, classNode);
		createTestClassImplementer(fileInfoProvider, testingAppPackage, classNode, androidManifestReader);
		createAndroidManifestImplementer(fileInfoProvider);
	}

	private void createAndroidManifestImplementer(IFileInfoProvider fileInfoProvider) {
		fAndroidManifestImplementer =
				new EclipseAndroidManifestImplementer(fileInfoProvider);
	}

	private void createLoggerClassImplementer(
			IFileInfoProvider fileInfoProvider, 
			String testingAppPackage) {

		fLoggerClassImplementer = 
				new	EclipseLoggerClassImplementer(fileInfoProvider, testingAppPackage);
	}

	private void createRunnerClassImplementer(
			IFileInfoProvider fileInfoProvider,
			String testingAppPackage,
			ClassNode classNode) {

		String baseRunner = classNode.getAndroidBaseRunner();

		fRunnerClassImplementer = 
				new EclipseRunnerClassImplementer(
						fileInfoProvider, 
						testingAppPackage,
						PackageClassHelper.getPackage(baseRunner),
						PackageClassHelper.getClass(baseRunner));
	}

	private void createTestClassImplementer(
			IFileInfoProvider fileInfoProvider,
			String testingAppPackage,
			ClassNode classNode, 
			AndroidManifestAccessor androidManifestReader) {

		String testingAppClass = "EcFeedTest";
		String testedAppPackage = androidManifestReader.getDefaultTestedAppPackage();

		if (testedAppPackage == null) {
			throw new RuntimeException(Messages.DEFAULT_PACKAGE_NOT_SET_IN_ANDROID_MANIFEST);
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

	void implementContent() {
		try {
			implementContentIntr();
		} catch (CoreException e) {
			MessageDialog.openError(
					Display.getDefault().getActiveShell(), 
					Messages.CAN_NOT_IMPLEMENT_SOURCE_FOR_CLASS, 
					e.getMessage());			

		}
	}

	void implementContentIntr() throws CoreException {
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
	}

	boolean contentImplemented() {
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
		return true;
	}	
}
