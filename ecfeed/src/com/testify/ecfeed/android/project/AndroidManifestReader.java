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
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class AndroidManifestReader {

	Document fDocument;

	public AndroidManifestReader(String projectPath) {

		Builder builder = new Builder();

		try {
			fDocument = builder.build(projectPath + File.separator + "AndroidManifest.xml");
		} catch (ParsingException | IOException e) {
		}
	}

	public String getPackageName() {

		if(fDocument == null) {
			return "";
		}

		return fDocument.getRootElement().getAttributeValue("package");
	}

	public List<String> getRunners() {

		List<String> runnerList = new ArrayList<String>();

		if(fDocument == null) {
			return runnerList;
		}

		addRunnersFromManifest(runnerList);
		return runnerList;
	}	

	private void addRunnersFromManifest(List<String> runnerList) {

		Elements children = fDocument.getRootElement().getChildElements();

		for(int index = 0; index < children.size(); index++) {
			addRunnerConditionally(children.get(index), runnerList);
		}
	}

	private void addRunnerConditionally(Element element, List<String> runnerList) {

		String nodeName = element.getQualifiedName();

		final String instrumentationTag = "instrumentation";
		if (!nodeName.equals(instrumentationTag))
			return;

		String runnerName = getRunnerFromAttributes(element);

		if (runnerName == null) {
			return;
		}

		runnerList.add(runnerName);
	}

	String getRunnerFromAttributes(Element element) {

		int attributeCount = element.getAttributeCount();

		for (int index = 0; index < attributeCount; index++) {

			Attribute attribute = element.getAttribute(index);
			if (attribute.getQualifiedName().equals("android:name")) {
				return attribute.getValue();
			}
		}
		return null;
	}
}
