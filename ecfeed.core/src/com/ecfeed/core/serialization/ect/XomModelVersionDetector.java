/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.core.serialization.ect;

import static com.ecfeed.core.serialization.ect.Constants.ROOT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.VERSION_ATTRIBUTE;
import nu.xom.Element;

import com.testify.ecfeed.core.serialization.ParserException;

public class XomModelVersionDetector {

	public static int getVersion(Element element) throws ParserException {
		String qualifiedName = element.getQualifiedName(); 

		if(qualifiedName.equals(ROOT_NODE_NAME) == false){
			ParserException.report("Unexpected root node name: " + qualifiedName);
		}

		String versionStr = element.getAttributeValue(VERSION_ATTRIBUTE);
		if(versionStr == null){
			return 0;
		}			

		return convertVersion(versionStr);
	}

	private static int convertVersion(String versionStr) throws ParserException {
		int version = 0;
		try {
			version = Integer.parseInt(versionStr);
		} catch (NumberFormatException e) {
			reportInvalidVersionException();
		}

		if (version < 0) {
			reportInvalidVersionException();
		}
		return version;
	}

	private static void reportInvalidVersionException() throws ParserException {
		ParserException.report("Invalid version of model.");
	}
}
