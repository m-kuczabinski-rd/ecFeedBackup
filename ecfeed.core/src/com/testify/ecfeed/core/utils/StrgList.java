/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.core.utils;

import java.util.ArrayList;
import java.util.List;

public class StrgList {

	List<String> fStrgs = new ArrayList<String>();

	public boolean isEmpty() {
		return fStrgs.isEmpty();
	}

	public void add(String strg) {
		fStrgs.add(strg);
	}

	public String contentsToMultilineString() {
		StringBuilder stringBuilder = new StringBuilder();

		boolean firstElement = true;

		for (String strg : fStrgs) {

			if (!firstElement) {
				stringBuilder.append("\n");
			}

			firstElement = false;

			stringBuilder.append(strg);
		}

		return stringBuilder.toString();
	}


}
