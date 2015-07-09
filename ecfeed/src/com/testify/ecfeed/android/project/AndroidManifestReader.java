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

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

public class AndroidManifestReader {

	public static String readPackageName(String projectPath) {

		Builder builder = new Builder();
		Document document = null;

		try {
			document = builder.build(projectPath + File.separator + "AndroidManifest.xml");
		} catch (ParsingException | IOException e) {
			System.out.println("Invalid AndroidManifest.xml");
			return "";
		}

		return document.getRootElement().getAttributeValue("package");
	}
}
