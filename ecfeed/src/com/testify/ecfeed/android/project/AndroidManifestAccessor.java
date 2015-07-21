/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
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

import com.testify.ecfeed.runner.Messages;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class AndroidManifestAccessor {

	private class Runner {

		@SuppressWarnings("unused")
		String fName;

		@SuppressWarnings("unused")
		String fTargetPackage;

		public Runner(String name, String targetPackage) {
			fName = name;
			fTargetPackage = targetPackage;
		}
	}	

	final String fNameTag = "android:name";
	final String fTargetPackageTag = "android:targetPackage";

	Document fDocument;
	Element fRoot;

	public AndroidManifestAccessor(String projectPath) {

		Builder builder = new Builder();

		try {
			fDocument = builder.build(projectPath + File.separator + "AndroidManifest.xml");
		} catch (ParsingException | IOException e) {
		}
		if (fDocument == null) {
			throw new RuntimeException(Messages.INVALID_ANDROID_MANIFEST_NO_ROOT());
		}

		fRoot = fDocument.getRootElement();
	}

	public String getTestingAppPackage() {

		return fRoot.getAttributeValue("package");
	}

	public String getDefaultTestedAppPackage() {

		Elements children = fRoot.getChildElements();

		for(int index = 0; index < children.size(); index++) {

			Element element = children.get(index);

			if (!isInstrumentationNode(element)) {
				continue;
			}
			return getAttribute(element, fTargetPackageTag);
		}
		return null;
	}

	public List<String> getRunnerNames() {

		List<String> names = new ArrayList<String>();

		if(fDocument == null) {
			return names;
		}

		addRunnerNamesFromManifest(names);
		return names;
	}	

	public List<Runner> getRunners() {

		List<Runner> runners = new ArrayList<Runner>();

		if(fDocument == null) {
			return runners;
		}

		addRunnersFromManifest(runners);
		return runners;
	}	

	public boolean containsRunner(String runnerName) {

		List<String> names = getRunnerNames();

		if (names.contains(runnerName)) {
			return true;
		}
		return false;
	}

	public boolean containsRunner(String runnerName, String targetPackage) {

		List<Runner> runners = getRunners();

		if (runners.contains(new Runner(runnerName, targetPackage))) {
			return true;
		}
		return false;
	}

	public void supplementRunner(String runnerName, String targetPackage) {

		if (containsRunner(runnerName, targetPackage)) {
			return;
		}

		Element newRunner = new Element("instrumentation");

		addAttribute(fNameTag, runnerName, newRunner);
		addAttribute(fTargetPackageTag, targetPackage, newRunner);

		fRoot.appendChild(newRunner);

		fDocument.toXML();
	}

	private void addAttribute(String name, String value, Element element) {
		Attribute attrName = new Attribute(name, value);
		element.addAttribute(attrName);
	}

	private void addRunnerNamesFromManifest(List<String> runnerNames) {

		Elements children = fRoot.getChildElements();

		for(int index = 0; index < children.size(); index++) {

			Element element = children.get(index);

			if (isInstrumentationNode(element)) {
				return;
			}

			addRunnerName(element, runnerNames);
		}
	}

	private void addRunnerName(Element element, List<String> runnerNames) {

		String runnerName = getAttribute(element, fNameTag);

		if (runnerName == null) {
			return;
		}

		runnerNames.add(runnerName);
	}

	private void addRunnersFromManifest(List<Runner> runners) {

		Elements children = fRoot.getChildElements();

		for(int index = 0; index < children.size(); index++) {

			Element element = children.get(index);

			if (isInstrumentationNode(element)) {
				addRunner(element, runners);
			}
		}
	}	

	private void addRunner(Element element, List<Runner> runners) {

		String runnerName = getAttribute(element, fNameTag);
		String targetPackage = getAttribute(element, fTargetPackageTag);

		if (runnerName == null || targetPackage == null) {
			return;
		}

		runners.add(new Runner(runnerName, targetPackage));
	}

	private boolean isInstrumentationNode(Element element) {

		String nodeName = element.getQualifiedName();

		if (nodeName.equals("instrumentation")){
			return true;
		}
		return false;
	}

	private String getAttribute(Element element, String attributeName) {

		int attributeCount = element.getAttributeCount();

		for (int index = 0; index < attributeCount; index++) {

			Attribute attribute = element.getAttribute(index);

			if (attribute.getQualifiedName().equals(attributeName)) {
				return attribute.getValue();
			}
		}
		return null;
	}
}
