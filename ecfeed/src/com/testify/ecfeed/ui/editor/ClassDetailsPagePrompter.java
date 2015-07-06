/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

class ReadDefaultRunnerOperation implements IRunnableWithProgress{

	final static String fEcFeedTestRunner = "EcFeedTestRunner";
	private String fProjectPath;
	private String fRunner = null;

	ReadDefaultRunnerOperation(String projectPath){
		fProjectPath = projectPath;
	}

	@Override
	public void run(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {

		monitor.beginTask("Looking for appropriate runner...", 1);
		fRunner = getDefaultRunner();
		monitor.worked(1);
		monitor.done();
	}

	public String getRunner() {
		return fRunner;
	}

	private String getDefaultRunner() {

		final String ecFeedTestRunner = "com.testify.ecfeed.android.junit.EcFeedTestRunner";
		Builder builder = new Builder();
		Document document = null;

		try {
			document = builder.build(fProjectPath + File.separator + "AndroidManifest.xml");
		} catch (ParsingException | IOException e) {
			System.out.println("Invalid AndroidManifest.xml");
			return "";
		}

		String packageName = document.getRootElement().getAttributeValue("package");

		return packageName + "/" + ecFeedTestRunner;
	}
}

public class ClassDetailsPagePrompter {

	public static String getDefaultAndroidRunner(String projectPath) {

		ReadDefaultRunnerOperation operation = new ReadDefaultRunnerOperation(projectPath);

		try {
			ProgressMonitorDialog progressDialog = 
					new ProgressMonitorDialog(Display.getCurrent().getActiveShell());

			progressDialog.setCancelable(false);
			progressDialog.run(true, true, operation);
		} catch (InvocationTargetException | InterruptedException e) {
			return null;
		}

		return operation.getRunner();
	}
}
