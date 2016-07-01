/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.android.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class AndroidManifestAccessor {

	private class Runner {

		private String fName;
		private String fTargetPackage;

		public Runner(String name, String targetPackage) {
			fName = name;
			fTargetPackage = targetPackage;
		}

		@Override
		public boolean equals(Object object) {
			Runner runner = (Runner)object;

			if (!fName.equals(runner.fName)) {
				return false;
			}
			if (!fTargetPackage.equals(runner.fTargetPackage)) {
				return false;
			}
			return true;
		}
	}

	private static final String ATTRIBUTE_ANDROID = "android";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_TARGET_PACKAGE = "targetPackage";
	private static final String ATTRIBUTE_PACKAGE = "package";
	private static final String ATTRIBUTE_VERSION_CODE = "versionCode";
	private static final String ATTRIBUTE_VERSION_NAME = "versionName";
	private static final String ATTRIBUTE_SEPARATOR = ":";
	private static final String NODE_NAME_INSTRUMENTATION = "instrumentation";
	private static final String NODE_NAME_APPLICATION = "application";
	private static final String NODE_NAME_ACTIVITY = "activity";
	private static final String NODE_NAME_ACTION = "action";
	private static final String NODE_NAME_INTENT_FILTER = "intent-filter";
	private static final String ANDROID_INTENT_ACTION_MAIN = "android.intent.action.MAIN";
	private static final String ANDROID_MANIFEST_FILE = "AndroidManifest.xml";
	private static final String MSG_NOT_FOUND_IN_PATH = " not found in path: ";

	private String fManifestPath;
	private Document fDocument;
	private Element fRootElement;

	public AndroidManifestAccessor(String projectPath) {
		fManifestPath = createManifestQualifiedName(projectPath);

		if(!DiskFileHelper.fileExists(fManifestPath)) {
			ExceptionHelper.reportRuntimeException(ANDROID_MANIFEST_FILE + MSG_NOT_FOUND_IN_PATH + projectPath);
		}		

		Builder builder = new Builder();

		try {
			fDocument = builder.build(fManifestPath);
		} catch (ParsingException | IOException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}

		fRootElement = fDocument.getRootElement();
	}

	private static String createManifestQualifiedName(String projectPath) {
		return DiskFileHelper.joinPathWithFile(projectPath, ANDROID_MANIFEST_FILE);
	}

	public String getTestingAppPackage() {
		return fRootElement.getAttributeValue(ATTRIBUTE_PACKAGE);
	}

	public String getDefaultTestedAppPackage() {
		Elements children = fRootElement.getChildElements();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if(!isInstrumentationNode(element)) {
				continue;
			}

			return getQualifiedAttribute(element, ATTRIBUTE_ANDROID,
					ATTRIBUTE_TARGET_PACKAGE);
		}
		return null;
	}

	public List<String> getRunnerNames() {
		List<String> names = new ArrayList<String>();

		if (fDocument == null) {
			return names;
		}

		addRunnerNamesFromManifest(names);
		return names;
	}

	public List<Runner> getRunners() {
		List<Runner> runners = new ArrayList<Runner>();

		if (fDocument == null) {
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

		Element newRunnerElement = new Element(NODE_NAME_INSTRUMENTATION);
		String namespaceURI = getNamespaceURI();

		addAttributeTo(newRunnerElement, namespaceURI, ATTRIBUTE_NAME,
				runnerName);
		addAttributeTo(newRunnerElement, namespaceURI,
				ATTRIBUTE_TARGET_PACKAGE, targetPackage);

		fRootElement.insertChild(newRunnerElement,
				getLastInstrumentationNodeIndex() + 1);
		writeDocument();
	}

	private String getNamespaceURI() {
		String attributeURI = getAttributeURI(fRootElement, ATTRIBUTE_ANDROID, ATTRIBUTE_VERSION_CODE);

		if (attributeURI != null) {
			return attributeURI; 
		}

		return getAttributeURI(fRootElement, ATTRIBUTE_ANDROID, ATTRIBUTE_VERSION_NAME);
	}

	private void addAttributeTo(Element element, String namespaceURI,
			String name, String value) {
		Attribute attr = new Attribute(ATTRIBUTE_ANDROID + ATTRIBUTE_SEPARATOR + name, namespaceURI, value);
		element.addAttribute(attr);
	}

	private int getLastInstrumentationNodeIndex() {
		int lastInstrumentationIndex = -1;
		Elements children = fRootElement.getChildElements();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if (isInstrumentationNode(element)) {
				lastInstrumentationIndex = index;
			}
		}
		return lastInstrumentationIndex;
	}

	private void writeDocument() {
		File file = new File(fManifestPath);

		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			ExceptionHelper.reportRuntimeException(
					Messages.CAN_NOT_OPEN_ANDROID_MANIFEST(fManifestPath));
		}

		Serializer serializer = new Serializer(stream);
		serializer.setIndent(4);
		try {
			serializer.write(fDocument);
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException(
					Messages.CAN_NOT_SERIALIZE_TO_ANDROID_MANIFEST(fManifestPath));
		}
	}

	private void addRunnerNamesFromManifest(List<String> runnerNames) {
		Elements children = fRootElement.getChildElements();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if (isInstrumentationNode(element)) {
				addRunnerName(element, runnerNames);
			}
		}
	}

	private void addRunnerName(Element element, List<String> runnerNames) {
		String runnerName = 
				getQualifiedAttribute(element, ATTRIBUTE_ANDROID, ATTRIBUTE_NAME);

		if (runnerName == null) {
			return;
		}
		runnerNames.add(runnerName);
	}

	private void addRunnersFromManifest(List<Runner> runners) {
		Elements children = fRootElement.getChildElements();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if (isInstrumentationNode(element)) {
				addRunner(element, runners);
			}
		}
	}

	private void addRunner(Element element, List<Runner> runners) {
		String runnerName = 
				getQualifiedAttribute(element, ATTRIBUTE_ANDROID, ATTRIBUTE_NAME);
		String targetPackage = 
				getQualifiedAttribute(element, ATTRIBUTE_ANDROID, ATTRIBUTE_TARGET_PACKAGE);

		if (runnerName == null || targetPackage == null) {
			return;
		}
		runners.add(new Runner(runnerName, targetPackage));
	}

	private boolean isInstrumentationNode(Element element) {
		String nodeName = element.getQualifiedName();

		if (nodeName.equals(NODE_NAME_INSTRUMENTATION)) {
			return true;
		}
		return false;
	}

	private String getQualifiedAttribute(
			Element element, String namespace, String name) {

		int attributeCount = element.getAttributeCount();

		for (int index = 0; index < attributeCount; index++) {
			Attribute attribute = element.getAttribute(index);

			if (hasCompatibleName(attribute, namespace, name)) {
				return attribute.getValue();
			}
		}
		return null;
	}

	private String getAttributeURI(Element element, String namespace, String name) {
		int attributeCount = element.getAttributeCount();

		for (int index = 0; index < attributeCount; index++) {
			Attribute attribute = element.getAttribute(index);

			if (hasCompatibleName(attribute, namespace, name)) {
				return attribute.getNamespaceURI();
			}
		}
		return null;
	}

	private boolean hasCompatibleName(Attribute attribute, String namespace, String name) {
		String namespacePrefix = attribute.getNamespacePrefix();
		if (!namespace.equals(namespacePrefix)) {
			return false;
		}

		String localName = attribute.getLocalName();
		if (!localName.equals(name)) {
			return false;
		}
		return true;
	}

	public String getMainActivityClassName() {
		return StringHelper.removePrefix(".", getMainActivityValue());
	}

	public String getMainActivityValue() {
		final Elements children = fRootElement.getChildElements();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if(!isApplicationNode(element)) {
				continue;
			}
			return getActivityForApplicationElement(element); 
		}
		return null;
	}

	private String getActivityForApplicationElement(final Element applicationElement) {
		final Elements children = applicationElement.getChildElements();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if(!isActivityNode(element)) {
				continue;
			}
			if (isMainActivity(element)) {
				return getClassNameForActivity(element);
			}
		}
		return null;
	}

	private boolean isMainActivity(final Element activityElement) {
		final Elements children = activityElement.getChildElements();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if(!isIntentFilterNode(element)) {
				continue;
			}
			if (hasChildActionMain(element)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasChildActionMain(final Element intentFilterElement) {
		final Elements children = intentFilterElement.getChildElements();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if(!isActionNode(element)) {
				continue;
			}
			String attributeValue = getQualifiedAttribute(element, ATTRIBUTE_ANDROID, ATTRIBUTE_NAME);

			if (isActionMainAttribute(attributeValue)) {
				return true;
			}
		}
		return false;
	}

	private String getClassNameForActivity(final Element element) {
		return getQualifiedAttribute(element, ATTRIBUTE_ANDROID, ATTRIBUTE_NAME);
	}

	private boolean isActionMainAttribute(final String attributeValue) {
		if (attributeValue.equals(ANDROID_INTENT_ACTION_MAIN)) {
			return true;
		}
		return false;
	}

	private static boolean isApplicationNode(final Element element) {
		return isNode(element, NODE_NAME_APPLICATION);
	}

	private static boolean isActivityNode(final Element element) {
		return isNode(element, NODE_NAME_ACTIVITY);
	}	

	private static boolean isActionNode(final Element element) {
		return isNode(element, NODE_NAME_ACTION);
	}	

	private static boolean isIntentFilterNode(final Element element) {
		return isNode(element, NODE_NAME_INTENT_FILTER);
	}	

	private static boolean isNode(final Element element, final String nodeName) {
		String currentNodeName = element.getQualifiedName();

		if (currentNodeName.equals(nodeName)) {
			return true;
		}
		return false;
	}	
}
