/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.android.project;

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

class ReadPackageOperation implements IRunnableWithProgress{

	private String fProjectPath;
	private String fPackageName = null;

	ReadPackageOperation(String projectPath){
		fProjectPath = projectPath;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Looking for package name...", 1);
		fPackageName = readPackageName();
		monitor.worked(1);
		monitor.done();
	}

	public String getPackageName() {
		return fPackageName;
	}

	private String readPackageName() {

		Builder builder = new Builder();
		Document document = null;

		try {
			document = builder.build(fProjectPath + File.separator + "AndroidManifest.xml");
		} catch (ParsingException | IOException e) {
			System.out.println("Invalid AndroidManifest.xml");
			return "";
		}

		return document.getRootElement().getAttributeValue("package");
	}
}

public class AndroidManifestReader {

	public static String readPackageName(String projectPath) {

		ReadPackageOperation operation = new ReadPackageOperation(projectPath);

		try {
			ProgressMonitorDialog progressDialog = 
					new ProgressMonitorDialog(Display.getCurrent().getActiveShell());

			progressDialog.setCancelable(false);
			progressDialog.run(true, true, operation);
		} catch (InvocationTargetException | InterruptedException e) {
			return null;
		}

		return operation.getPackageName();
	}
}
